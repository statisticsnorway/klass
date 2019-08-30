package no.ssb.klass.core.service.search;

import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.SimpleFacetAndHighlightQuery;
import org.springframework.data.solr.core.query.SimpleStringCriteria;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

/**
 * @author Mads Lundemo, SSB.
 */
public class PublicSearchQuery extends SimpleFacetAndHighlightQuery {
    private static final String HANDLER = "search";

    public PublicSearchQuery(String query, Pageable pageable, String filterOnSection,
            boolean includeCodeLists) {

        setRequestHandler(HANDLER);
        this.addCriteria(new SimpleStringCriteria(query));

        if (pageable != null) {
            setPageRequest(pageable);
        }

        if (includeCodeLists) {
            this.addCriteria(Criteria.where("type")
                    .is(ClassificationType.CLASSIFICATION.getDisplayName(Language.EN))
                    .or("type")
                    .is(ClassificationType.CODELIST.getDisplayName(Language.EN)));
        } else {
            this.addCriteria(Criteria.where("type")
                    .is(ClassificationType.CLASSIFICATION.getDisplayName(Language.EN)));
        }

        if (filterOnSection != null) {
            this.addCriteria(Criteria.where("section").is(filterOnSection));
        }
    }
}
