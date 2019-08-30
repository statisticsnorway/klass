package no.ssb.klass.core.service.search;

import java.util.Map;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FacetOptions;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.SimpleFacetAndHighlightQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

/**
 * @author Mads Lundemo, SSB.
 */
public class InternalSearchQuery extends SimpleFacetAndHighlightQuery {

    private static final String HANDLER = "internalSearch";

    public InternalSearchQuery(String query, Pageable pageable, String filterOnSection,
            ClassificationType classificationType, Map<Field, String> facets) {

        setRequestHandler(HANDLER);
        this.addCriteria(new SimpleStringCriteria(query));

        if (pageable != null) {
            setPageRequest(pageable);
        }

        if (classificationType != null) {
            this.addCriteria(Criteria.where("type").is(classificationType.getDisplayName(Language.EN)));
        }

        if (filterOnSection != null) {
            this.addCriteria(Criteria.where("section").is(filterOnSection));
        }
        if (facets != null) {
            facets.entrySet().forEach(entry -> this.addCriteria(Criteria.where(entry.getKey()).is(entry.getValue())));

        }
        FacetOptions facetOptions = new FacetOptions();
        facetOptions.addFacetOnField("type");
        facetOptions.addFacetOnField("published");
        facetOptions.addFacetOnField("language");
        facetOptions.setFacetMinCount(0);
        facetOptions.setFacetSort(FacetOptions.FacetSort.INDEX);
        setFacetOptions(facetOptions);
    }

    @Override
    public String toString() {
        return getCriteria().toString();
    }
}
