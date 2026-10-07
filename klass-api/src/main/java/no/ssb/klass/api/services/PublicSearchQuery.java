package no.ssb.klass.api.services;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.Language;

import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.no.NorwegianAnalyzer;
import org.opensearch.common.unit.Fuzziness;
import org.opensearch.data.client.orhlc.NativeSearchQueryBuilder;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.DisMaxQueryBuilder;
import org.opensearch.index.query.Operator;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.query.Query;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PublicSearchQuery {

    private static final int MAX_SHORT_PREFIX_LENGTH = 2;

    /** Body matches need 75% of the terms, so a two-word query matches when one word is found. */
    private static final String BODY_MINIMUM_SHOULD_MATCH = "75%";

    private static final Fuzziness FUZZINESS = Fuzziness.customAuto(3, 6);
    private static final CharArraySet STOP_WORDS = NorwegianAnalyzer.getDefaultStopSet();

    private static final float TIE_BREAKER = 0.2f;
    private static final float EXACT_TITLE_BOOST = 15.0f;
    private static final float TITLE_PREFIX_BOOST = 10.0f;
    private static final float TITLE_BOOL_PREFIX_BOOST = 7.0f;
    private static final float FUZZY_TITLE_BOOST = 5.0f;
    private static final float SHORT_PREFIX_BOOST = 4.0f;

    /** Prevents instantiation of this static query-building utility. */
    private PublicSearchQuery() {
        throw new UnsupportedOperationException(
                "This is a utility class and cannot be instantiated");
    }

    /** Builds the OpenSearch query, public-result filters, score ordering, and optional paging. */
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

    /** Combines title and body search groups and adds an exact ID match for numeric input. */
    private static BoolQueryBuilder buildSearchQuery(String query) {
        BoolQueryBuilder searchQuery = QueryBuilders.boolQuery();
        List<String> tokens = queryTokens(query);
        searchQuery.should(titleMatches(query));
        searchQuery.should(bodyMatches(prepareBodyQuery(query, tokens)));
        searchQuery.minimumShouldMatch(1);

        addClassificationIdMatch(searchQuery, query);
        return searchQuery;
    }

    /** Builds non-scoring filters for published status, allowed types, section, and copyright. */
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

    /** Requires classification documents and optionally allows codelist documents as well. */
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

    /**
     * Groups boosted title strategies: exact, prefix, bool-prefix, fuzzy, and short-prefix matches.
     */
    private static QueryBuilder titleMatches(String query) {
        DisMaxQueryBuilder matches = QueryBuilders.disMaxQuery().tieBreaker(TIE_BREAKER);
        matches.add(exactTitleMatch(query));
        matches.add(titlePrefixMatch(query));
        matches.add(titleBoolPrefixMatch(query));
        matches.add(fuzzyTitleMatch(query));

        addShortPrefixMatch(matches, query);
        return matches;
    }

    /** Matches the full analyzed query as an exact phrase in the title. */
    private static QueryBuilder exactTitleMatch(String query) {
        return QueryBuilders.matchPhraseQuery("title", query).boost(EXACT_TITLE_BOOST);
    }

    /**
     * Matches the analyzed query as a phrase whose last term may be a prefix, such as "kommune" in
     * "kommuneinndeling".
     */
    private static QueryBuilder titlePrefixMatch(String query) {
        return QueryBuilders.matchPhrasePrefixQuery("title", query).boost(TITLE_PREFIX_BOOST);
    }

    /** Requires all analyzed query terms in the title while treating the final term as a prefix. */
    private static QueryBuilder titleBoolPrefixMatch(String query) {
        return QueryBuilders.matchBoolPrefixQuery("title", query)
                .operator(Operator.AND)
                .boost(TITLE_BOOL_PREFIX_BOOST);
    }

    /** Adds a title-term prefix clause only for short, in-progress queries. */
    private static void addShortPrefixMatch(DisMaxQueryBuilder matches, String query) {
        String trimmedQuery = query.trim();
        if (!trimmedQuery.isEmpty() && trimmedQuery.length() <= MAX_SHORT_PREFIX_LENGTH) {
            matches.add(
                    QueryBuilders.prefixQuery("title", trimmedQuery.toLowerCase(Locale.ROOT))
                            .boost(SHORT_PREFIX_BOOST));
        }
    }

    /** Matches analyzed title terms while allowing a limited edit distance for typos. */
    private static QueryBuilder fuzzyTitleMatch(String query) {
        return QueryBuilders.matchQuery("title", query)
                .fuzziness(FUZZINESS)
                .prefixLength(2)
                .maxExpansions(30)
                .boost(FUZZY_TITLE_BOOST);
    }

    /** Groups lower-priority description and code matches, preferring the strongest body match. */
    private static QueryBuilder bodyMatches(String bodyQuery) {
        return QueryBuilders.disMaxQuery()
                .tieBreaker(TIE_BREAKER)
                .add(fuzzyDescriptionMatch(bodyQuery))
                .add(descriptionAndCodesMatch(bodyQuery));
    }

    /** Matches description terms while allowing a limited edit distance for typos. */
    private static QueryBuilder fuzzyDescriptionMatch(String bodyQuery) {
        return QueryBuilders.matchQuery("description", bodyQuery)
                .fuzziness(FUZZINESS)
                .prefixLength(2)
                .maxExpansions(30)
                .boost(1.0f);
    }

    /** Searches descriptions and indexed classification code/name text with weighted fields. */
    private static QueryBuilder descriptionAndCodesMatch(String bodyQuery) {
        return QueryBuilders.multiMatchQuery(bodyQuery)
                .field("description", 2.0f)
                .field("codes", 0.3f)
                .operator(Operator.OR)
                .minimumShouldMatch(BODY_MINIMUM_SHOULD_MATCH)
                .boost(2.0f);
    }

    /**
     * Removes Norwegian stop words from body-search terms, retaining the original if none remain.
     */
    private static String prepareBodyQuery(String query, List<String> tokens) {
        String filtered =
                tokens.stream()
                        .filter(token -> !STOP_WORDS.contains(token))
                        .collect(Collectors.joining(" "));
        return filtered.isBlank() ? query : filtered;
    }

    /**
     * Splits query text on whitespace and hyphens, as the index tokenizer does, and lowercases each
     * token. Underscores and dots are kept, so "org_form" and "47.771" stay whole.
     */
    private static List<String> queryTokens(String query) {
        return Arrays.stream(query.trim().split("[\\s-]+"))
                .filter(token -> !token.isBlank())
                .map(token -> token.toLowerCase(Locale.ROOT))
                .toList();
    }

    /** Adds an exact, high-boost item ID clause when the trimmed query is a valid long integer. */
    private static void addClassificationIdMatch(BoolQueryBuilder searchQuery, String query) {
        try {
            long classificationId = Long.parseLong(query.trim());
            searchQuery.should(QueryBuilders.termQuery("itemid", classificationId).boost(100.0f));
        } catch (NumberFormatException ignored) {
            // Non-numeric queries use the regular text-search clauses only.
        }
    }
}
