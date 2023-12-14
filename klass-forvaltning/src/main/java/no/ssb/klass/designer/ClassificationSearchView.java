package no.ssb.klass.designer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.solr.core.query.Field;
import org.springframework.data.solr.core.query.SimpleField;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;
import org.springframework.data.solr.core.query.result.FacetFieldEntry;
import org.springframework.data.solr.core.query.result.HighlightEntry;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.spring.annotation.SpringView;
import com.vaadin.spring.annotation.UIScope;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.search.SolrSearchResult;
import no.ssb.klass.designer.MainView.ClassificationFilter;
import no.ssb.klass.designer.components.search.ClassificationSearchResult;
import no.ssb.klass.designer.components.search.CorrespondenceSearchResult;
import no.ssb.klass.designer.components.search.SearchResultComponent;
import no.ssb.klass.designer.components.search.VaraintSearchResult;
import no.ssb.klass.designer.components.search.VersionSearchResult;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.util.KlassTheme;
import no.ssb.klass.designer.util.ParameterUtil;
import no.ssb.klass.designer.util.VaadinUtil;

/**
 * @author Mads Lundemo, SSB.
 */
@SuppressWarnings("serial")
@UIScope
@SpringView(name = ClassificationSearchView.NAME)
public class ClassificationSearchView extends ClassificationSearchDesign implements FilteringView {

    private static final Logger log = LoggerFactory.getLogger(ClassificationSearchView.class);

    public static final String NAME = "search";
    public static final String PARAM_QUERY = "query";
    public static final String PARAM_PAGE_NO = "page";
    public static final String PARAM_FILTERS = "filters";

    @Value("${klass.search.resultsPerPage}")
    private Integer resultsPerPage;

    @Value("${klass.search.maxDescriptionLength}")
    private Integer maxDescriptionLength;

    @Autowired
    private ClassificationFacade classificationFacade;
    @Autowired
    private SearchService searchService;

    private FacetAndHighlightPage<SolrSearchResult> searchResult;
    private Map<Field, String> facets = new HashMap<>();
    private String searchQuery;
    private ClassificationFilter classificationFilter;

    public ClassificationSearchView() {
        this.classificationFilter = VaadinUtil.getKlassState().getClassificationFilter();
    }

    private FacetAndHighlightPage<SolrSearchResult> getPage(String searchQuery, int page) {
        return searchService.internalSearch(searchQuery, PageRequest.of(page, resultsPerPage),
                classificationFilter.getCurrentSection(), classificationFilter.getCurrentClassificationType(), facets);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent viewChangeEvent) {
        String parameters = viewChangeEvent.getParameters();
        int page = 0;
        if (ParameterUtil.hasParameter(PARAM_PAGE_NO, parameters)) {
            page = ParameterUtil.getRequiredIntParameter(PARAM_PAGE_NO, parameters);
        }

        if (ParameterUtil.hasParameter(PARAM_FILTERS, parameters)) {
            String filters = ParameterUtil.getRequiredStringParameter(PARAM_FILTERS, parameters);
            facets = prepareFilters(filters);
        } else {
            facets = new HashMap<>();
        }
        searchQuery = ParameterUtil.getRequiredStringParameter(PARAM_QUERY, parameters);
        searchBox.setSearchText(searchQuery);
        searchResult = getPage(searchQuery, page);
        setResultsText();
        populatePage();
        showFacets();
    }

    private HashMap<Field, String> prepareFilters(String filters) {
        if (Strings.isNullOrEmpty(filters)) {
            return new HashMap<>();
        } else {
            HashMap<Field, String> map = new HashMap<>();
            Arrays.asList(filters.split(",")).forEach(s -> {
                String[] split = s.split(":");
                String fieldName = split[0];
                String value = split[1];
                map.put(new SimpleField(fieldName), value);
            });
            return map;
        }
    }

    private void showFacets() {
        facetLayout.removeAllComponents();
        Label filter = new Label("Resultat Filtrering");
        filter.addStyleName(ValoTheme.LABEL_BOLD);
        facetLayout.addComponent(filter);
        searchResult.getFacetFields().forEach(field -> {
            Label label = new Label(getNorwegianName(field));
            facetLayout.addComponent(label);
            searchResult.getFacetResultPage(field).forEach(entry -> {

                if (facets.containsKey(entry.getField())
                        && facets.get(entry.getField()).equals(entry.getValue())
                        && entry.getValueCount() > 0) {

                    Button button = new Button();
                    button.setStyleName(ValoTheme.BUTTON_LINK);
                    facetLayout.addComponent(button);
                    button.setIcon(FontAwesome.TIMES);
                    button.setCaptionAsHtml(true);
                    button.setCaption("<strong>" + getNorwegianValue(entry) + " (" + entry
                            .getValueCount() + ")</strong>");
                    button.addClickListener(event -> {
                        facets.remove(entry.getField());
                        searchResult = getPage(searchQuery, 0);
                        gotoPage(0);
                        populatePage();
                        showFacets();
                    });
                } else if (entry.getValueCount() > 0) {

                    Button button = new Button();
                    button.setStyleName(ValoTheme.BUTTON_LINK);
                    facetLayout.addComponent(button);
                    button.setCaption(getNorwegianValue(entry) + " (" + entry.getValueCount()
                            + ")");
                    button.addClickListener(event -> {
                        // facets.clear();
                        facets.put(entry.getField(), entry.getValue());
                        searchResult = getPage(searchQuery, 0);
                        gotoPage(0);
                        populatePage();
                        showFacets();
                    });
                }

            });
        });
    }

