package no.ssb.klass.api.services;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilders;
import org.opensearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.opensearch.index.query.functionscore.ScoreFunctionBuilders;
import org.springframework.data.domain.Pageable;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;

public class PublicSearchQuery {

    private PublicSearchQuery() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    public static org.springframework.data.elasticsearch.core.query.Query build(
            String query,
            Pageable pageable,
            String filterOnSection,
            boolean includeCodeLists
    ) {
        BoolQueryBuilder filterBuilder = QueryBuilders.boolQuery();
        filterBuilder.must(QueryBuilders.termQuery("published", true));

        if (includeCodeLists) {
            BoolQueryBuilder typeFilterBuilder = QueryBuilders.boolQuery();
            String classificationTypeName = ClassificationType.CLASSIFICATION.getDisplayName(Language.EN);
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

        BoolQueryBuilder mainQuery = QueryBuilders.boolQuery()
                .must(QueryBuilders.multiMatchQuery(query)
                        .field("title", 5.0f)
                        .field("title.autocomplete", 2.5f)
                        .field("description", 3.0f)
                        .field("codes", 2.0f)
                        .type(MultiMatchQueryBuilder.Type.BEST_FIELDS)
                        .operator(Operator.OR))
                .should(QueryBuilders.multiMatchQuery(query)
                        .field("title", 1.0f)
                        .field("title.autocomplete", 0.5f)
                        .field("description", 0.5f)
                        .field("codes", 0.25f)
                        .fuzziness(Fuzziness.AUTO)
                        .operator(Operator.OR));
        FunctionScoreQueryBuilder functionScoreQuery = QueryBuilders.functionScoreQuery(
                mainQuery,
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                        new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                                ScoreFunctionBuilders.weightFactorFunction(2.0f)
                        )
                }
        );
        BoolQueryBuilder finalQuery = QueryBuilders.boolQuery()
                .must(functionScoreQuery)
                .filter(filterBuilder);

        NativeSearchQueryBuilder nativeQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(finalQuery)
                .withSort(org.springframework.data.domain.Sort.by(
                        org.springframework.data.domain.Sort.Order.desc("_score")
                ));

        if (pageable != null) {
            nativeQueryBuilder.withPageable(pageable);
        }

        return nativeQueryBuilder.build();
    }
}
