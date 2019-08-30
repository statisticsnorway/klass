package no.ssb.klass.core.service;

import static com.google.common.base.Preconditions.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.FacetAndHighlightQuery;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.SoftDeletable;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.service.search.InternalSearchQuery;
import no.ssb.klass.core.service.search.PublicSearchQuery;
import no.ssb.klass.core.service.search.SolrSearchResult;
import no.ssb.klass.core.util.TimeUtil;

@Service
public class SearchServiceImpl implements SearchService {
    private static final Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);

    private final ClassificationSeriesRepository classificationRepository;

    @Autowired
    private SolrTemplate solrTemplate;

    @Autowired
    public SearchServiceImpl(ClassificationSeriesRepository classificationRepository) {
        this.classificationRepository = classificationRepository;
    }

    public FacetAndHighlightPage<SolrSearchResult> search(FacetAndHighlightQuery query) {

        Date start = TimeUtil.now();
        FacetAndHighlightPage<SolrSearchResult> searchResults = solrTemplate.queryForFacetAndHighlightPage(query,
                SolrSearchResult.class);
        log.info("Search for: '" + query + "' resulted in " + searchResults.getTotalElements() + " hits. Took (ms): "
                + TimeUtil.millisecondsSince(start));
        return searchResults;
    }

    /**
     * Search for public classifications (filtering out copyrighted and not published)
     *
     * @param query
     *            query to match, may be many words. Each word will then be searched for
     * @param pageable
     * @param filterOnSection
     *            null means all sections
     * @param classificationType
     *            null means all classificationTypes
     * @return searchResults
     */
    @Override
    public FacetAndHighlightPage<SolrSearchResult> publicSearch(String query, Pageable pageable, String filterOnSection,
            boolean includeCodeLists) {

        return search(new PublicSearchQuery(query, pageable, filterOnSection, includeCodeLists));
    }

    /**
     * Internal Search for classifications (results may include copyrighted and upublished content)
     *
     * @param query
     *            query to match, may be many words. Each word will then be searched for
     * @param pageable
     * @param filterOnSection
     *            null means all sections
     * @param classificationType
     *            null means all classificationTypes
     * @return searchResults
     */

    @Override
    public FacetAndHighlightPage<SolrSearchResult> internalSearch(String query, Pageable pageable,
            String filterOnSection,
            ClassificationType classificationType, Map<Field, String> facets) {

        return search(new InternalSearchQuery(query, pageable, filterOnSection, classificationType, facets));
    }

    public void clearIndex() {
        solrTemplate.delete(new SimpleQuery("*:*"));
    }

    @Async
    @Transactional(readOnly = true)
    public void reindex() {
        clearIndex();
        classificationRepository.findAllClassificationIds().forEach(this::indexAsync);
    }

    @Override
    @Transactional(readOnly = true)
    @Async
    public void indexAsync(Long classificationSeriesId) {
        checkNotNull(classificationSeriesId);
        ClassificationSeries classification = classificationRepository.getOne(classificationSeriesId);
        indexSync(classification);
    }

    @Override
    @Transactional(readOnly = true)
    public void indexSync(ClassificationSeries classification) {
        Date start = TimeUtil.now();
        for (Language language : Language.values()) {
            // filter out entities that are missing translation
            if (!classification.getName(language).isEmpty()) {
                SolrInputDocument doc = new SolrInputDocument();
                doc.addField("itemid", classification.getId());
                doc.addField("uuid", language.getLanguageCode() + "_" + classification.getUuid());
                doc.addField("language", language.getLanguageCode());
                doc.addField("type", classification.getClassificationType().getDisplayName(Language.EN));
                doc.addField("copyrighted", classification.isCopyrighted());
                doc.addField("published", classification.isPublished(language));
                doc.addField("title", classification.getName(language));
                doc.addField("description", classification.getDescription(language));
                doc.addField("family", classification.getClassificationFamily().getName(language));
                doc.addField("section", classification.getContactPerson().getSection());
                for (ClassificationVersion version : classification.getClassificationVersions()) {
                    version.getAllClassificationItems().forEach(item -> doc.addField("codes", formatClassificationItem(
                            language, item)));

                    recursiveIndex(version, language);
                }
                updateSolr(classification, doc);
            }

        }
        solrTemplate.commit();
        log.info("Indexing: " + classification.getNameInPrimaryLanguage() + ". Took (ms): " + TimeUtil
                .millisecondsSince(start));
    }

    private void recursiveIndex(ClassificationVersion version, Language language) {
        SolrInputDocument doc = new SolrInputDocument();
        doc.addField("itemid", version.getId());
        doc.addField("uuid", language.getLanguageCode() + "_" + version.getUuid());
        doc.addField("classificationId", version.getClassification().getId());
        doc.addField("language", language.getLanguageCode());
        doc.addField("type", "Version");
        doc.addField("title", version.getName(language));
        doc.addField("legalBase", version.getLegalBase(language));
        doc.addField("publications", version.getPublications(language));
        doc.addField("derivedFrom", version.getDerivedFrom(language));
        doc.addField("description", version.getIntroduction(language));
        doc.addField("section", version.getContactPerson().getSection());
        doc.addField("copyrighted", version.getOwnerClassification().isCopyrighted());
        doc.addField("published", version.isPublished(language));

        version.getAllClassificationItems()
                .forEach(item -> doc.addField("codes", formatClassificationItem(language, item)));

        indexVariants(version.getClassificationVariants(), language);
        indexCorrespondenceTables(version.getCorrespondenceTables(), language);

        updateSolr(version, doc);

    }

    private void indexCorrespondenceTables(List<CorrespondenceTable> correspondenceTables, Language language) {

        for (CorrespondenceTable correspondenceTable : correspondenceTables) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("itemid", correspondenceTable.getId());
            doc.addField("uuid", language.getLanguageCode() + "_" + correspondenceTable.getUuid());
            doc.addField("language", language.getLanguageCode());
            doc.addField("type", "Correspondencetable");
            doc.addField("title", correspondenceTable.getName(language));
            doc.addField("description", correspondenceTable.getDescription(language));
            updateSolr(correspondenceTable, doc);
        }
    }

    private void indexVariants(List<ClassificationVariant> variants, Language language) {
        for (ClassificationVariant variant : variants) {
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("itemid", variant.getId());
            doc.addField("uuid", language.getLanguageCode() + "_" + variant.getUuid());
            doc.addField("language", language.getLanguageCode());
            doc.addField("type", "Variant");
            doc.addField("title", variant.getFullName(language));
            doc.addField("copyrighted", variant.getOwnerClassification().isCopyrighted());
            doc.addField("published", variant.isPublished(language));
            doc.addField("description", variant.getIntroduction(language));
            doc.addField("section", variant.getContactPerson().getSection());
            variant.getAllClassificationItems()
                    .forEach(item -> doc.addField("codes", formatClassificationItem(language, item)));
            updateSolr(variant, doc);
        }
    }

    private void updateSolr(SoftDeletable entity, SolrInputDocument doc) {
        if (!entity.isDeleted()) {
            solrTemplate.saveDocument(doc);
        } else {
            solrTemplate.deleteById((String) doc.getField("uuid").getValue());
        }
    }

    private String formatClassificationItem(Language language, ClassificationItem item) {
        return item.getCode() + " - " + item.getOfficialName(language);
    }

}