    private String getNorwegianName(Field field) {
        String name = field.getName();
        switch (name) {
        case "type":
            return "Type";
        case "published":
            return "Publisering";
        case "language":
            return "Språk";
        default:
            return name;
        }
    }

    private String getNorwegianValue(FacetFieldEntry field) {
        String value = field.getValue();
        switch (value) {
        case "Classification":
            return "Klassifikasjon";
        case "Version":
            return "Versjon";
        case "Codelist":
            return "Kodeliste";
        case "Variant":
            return "Variant";
        case "Correspondencetable":
            return "Korrespondansetabell";
        case "true":
            if (field.getField().getName().equals("published")) {
                return "Publisert";
            }

        case "false":
            if (field.getField().getName().equals("published")) {
                return "Ikke publisert";
            }
        case "nb":
            return "Bokmål";
        case "nn":
            return "Nynorsk";
        case "en":
            return "Engelsk";
        default:
            return value;
        }


    }

    public void setResultsText() {
        long resultCount = searchResult.getTotalElements();
        if (resultCount == 0) {
            resultsText.setValue("Fant ingen treff på \"" + searchQuery + "\" ");
        } else {
            resultsText.setValue("Resultat på \"" + searchQuery + "\" " + resultCount + " stk.");
        }
    }

    private void gotoPage(int page) {

        StringJoiner filters = new StringJoiner(",");
        facets.forEach((field, s) -> filters.add(field.getName() + ":" + s));
        if (filters.length() > 0) {
            VaadinUtil.navigateTo(NAME, ImmutableMap.of(PARAM_QUERY, searchQuery,
                    PARAM_PAGE_NO, String.valueOf(page),
                    PARAM_FILTERS, filters.toString()));
        } else {
            VaadinUtil.navigateTo(NAME, ImmutableMap.of(PARAM_QUERY, searchQuery,
                    PARAM_PAGE_NO, String.valueOf(page)));
        }
    }

    private void populatePage() {
        clearSearchResults();

        // reverse due to order added in View
        List<SolrSearchResult> pageContent = Lists.reverse(searchResult.getContent());

        for (SolrSearchResult searchElement : pageContent) {
            SearchResultComponent searchResultUI;
            try {
                String type = searchElement.getType();
                switch (type) {
                case "Classification":
                case "Codelist":
                    searchResultUI = classificationSearchElement(searchElement);
                    break;
                case "Version":
                    searchResultUI = versionSearchElement(searchElement);
                    break;
                case "Variant":
                    searchResultUI = variantSearchElement(searchElement);
                    break;
                case "Correspondencetable":
                    searchResultUI = correspondenceSearchElement(searchElement);
                    break;
                default:
                    throw new RuntimeException("Unknown search result type");
                }
                replaceFieldsWithHighlighting(searchElement, searchResultUI);
                searchResultComponent.addComponentAsFirst(searchResultUI);
            } catch (Exception e) {
                log.error("Problem fetching search result from klass", e);

            }

        }
        populatePageSelector();
    }

    private void replaceFieldsWithHighlighting(SolrSearchResult searchElement, SearchResultComponent searchResultUI) {
        List<HighlightEntry.Highlight> highlights = searchResult.getHighlights(searchElement);
        boolean descriptionFound = false;
        for (HighlightEntry.Highlight h : highlights) {
            if (h.getField().getName().equals("description")) {
                if (!h.getSnipplets().isEmpty()) {
                    String snippet = h.getSnipplets().get(0);
                    if (snippet.contains("<strong>")) {
                        descriptionFound = true;
                        searchResultUI.setDescription(snippet);
                    }
                }
            } else if (h.getField().getName().equals("codes")) {
                if (!h.getSnipplets().isEmpty()) {
                    String snippet = h.getSnipplets().get(0);
                    if (snippet.contains("<strong>") && !descriptionFound) {
                        StringJoiner joiner = new StringJoiner(", ");
                        h.getSnipplets().forEach(s -> joiner.add(s));
                        searchResultUI.setDescription(joiner.toString());
                    }
                }
            } else if (h.getField().getName().equals("title")) {
                if (!h.getSnipplets().isEmpty()) {
                    String snipplet = h.getSnipplets().get(0);
                    if (snipplet.contains("<strong>")) {
                        searchResultUI.setLinkText(snipplet);
                    }
                }
            }
        }
    }

