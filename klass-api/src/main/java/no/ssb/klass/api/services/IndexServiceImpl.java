package no.ssb.klass.api.services;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.*;
import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
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
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class IndexServiceImpl implements IndexService {

    private static final Logger log = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Value("${klass.env.search.elasticsearch.index:klass}")
    protected String elasticsearchIndex;

    private final ClassificationSeriesRepository classificationRepository;
    private final OpenSearchRestTemplate elasticsearchOperations;
    private final DocumentMapper documentMapper;

    @Autowired
    public IndexServiceImpl(ClassificationSeriesRepository classificationRepository,
                            @Qualifier("opensearchRestTemplate") OpenSearchRestTemplate elasticsearchOperations) {
        this.classificationRepository = classificationRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.documentMapper = new DocumentMapper();
    }

    private IndexCoordinates getIndexCoordinates() {
        return IndexCoordinates.of(elasticsearchIndex);
    }


    @PostConstruct
    private void createIndexWithStemmingAnalyzer() {
        try {
            var indexOps = elasticsearchOperations.indexOps(getIndexCoordinates());

            if (indexOps.exists()) {
                log.info("Index '{}' already exists — skipping creation.", elasticsearchIndex);
                return;
            }

            Map<String, Object> settings = Map.of(
                    "analysis", Map.of(
                            "analyzer", Map.of(
                                    "norwegian_stemmer_analyzer", Map.of(
                                            "type", "custom",
                                            "tokenizer", "standard",
                                            "filter", List.of("lowercase", "norwegian_stemmer")
                                    )
                            ),
                            "filter", Map.of(
                                    "norwegian_stemmer", Map.of(
                                            "type", "stemmer",
                                            "name", "norwegian"
                                    )
                            )
                    )
            );

            Map<String, Object> mappings = Map.of(
                    "properties", Map.of(
                            "title", Map.of(
                                    "type", "text",
                                    "analyzer", "norwegian_stemmer_analyzer"
                            ),
                            "description", Map.of(
                                    "type", "text",
                                    "analyzer", "norwegian_stemmer_analyzer"
                            ),
                            "family", Map.of(
                                    "type", "keyword"
                            ),
                            "section", Map.of(
                                    "type", "keyword"
                            )
                    )
            );

            boolean created = indexOps.create(settings);
            if (created) {
                indexOps.putMapping(Document.from(mappings));
                log.info("Created index '{}' with Norwegian stemming analyzer.", elasticsearchIndex);
            } else {
                log.warn("Failed to create index '{}'", elasticsearchIndex);
            }

        } catch (Exception e) {
            log.error("Error creating index '{}': {}", elasticsearchIndex, e.getMessage(), e);
        }
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

        Arrays.stream(Language.values())
                .filter(language -> !classification.getName(language).isEmpty())
                .forEach(language -> indexLanguage(classification, language));

        elasticsearchOperations.indexOps(getIndexCoordinates()).refresh();
        log.info("Indexing: {} took (ms): {}", classification.getNameInPrimaryLanguage(),
                TimeUtil.millisecondsSince(start));
    }

    private void indexLanguage(ClassificationSeries classification, Language language) {
        Map<String, Object> doc = documentMapper.mapClassificationSeries(classification, language);
        updateElasticsearch(classification, doc);

        classification.getClassificationVersions()
                .forEach(version -> recursiveIndex(version, language));
    }

    private void recursiveIndex(ClassificationVersion version, Language language) {
        Map<String, Object> doc = documentMapper.mapVersion(version, language);
        updateElasticsearch(version, doc);

        indexVariants(version.getClassificationVariants(), language);
        indexCorrespondenceTables(version.getCorrespondenceTables(), language);
    }

    private void indexCorrespondenceTables(List<CorrespondenceTable> correspondenceTables, Language language) {
        correspondenceTables.forEach(table -> {
            Map<String, Object> doc = documentMapper.mapCorrespondenceTable(table, language);
            updateElasticsearch(table, doc);
        });
    }

    private void indexVariants(List<ClassificationVariant> variants, Language language) {
        variants.forEach(variant -> {
            Map<String, Object> doc = documentMapper.mapVariant(variant, language);
            updateElasticsearch(variant, doc);
        });
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

    /**
     * Inner class to handle document mapping from domain entities to search documents
     */
    private static class DocumentMapper {

        public Map<String, Object> mapClassificationSeries(ClassificationSeries classification, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("itemid", classification.getId());
            doc.put("uuid", buildUuid(language, classification.getUuid()));
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
                    .collect(Collectors.toList());
            doc.put("codes", codes);

            return doc;
        }

        public Map<String, Object> mapVersion(ClassificationVersion version, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("itemid", version.getId());
            doc.put("uuid", buildUuid(language, version.getUuid()));
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
                    .collect(Collectors.toList());
            doc.put("codes", codes);

            return doc;
        }

        public Map<String, Object> mapVariant(ClassificationVariant variant, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("itemid", variant.getId());
            doc.put("uuid", buildUuid(language, variant.getUuid()));
            doc.put("language", language.getLanguageCode());
            doc.put("type", "Variant");
            doc.put("title", variant.getFullName(language));
            doc.put("copyrighted", variant.getOwnerClassification().isCopyrighted());
            doc.put("published", variant.isPublished(language));
            doc.put("description", variant.getIntroduction(language));
            doc.put("section", variant.getContactPerson().getSection());

            List<String> codes = variant.getAllClassificationItems().stream()
                    .map(item -> formatClassificationItem(language, item))
                    .collect(Collectors.toList());
            doc.put("codes", codes);

            return doc;
        }

        public Map<String, Object> mapCorrespondenceTable(CorrespondenceTable table, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put("itemid", table.getId());
            doc.put("uuid", buildUuid(language, table.getUuid()));
            doc.put("language", language.getLanguageCode());
            doc.put("type", "Correspondencetable");
            doc.put("title", table.getName(language));
            doc.put("description", table.getDescription(language));
            return doc;
        }

        private String buildUuid(Language language, String baseUuid) {
            return language.getLanguageCode() + "_" + baseUuid;
        }

        private String formatClassificationItem(Language language, ClassificationItem item) {
            return item.getCode() + " - " + item.getOfficialName(language);
        }
    }
}
