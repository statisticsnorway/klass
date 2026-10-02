package no.ssb.klass.api.services;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Query;

public class PublicSearchQuery {

    private PublicSearchQuery() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    public static Query build(
            String query, Pageable pageable, String filterOnSection, boolean includeCodeLists) {
        BoolQueryBuilder searchQuery = buildSearchQuery(query);
        searchQuery.filter(buildFilters(filterOnSection, includeCodeLists));

        NativeSearchQueryBuilder nativeQueryBuilder =
                new NativeSearchQueryBuilder()
                        .withQuery(searchQuery)
                        .withSort(Sort.by(Sort.Order.desc("_score")));

        if (pageable != null) {
            nativeQueryBuilder.withPageable(pageable);
        }

        return nativeQueryBuilder.build();
    }

    private static BoolQueryBuilder buildSearchQuery(String query) {
        BoolQueryBuilder searchQuery =
                QueryBuilders.boolQuery()
                        .should(titlePrefixMatch(query))
                        .should(fuzzyTitleMatch(query))
                        .should(fuzzyDescriptionMatch(query))
                        .should(descriptionAndCodesMatch(query))
                        .minimumShouldMatch(1);

        addClassificationIdMatch(searchQuery, query);
        return searchQuery;
    }

    private static BoolQueryBuilder buildFilters(String filterOnSection, boolean includeCodeLists) {
        BoolQueryBuilder filters = QueryBuilders.boolQuery();
        filters.must(QueryBuilders.termQuery("published", true));
        addTypeFilter(filters, includeCodeLists);

        if (filterOnSection != null) {
            filters.must(QueryBuilders.termQuery("section", filterOnSection));
        }

        // Do not display copyrighted classifications in search.
        filters.mustNot(QueryBuilders.termQuery("copyrighted", true));
        return filters;
    }

    private static void addTypeFilter(BoolQueryBuilder filters, boolean includeCodeLists) {
        String classificationType = ClassificationType.CLASSIFICATION.getDisplayName(Language.EN);
        if (includeCodeLists) {
            String codeListType = ClassificationType.CODELIST.getDisplayName(Language.EN);
            BoolQueryBuilder classificationTypes = QueryBuilders.boolQuery();
            classificationTypes.should(QueryBuilders.matchQuery("type", classificationType));
            classificationTypes.should(QueryBuilders.matchQuery("type", codeListType));
            filters.must(classificationTypes);
        } else {
            filters.must(QueryBuilders.matchQuery("type", classificationType));
        }
    }

    private static QueryBuilder titlePrefixMatch(String query) {
        // Match a query such as "kommune" in a title such as "kommuneinndeling".
        return QueryBuilders.matchPhrasePrefixQuery("title", query).boost(10.0f);
    }

    private static QueryBuilder fuzzyTitleMatch(String query) {
        // Allow a one-edit fuzzy match in titles and descriptions.
        return QueryBuilders.matchQuery("title", query)
                .fuzziness(Fuzziness.fromEdits(1))
                .prefixLength(2)
                .maxExpansions(30)
                .boost(5.0f);
    }

    private static QueryBuilder fuzzyDescriptionMatch(String query) {
        return QueryBuilders.matchQuery("description", query)
                .fuzziness(Fuzziness.fromEdits(1))
                .prefixLength(2)
                .maxExpansions(30)
                .boost(1.0f);
    }

    private static QueryBuilder descriptionAndCodesMatch(String query) {
        return QueryBuilders.multiMatchQuery(query)
                .field("description", 2.0f)
                .field("codes", 0.5f)
                .operator(Operator.OR)
                .boost(2.0f);
    }

    private static void addClassificationIdMatch(BoolQueryBuilder searchQuery, String query) {
        try {
            long classificationId = Long.parseLong(query.trim());
            searchQuery.should(QueryBuilders.termQuery("itemid", classificationId).boost(100.0f));
        } catch (NumberFormatException ignored) {
            // Non-numeric queries use the regular text-search clauses only.
        }
    }
}
