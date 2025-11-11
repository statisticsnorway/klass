package no.ssb.klass.api.services;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;

public class PublicSearchQuery {

    private PublicSearchQuery() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static org.springframework.data.elasticsearch.core.query.Query build(
            String query, Pageable pageable, String filterOnSection, boolean includeCodeLists) {
        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
        filterBuilder.must(QueryBuilders.termQuery("published", true));

        if (includeCodeLists) {
            BoolQueryBuilder typeFilterBuilder = QueryBuilders.boolQuery();
            String classificationTypeName =
                    ClassificationType.CLASSIFICATION.getDisplayName(Language.EN);
            String codelistTypeName = ClassificationType.CODELIST.getDisplayName(Language.EN);
            typeFilterBuilder.should(QueryBuilders.matchQuery("type", classificationTypeName));
            typeFilterBuilder.should(QueryBuilders.matchQuery("type", codelistTypeName));
            filterBuilder.must(typeFilterBuilder);
        } else {
            String typeName = ClassificationType.CLASSIFICATION.getDisplayName(Language.EN);
            filterBuilder.must(QueryBuilders.matchQuery("type", typeName));
        }

        if (filterOnSection != null) {
            filterBuilder.must(QueryBuilders.termQuery("section", filterOnSection));
        }
        // Do not display copyrighted in search
        filterBuilder.mustNot(QueryBuilders.termQuery("copyrighted", true));

        // Building a query of multiple conditions
        // Boosting pure match on title as top match
        // Adding fuzziness on title to accept incomplete search words
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery()
                .should(QueryBuilders.matchPhrasePrefixQuery("title", query).boost(20.0f))
                .should(QueryBuilders.matchQuery("title", query)
                        .fuzziness(Fuzziness.fromEdits(1))
                        .prefixLength(2)
                        .maxExpansions(30)
                        .boost(10.0f))
                .should(QueryBuilders.multiMatchQuery(query)
                        .field("description", 2.0f)
                        .field("codes", 0.5f)
                        .operator(Operator.OR)
                        .boost(2.0f))
                .filter(filterBuilder)
                .minimumShouldMatch(1);

        NativeSearchQueryBuilder nativeQueryBuilder =
                new NativeSearchQueryBuilder()
                        .withQuery(finalQuery)
                        .withSort(
                                org.springframework.data.domain.Sort.by(
                                        org.springframework.data.domain.Sort.Order.desc("_score")));

        if (pageable != null) {
            nativeQueryBuilder.withPageable(pageable);
        }

        return nativeQueryBuilder.build();
    }
}
