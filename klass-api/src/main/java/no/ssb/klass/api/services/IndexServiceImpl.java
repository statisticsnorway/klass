package no.ssb.klass.api.services;

import static com.google.common.base.Preconditions.checkNotNull;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.core.document.Document;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Profile("!" + ConfigurationProfiles.MOCK_SEARCH)
public class IndexServiceImpl implements IndexService {

    private static final String ANALYZER = "analyzer";
    private static final String SEARCH_ANALYZER = "search_analyzer";

    private static final String ITEM_ID = "itemid";
    private static final String UUID = "uuid";
    private static final String LANGUAGE = "language";
    private static final String TYPE = "type";
    private static final String TITLE = "title";
    private static final String DESCRIPTION = "description";
    private static final String COPYRIGHTED = "copyrighted";
    private static final String PUBLISHED = "published";
    private static final String SECTION = "section";
    private static final String CODES = "codes";
    private static final String FAMILY = "family";
    private static final String CLASSIFICATION_ID = "classificationId";
    private static final String LEGAL_BASE = "legalBase";
    private static final String PUBLICATIONS = "publications";
    private static final String DERIVED_FROM = "derivedFrom";

    private static final Logger log = LoggerFactory.getLogger(IndexServiceImpl.class);

    @Value("${klass.env.search.elasticsearch.index:klass}")
    protected String elasticsearchIndex;

    private final ClassificationSeriesRepository classificationRepository;
    private final OpenSearchRestTemplate elasticsearchOperations;
    private final DocumentMapper documentMapper;
    private final IndexService indexService;

