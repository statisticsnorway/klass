package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import no.ssb.klass.api.controllers.ClassificationController;
import no.ssb.klass.api.services.OpenSearchResult;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

@Relation(collectionRelation = "searchResults")
@JsonPropertyOrder({"name", "id", "snippet", "searchScore", "links"})
public class SearchResultResource extends KlassResource {

    private final String name;
    private String snippet;
    private final Double searchScore;
    private final String language;

    public SearchResultResource(
            OpenSearchResult searchResult, Map<String, List<String>> highlights) {
        super(searchResult.getItemId());
        this.name = searchResult.getTitle();
        this.snippet = searchResult.getDescription();
        this.searchScore = searchResult.getScore();
        this.language = searchResult.getLanguage();
        addLink(createSelfLink(searchResult.getItemId()));

        if (highlights != null) {
            List<String> descriptionHighlights = highlights.get("description");
            if (descriptionHighlights != null && !descriptionHighlights.isEmpty()) {
                String snipplet = descriptionHighlights.getFirst();
                if (snipplet.contains("<strong>")) {
                    this.snippet = snipplet;
                    return;
                }
            }

            List<String> codesHighlights = highlights.get("codes");
            if (codesHighlights != null && !codesHighlights.isEmpty()) {
                String snipplet = codesHighlights.getFirst();
                if (snipplet.contains("<strong>")) {
                    StringJoiner joiner = new StringJoiner(", ");
                    codesHighlights.forEach(joiner::add);
                    this.snippet = joiner.toString();
                }
            }
        }
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null, null))
                .withSelfRel()
                .expand();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        SearchResultResource that = (SearchResultResource) o;
        return Objects.equals(name, that.name) && Objects.equals(snippet, that.snippet) && Objects.equals(searchScore, that.searchScore) && Objects.equals(language, that.language);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), name, snippet, searchScore, language);
    }

    public String getName() {
        return name;
    }

    public String getSnippet() {
        return snippet;
    }

    public String getLanguage() {
        return language;
    }

    public Double getSearchScore() {
        return searchScore;
    }
}
