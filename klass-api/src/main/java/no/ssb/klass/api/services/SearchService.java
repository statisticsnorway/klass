package no.ssb.klass.api.services;

import no.ssb.klass.api.services.search.OpenSearchResult;
import no.ssb.klass.core.model.ClassificationSeries;
import org.springframework.data.domain.Pageable;


public interface SearchService {

    org.springframework.data.domain.Page<OpenSearchResult> publicSearch(String query, Pageable pageable, String filterOnSection,
                                        boolean includeCodeLists);

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