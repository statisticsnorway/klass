package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import no.ssb.klass.api.services.search.OpenSearchResult;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.core.Relation;

import no.ssb.klass.api.controllers.ClassificationController;

@Relation(collectionRelation = "searchResults")
@JsonPropertyOrder({"name", "id", "snippet", "searchScore", "links"})
public class SearchResultResource extends KlassResource {

    private String name;
    private String snippet;
    private Double searchScore;

    public SearchResultResource(OpenSearchResult searchResult, Map<String, List<String>> highlights) {
        super(searchResult.getItemid());
        this.name = searchResult.getTitle();
        this.snippet = searchResult.getDescription();
        this.searchScore = searchResult.getScore();
        addLink(createSelfLink(searchResult.getItemid()));

        if (highlights != null) {
            List<String> descriptionHighlights = highlights.get("description");
            if (descriptionHighlights != null && !descriptionHighlights.isEmpty()) {
                String snipplet = descriptionHighlights.get(0);
                if (snipplet.contains("<strong>")) {
                    this.snippet = snipplet;
                    return;
                }
            }
            
            List<String> codesHighlights = highlights.get("codes");
            if (codesHighlights != null && !codesHighlights.isEmpty()) {
                String snipplet = codesHighlights.get(0);
                if (snipplet.contains("<strong>")) {
                    StringJoiner joiner = new StringJoiner(", ");
                    codesHighlights.forEach(joiner::add);
                    this.snippet = joiner.toString();
                }
            }
        }
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null, null)).withSelfRel().expand();
    }

    public String getName() {
        return name;
    }

    public String getSnippet() {
        return snippet;
    }

    public Double getSearchScore() {
        return searchScore;
    }
}
