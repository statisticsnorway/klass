package no.ssb.klass.designer.components.search;

import java.util.function.Consumer;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;

/**
 * @author Mads Lundemo, SSB.
 */
public class SearchResultComponent extends CustomComponent {

    protected SearchResultDesign design = new SearchResultDesign();

    public SearchResultComponent() {
        setCompositionRoot(design);
    }

    public void setLinkText(String title) {
        design.titleButton.setCaptionAsHtml(true);
        design.titleButton.setCaption(title);

    }

    public void setLink(Consumer<Button.ClickEvent> link) {
        design.titleButton.addClickListener(link::accept);
    }


    @Override
    public void setDescription(String description) {
        design.descriptionLabel.setValue(description);
    }

    public void setHierarchy(String hierarchy) {
        design.hierarchyLabel.setValue(hierarchy);
    }

}
