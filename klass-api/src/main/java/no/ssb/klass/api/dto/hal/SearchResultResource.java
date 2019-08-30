package no.ssb.klass.api.dto.hal;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import java.util.List;
import java.util.StringJoiner;

import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.core.Relation;

import no.ssb.klass.core.service.search.SolrSearchResult;
import no.ssb.klass.api.controllers.ClassificationController;

@Relation(collectionRelation = "searchResults")
public class SearchResultResource extends KlassResource {

    private String name;
    private String snippet;
    private Double searchScore;

    public SearchResultResource(SolrSearchResult searchResult, List<HighlightEntry.Highlight> highlights) {
        this.name = searchResult.getTitle();
        this.snippet = searchResult.getDescription();
        this.searchScore = searchResult.getScore();
        addLink(createSelfLink(searchResult.getItemid()));

        for (HighlightEntry.Highlight h : highlights) {
            if (h.getField().getName().equals("codes")) {
                if (!h.getSnipplets().isEmpty()) {
                    String snipplet = h.getSnipplets().get(0);
                    if (snipplet.contains("<strong>")) {
                        StringJoiner joiner = new StringJoiner(", ");
                        h.getSnipplets().forEach(joiner::add);
                        this.snippet = joiner.toString();
                    }
                }
            } else if (h.getField().getName().equals("description")) {
                if (!h.getSnipplets().isEmpty()) {
                    String snipplet = h.getSnipplets().get(0);
                    if (snipplet.contains("<strong>")) {
                        this.snippet = snipplet;
                        return;
                    }
                }

            }
        }
    }

    private Link createSelfLink(Long id) {
        return linkTo(methodOn(ClassificationController.class).classification(id, null, null)).withSelfRel();
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
