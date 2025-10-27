package no.ssb.klass.api.services;

import no.ssb.klass.api.config.OpenSearchConfig;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.*;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.util.TimeUtil;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
@ConditionalOnBean({OpenSearchRestTemplate.class, OpenSearchConfig.class})
public class IndexServiceImpl implements IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Value("${klass.env.search.elasticsearch.index:klass}")
    protected String elasticsearchIndex;

    private final ClassificationSeriesRepository classificationRepository;
    private final OpenSearchRestTemplate elasticsearchOperations;

    @Autowired
    public IndexServiceImpl(ClassificationSeriesRepository classificationRepository,
                            @Qualifier("opensearchRestTemplate") OpenSearchRestTemplate elasticsearchOperations) {
        this.classificationRepository = classificationRepository;
        this.elasticsearchOperations = elasticsearchOperations;
    }

    private IndexCoordinates getIndexCoordinates() {
        return IndexCoordinates.of(elasticsearchIndex);
    }

    @Override
    @Async
    @Transactional(readOnly = true)
    public void indexAsync(Long classificationSeriesId) {
        checkNotNull(classificationSeriesId);
        try {
            ClassificationSeries classification = classificationRepository.getOne(classificationSeriesId);
            indexSync(classification);
        } catch (Exception e) {
            log.warn("Failed to index classification {}: {}", classificationSeriesId, e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void indexSync(ClassificationSeries classification) {
        Date start = TimeUtil.now();
        for (Language language : Language.values()) {
            if (!classification.getName(language).isEmpty()) {
                Map<String, Object> doc = new HashMap<>();
                doc.put("itemid", classification.getId());
                doc.put("uuid", language.getLanguageCode() + "_" + classification.getUuid());
                doc.put("language", language.getLanguageCode());
                doc.put("type", classification.getClassificationType().getDisplayName(Language.EN));
                doc.put("copyrighted", classification.isCopyrighted());
                doc.put("published", classification.isPublished(language));
                doc.put("title", classification.getName(language));
                doc.put("description", classification.getDescription(language));
                doc.put("family", classification.getClassificationFamily().getName(language));
                doc.put("section", classification.getContactPerson().getSection());

                List<String> codes = classification.getClassificationVersions().stream()
                        .flatMap(version -> version.getAllClassificationItems().stream())
                        .map(item -> formatClassificationItem(language, item))
                        .toList();
                doc.put("codes", codes);

                for (ClassificationVersion version : classification.getClassificationVersions()) {
                    recursiveIndex(version, language);
                }

                updateElasticsearch(classification, doc);
            }
        }
        elasticsearchOperations.indexOps(getIndexCoordinates()).refresh();
        log.info("Indexing: {} took (ms): {}", classification.getNameInPrimaryLanguage(),
                TimeUtil.millisecondsSince(start));
    }

    private void recursiveIndex(ClassificationVersion version, Language language) {
        Map<String, Object> doc = new HashMap<>();
        doc.put("itemid", version.getId());
        doc.put("uuid", language.getLanguageCode() + "_" + version.getUuid());
        doc.put("classificationId", version.getClassification().getId());
        doc.put("language", language.getLanguageCode());
        doc.put("type", "Version");
        doc.put("title", version.getName(language));
        doc.put("legalBase", version.getLegalBase(language));
        doc.put("publications", version.getPublications(language));
        doc.put("derivedFrom", version.getDerivedFrom(language));
        doc.put("description", version.getIntroduction(language));
        doc.put("section", version.getContactPerson().getSection());
        doc.put("copyrighted", version.getOwnerClassification().isCopyrighted());
        doc.put("published", version.isPublished(language));

        List<String> codes = version.getAllClassificationItems().stream()
                .map(item -> formatClassificationItem(language, item))
                .toList();
        doc.put("codes", codes);

        indexVariants(version.getClassificationVariants(), language);
        indexCorrespondenceTables(version.getCorrespondenceTables(), language);

        updateElasticsearch(version, doc);
    }

    private void indexCorrespondenceTables(List<CorrespondenceTable> correspondenceTables, Language language) {
        for (CorrespondenceTable correspondenceTable : correspondenceTables) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("itemid", correspondenceTable.getId());
            doc.put("uuid", language.getLanguageCode() + "_" + correspondenceTable.getUuid());
            doc.put("language", language.getLanguageCode());
            doc.put("type", "Correspondencetable");
            doc.put("title", correspondenceTable.getName(language));
            doc.put("description", correspondenceTable.getDescription(language));
            updateElasticsearch(correspondenceTable, doc);
        }
    }

    private void indexVariants(List<ClassificationVariant> variants, Language language) {
        for (ClassificationVariant variant : variants) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("itemid", variant.getId());
            doc.put("uuid", language.getLanguageCode() + "_" + variant.getUuid());
            doc.put("language", language.getLanguageCode());
            doc.put("type", "Variant");
            doc.put("title", variant.getFullName(language));
            doc.put("copyrighted", variant.getOwnerClassification().isCopyrighted());
            doc.put("published", variant.isPublished(language));
            doc.put("description", variant.getIntroduction(language));
            doc.put("section", variant.getContactPerson().getSection());

            List<String> codes = variant.getAllClassificationItems().stream()
                    .map(item -> formatClassificationItem(language, item))
                    .toList();
            doc.put("codes", codes);

            updateElasticsearch(variant, doc);
        }
    }

    private void updateElasticsearch(SoftDeletable entity, Map<String, Object> doc) {
        String uuid = (String) doc.get("uuid");
        if (!entity.isDeleted()) {
            IndexQuery indexQuery = new IndexQueryBuilder()
                    .withId(uuid)
                    .withObject(doc)
                    .build();
            elasticsearchOperations.index(indexQuery, getIndexCoordinates());
        } else {
            elasticsearchOperations.delete(uuid, getIndexCoordinates());
        }
    }

    private String formatClassificationItem(Language language, ClassificationItem item) {
        return item.getCode() + " - " + item.getOfficialName(language);
    }
}
