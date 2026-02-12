package no.ssb.klass.search;

import no.ssb.klass.core.model.ClassificationSeries;

import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

public interface IndexService {

    void createIndexWithStemmingAnalyzer();

    /**
     * Indexes a classification and makes it searchable.
     *
     * <p>Note:
     *
     * <ul>
     *   <li>If classification is copyrighted the classification is not made searchable
     *   <li>Classification is indexed in each language
     * </ul>
     *
     * <p>Implementation note: Indexing is done asynchronously in order to be more responsive for
     * front end application.
     *
     * @param classificationSeriesId
     */
    @Async
    @Transactional(readOnly = true)
    void indexAsync(Long classificationSeriesId);

    /**
     * Same as indexAsync, but performs indexing within same thread. This means user must wait while
     * indexing, so in normal cases prefer indexAsync.
     *
     * <p>Mostly to be used by unit tests
     *
     * @param classificationSeries
     */
    @Transactional(readOnly = true)
    void indexSync(ClassificationSeries classificationSeries);
}