    @Autowired
    public IndexServiceImpl(
            ClassificationSeriesRepository classificationRepository,
            OpenSearchRestTemplate elasticsearchOperations, IndexService indexService) {
        this.classificationRepository = classificationRepository;
        this.elasticsearchOperations = elasticsearchOperations;
        this.documentMapper = new DocumentMapper();
        this.indexService = indexService;
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


            Map<String, Object> settings =
                    Map.of(
                            "analysis",
                            Map.of(
                                    ANALYZER,
                                            Map.of(
                                                    OpenSearchConfig.NORWEGIAN_STEMMER_ANALYZER,
                                                    Map.of(
                                                            "type", "custom",
                                                            "tokenizer", "standard",
                                                            "filter",
                                                                    List.of(
                                                                            "lowercase",
                                                                            "norwegian_stemmer"))),
                                    "filter",
                                            Map.of(
                                                    "norwegian_stemmer",
                                                    Map.of(
                                                            "type", "stemmer",
                                                            "name", "norwegian"))));

            Map<String, Object> mappings =
                    Map.of(
                            "properties",
                            Map.of(
                                    TITLE,
                                            Map.of(
                                                    "type", "text",
                                                    ANALYZER,
                                                            OpenSearchConfig
                                                                    .NORWEGIAN_STEMMER_ANALYZER,
                                                    SEARCH_ANALYZER,
                                                            OpenSearchConfig
                                                                    .NORWEGIAN_STEMMER_ANALYZER),
                                    DESCRIPTION,
                                            Map.of(
                                                    "type", "text", ANALYZER,
                                                            OpenSearchConfig
                                                                    .NORWEGIAN_STEMMER_ANALYZER,
                                                    SEARCH_ANALYZER,
                                                            OpenSearchConfig
                                                                    .NORWEGIAN_STEMMER_ANALYZER),
                                    CODES,
                                            Map.of(
                                                    "type", "text",
                                                    ANALYZER,
                                                            OpenSearchConfig
                                                                    .NORWEGIAN_STEMMER_ANALYZER,
                                                    "",
                                                            OpenSearchConfig
                                                                    .NORWEGIAN_STEMMER_ANALYZER),
                                    FAMILY, Map.of("type", "keyword"),
                                    SECTION, Map.of("type", "keyword")));

            boolean created = indexOps.create(settings);
            if (created) {
                indexOps.putMapping(Document.from(mappings));
                log.info(
                        "Created index '{}' with Norwegian stemming analyzer.", elasticsearchIndex);
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
            ClassificationSeries classification =
                    classificationRepository.getOne(classificationSeriesId);
            indexService.indexSync(classification);
        } catch (Exception e) {
            log.warn(
                    "Failed to index classification {}: {}",
                    classificationSeriesId,
                    e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void indexSync(ClassificationSeries classification) {
        Date start = TimeUtil.now();

        Arrays.stream(Language.values())
                .filter(language -> !classification.getName(language).isEmpty())
                .forEach(language -> indexLanguage(classification, language));

        elasticsearchOperations.indexOps(getIndexCoordinates());
        log.info(
                "Indexing: {} took (ms): {}",
                classification.getNameInPrimaryLanguage(),
                TimeUtil.millisecondsSince(start));
    }

    private void indexLanguage(ClassificationSeries classification, Language language) {
        Map<String, Object> doc = documentMapper.mapClassificationSeries(classification, language);
        updateElasticsearch(classification, doc);

        classification
                .getClassificationVersions()
                .forEach(version -> recursiveIndex(version, language));
    }

    private void recursiveIndex(ClassificationVersion version, Language language) {
        Map<String, Object> doc = documentMapper.mapVersion(version, language);
        updateElasticsearch(version, doc);

        indexVariants(version.getClassificationVariants(), language);
        indexCorrespondenceTables(version.getCorrespondenceTables(), language);
    }

    private void indexCorrespondenceTables(
            List<CorrespondenceTable> correspondenceTables, Language language) {
        correspondenceTables.forEach(
                table -> {
                    Map<String, Object> doc =
                            documentMapper.mapCorrespondenceTable(table, language);
                    updateElasticsearch(table, doc);
                });
    }

    private void indexVariants(List<ClassificationVariant> variants, Language language) {
        variants.forEach(
                variant -> {
                    Map<String, Object> doc = documentMapper.mapVariant(variant, language);
                    updateElasticsearch(variant, doc);
                });
    }

    private void updateElasticsearch(SoftDeletable entity, Map<String, Object> doc) {
        String uuid = (String) doc.get("uuid");
        if (!entity.isDeleted()) {
            IndexQuery indexQuery = new IndexQueryBuilder().withId(uuid).withObject(doc).build();
            elasticsearchOperations.index(indexQuery, getIndexCoordinates());
        } else {
            elasticsearchOperations.delete(uuid, getIndexCoordinates());
        }
    }

    /** Inner class to handle document mapping from domain entities to search documents */
    private static class DocumentMapper {

        public Map<String, Object> mapClassificationSeries(
                ClassificationSeries classification, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put(ITEM_ID, classification.getId());
            doc.put(UUID, buildUuid(language, classification.getUuid()));
            doc.put(LANGUAGE, language.getLanguageCode());
            doc.put(TYPE, classification.getClassificationType().getDisplayName(Language.EN));
            doc.put(COPYRIGHTED, classification.isCopyrighted());
            doc.put(PUBLISHED, classification.isPublished(language));
            doc.put(TITLE, classification.getName(language));
            doc.put(DESCRIPTION, classification.getDescription(language));
            doc.put(FAMILY, classification.getClassificationFamily().getName(language));
            doc.put(SECTION, classification.getContactPerson().getSection());

            List<String> codes =
                    classification.getClassificationVersions().stream()
                            .flatMap(version -> version.getAllClassificationItems().stream())
                            .map(item -> formatClassificationItem(language, item))
                            .collect(Collectors.toList());
            doc.put(CODES, codes);

            return doc;
        }

        public Map<String, Object> mapVersion(ClassificationVersion version, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put(ITEM_ID, version.getId());
            doc.put(UUID, buildUuid(language, version.getUuid()));
            doc.put(CLASSIFICATION_ID, version.getClassification().getId());
            doc.put(LANGUAGE, language.getLanguageCode());
            doc.put(TYPE, "Version");
            doc.put(TITLE, version.getName(language));
            doc.put(LEGAL_BASE, version.getLegalBase(language));
            doc.put(PUBLICATIONS, version.getPublications(language));
            doc.put(DERIVED_FROM, version.getDerivedFrom(language));
            doc.put(DESCRIPTION, version.getIntroduction(language));
            doc.put(SECTION, version.getContactPerson().getSection());
            doc.put(COPYRIGHTED, version.getOwnerClassification().isCopyrighted());
            doc.put(PUBLISHED, version.isPublished(language));

            List<String> codes =
                    version.getAllClassificationItems().stream()
                            .map(item -> formatClassificationItem(language, item))
                            .collect(Collectors.toList());
            doc.put(CODES, codes);

            return doc;
        }

        public Map<String, Object> mapVariant(ClassificationVariant variant, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put(ITEM_ID, variant.getId());
            doc.put(UUID, buildUuid(language, variant.getUuid()));
            doc.put(LANGUAGE, language.getLanguageCode());
            doc.put(TYPE, "Variant");
            doc.put(TITLE, variant.getFullName(language));
            doc.put(COPYRIGHTED, variant.getOwnerClassification().isCopyrighted());
            doc.put(PUBLISHED, variant.isPublished(language));
            doc.put(DESCRIPTION, variant.getIntroduction(language));
            doc.put(SECTION, variant.getContactPerson().getSection());

            List<String> codes =
                    variant.getAllClassificationItems().stream()
                            .map(item -> formatClassificationItem(language, item))
                            .collect(Collectors.toList());
            doc.put(CODES, codes);

            return doc;
        }

        public Map<String, Object> mapCorrespondenceTable(
                CorrespondenceTable table, Language language) {
            Map<String, Object> doc = new HashMap<>();
            doc.put(ITEM_ID, table.getId());
            doc.put(UUID, buildUuid(language, table.getUuid()));
            doc.put(LANGUAGE, language.getLanguageCode());
            doc.put(TYPE, "Correspondencetable");
            doc.put(TITLE, table.getName(language));
            doc.put(DESCRIPTION, table.getDescription(language));
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