    private SearchResultComponent classificationSearchElement(SolrSearchResult searchElement) {

        ClassificationSeries series =
                classificationFacade.getRequiredClassificationSeries(searchElement.getItemid());
        ClassificationSearchResult searchResultUI = new ClassificationSearchResult();
        searchResultUI.setClassification(series);
        searchResultUI.setLinkText(searchElement.getTitle());
        searchResultUI.setDescription(limitedDescription(searchElement.getDescription()));
        String hierarchy = series.getClassificationType().getDisplayName(Language.getDefault())
                + " - " + series.getClassificationFamily().getName()
                + " - " + series.getContactPerson().getSection();
        searchResultUI.setHierarchy(hierarchy);
        return searchResultUI;
    }

    private SearchResultComponent versionSearchElement(SolrSearchResult searchElement) {
        ClassificationVersion version = classificationFacade.getRequiredClassificationVersion(searchElement
                .getItemid());

        VersionSearchResult searchResultUI = new VersionSearchResult();
        searchResultUI.setVersion(version);
        searchResultUI.setLinkText(searchElement.getTitle());
        searchResultUI.setDescription(limitedDescription(searchElement.getDescription()));
        String hierarchy = "Versjon"
                // version.getOwnerClassification().getClassificationType().getDisplayName(Language
                // .getDefault())
                + " - " + version.getOwnerClassification().getClassificationFamily().getName()
                + " - " + version.getContactPerson().getSection();
        searchResultUI.setHierarchy(hierarchy);
        return searchResultUI;

    }

    private SearchResultComponent variantSearchElement(SolrSearchResult searchElement) {
        ClassificationVariant variant = classificationFacade.getRequiredClassificationVariant(searchElement
                .getItemid());

        VaraintSearchResult searchResultUI = new VaraintSearchResult();
        searchResultUI.setVariant(variant);
        searchResultUI.setLinkText(searchElement.getTitle());
        searchResultUI.setDescription(limitedDescription(searchElement.getDescription()));
        String hierarchy = "Variant"
                // variant.getOwnerClassification().getClassificationType().getDisplayName(Language
                // .getDefault())
                + " - " + variant.getOwnerClassification().getClassificationFamily().getName()
                + " - " + variant.getContactPerson().getSection();
        searchResultUI.setHierarchy(hierarchy);
        return searchResultUI;
    }

    private SearchResultComponent correspondenceSearchElement(SolrSearchResult searchElement) {
        CorrespondenceTable correspondenceTable = classificationFacade.getRequiredCorrespondenceTable(searchElement
                .getItemid());

        CorrespondenceSearchResult searchResultUI = new CorrespondenceSearchResult();
        searchResultUI.setCorrespondenceTable(correspondenceTable);
        searchResultUI.setLinkText(searchElement.getTitle());
        searchResultUI.setDescription(limitedDescription(searchElement.getDescription()));
        String hierarchy = "Korrespondansetabell"
                // correspondenceTable.getOwnerClassification().getClassificationType().getDisplayName(Language
                // .getDefault())
                + " - " + correspondenceTable.getOwnerClassification().getClassificationFamily().getName()
                + " - " + correspondenceTable.getContactPerson().getSection();
        searchResultUI.setHierarchy(hierarchy);
        return searchResultUI;
    }

    private String limitedDescription(String description) {
        if (description.length() > maxDescriptionLength) {
            return description.substring(0, maxDescriptionLength) + "[...]";
        }
        return description;
    }

    private void populatePageSelector() {
        int totalPages = searchResult.getTotalPages();
        if (totalPages > 1) {
            for (int i = 0; i < totalPages; i++) {
                Button button = new Button(String.valueOf(i + 1));
                button.setData(i);

                boolean selected = i == searchResult.getNumber();
                button.setPrimaryStyleName(selected ? KlassTheme.PAGE_NUMBER_HIGHLIGHTED : KlassTheme.BUTTON_AS_LINK);
                button.setStyleName(KlassTheme.PAGE_NUMBER);
                button.addClickListener(event -> gotoPage((Integer) event.getButton().getData()));
                pagingComponent.addComponent(button);
            }
        }
    }

    private void clearSearchResults() {
        searchResultComponent.removeAllComponents();
        pagingComponent.removeAllComponents();
        searchResultComponent.addComponent(pagingComponent);
    }
}
