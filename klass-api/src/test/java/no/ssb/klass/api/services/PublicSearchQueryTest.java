package no.ssb.klass.api.services;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.opensearch.data.client.orhlc.NativeSearchQuery;
import org.opensearch.index.query.BoolQueryBuilder;
import org.opensearch.index.query.DisMaxQueryBuilder;
import org.opensearch.index.query.MatchQueryBuilder;
import org.opensearch.index.query.MultiMatchQueryBuilder;
import org.opensearch.index.query.PrefixQueryBuilder;
import org.opensearch.index.query.QueryBuilder;
import org.opensearch.index.query.TermQueryBuilder;

import java.util.List;

class PublicSearchQueryTest {

    @Test
    void shortQueryUsesPrefixClause() {
        assertThat(titleClauses("so"))
                .filteredOn(PrefixQueryBuilder.class::isInstance)
                .map(PrefixQueryBuilder.class::cast)
                .singleElement()
                .satisfies(
                        q -> {
                            assertThat(q.fieldName()).isEqualTo("title");
                            assertThat(q.value()).isEqualTo("so");
                        });
    }

    @Test
    void longerQueryHasNoPrefixClause() {
        assertThat(titleClauses("sone")).noneMatch(PrefixQueryBuilder.class::isInstance);
    }

    @Test
    void bodyMatchRequiresSeventyFivePercentOfTerms() {
        assertThat(bodyMultiMatch("levekår sone").minimumShouldMatch()).isEqualTo("75%");
    }

    @Test
    void hyphenatedQueryIsSplitBeforeStopWordRemoval() {
        assertThat(bodyMultiMatch("ra-nummer og").value()).isEqualTo("ra nummer");
    }

    @Test
    void stopWordsAreRemovedFromBodyQueryOnly() {
        assertThat(bodyMultiMatch("standard for nærings").value()).isEqualTo("standard nærings");
        assertThat(titleClauses("standard for nærings"))
                .filteredOn(MatchQueryBuilder.class::isInstance)
                .map(MatchQueryBuilder.class::cast)
                .filteredOn(q -> q.fieldName().equals("title"))
                .allMatch(q -> "standard for nærings".equals(q.value()));
    }

    @Test
    void nynorskStopWordsAreRemoved() {
        assertThat(bodyMultiMatch("ikkje kommune").value()).isEqualTo("kommune");
    }

    @Test
    void bodyQueryFallsBackToOriginalWhenOnlyStopWords() {
        assertThat(bodyMultiMatch("og i").value()).isEqualTo("og i");
    }

    @Test
    void numericQueryKeepsClassificationIdMatchWithBoost() {
        List<QueryBuilder> should = bool("121").should();

        assertThat(should)
                .filteredOn(TermQueryBuilder.class::isInstance)
                .map(TermQueryBuilder.class::cast)
                .singleElement()
                .satisfies(
                        q -> {
                            assertThat(q.fieldName()).isEqualTo("itemid");
                            assertThat(q.value()).isEqualTo(121L);
                            assertThat(q.boost()).isEqualTo(100.0f);
                        });
    }

    @Test
    void textQueryHasNoClassificationIdMatch() {
        assertThat(bool("kommune").should()).noneMatch(TermQueryBuilder.class::isInstance);
    }

    private static BoolQueryBuilder bool(String query) {
        return (BoolQueryBuilder)
                ((NativeSearchQuery) PublicSearchQuery.build(query, null, null, true)).getQuery();
    }

    private static List<QueryBuilder> titleClauses(String query) {
        return ((DisMaxQueryBuilder) bool(query).should().get(0)).innerQueries();
    }

    private static MultiMatchQueryBuilder bodyMultiMatch(String query) {
        DisMaxQueryBuilder body = (DisMaxQueryBuilder) bool(query).should().get(1);
        return body.innerQueries().stream()
                .filter(MultiMatchQueryBuilder.class::isInstance)
                .map(MultiMatchQueryBuilder.class::cast)
                .findFirst()
                .orElseThrow();
    }
}
