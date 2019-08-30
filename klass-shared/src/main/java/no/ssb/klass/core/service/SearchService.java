package no.ssb.klass.core.service;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.service.search.SolrSearchResult;

public interface SearchService {


    FacetAndHighlightPage<SolrSearchResult> publicSearch(String query, Pageable pageable, String filterOnSection,
            boolean includeCodeLists);

    /**
     * Search for classifications
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
    FacetAndHighlightPage<SolrSearchResult> internalSearch(String query, Pageable pageable, String filterOnSection,
            ClassificationType classificationType, Map<Field, String> facets);

    /**
     * will delete all documents in core (*:*)
     *
     */
    void clearIndex();

    /**
     * will delete all documents(*:*) and start indexing everything
     */
    void reindex();

    /**
     * Indexes a classification and makes it searchable.
     *
     * <p>
     * Note:
     * <ul>
     * <li>If classification is copyrighted the classification is not made searchable</li>
     * <li>Classification is indexed in each language</li>
     * </ul>
     *
     * <p>
     * Implementation note: Indexing is done asynchronously in order to be more responsive for front end application.
     *
     * @param classificationSeriesId
     */
    void indexAsync(Long classificationSeriesId);


    /**
     * Same as indexAsync, but performs indexing within same thread. This means user must wait while indexing, so in
     * normal cases prefer indexAsync.
     * <p>
     * Mostly to be used by unit tests
     * 
     * @param classificationSeries
     */
    void indexSync(ClassificationSeries classificationSeries);
}