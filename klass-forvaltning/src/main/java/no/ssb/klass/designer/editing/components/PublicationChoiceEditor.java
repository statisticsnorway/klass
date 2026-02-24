package no.ssb.klass.designer.editing.components;

import org.apache.commons.lang.builder.EqualsBuilder;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.VerticalLayout;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Publishable;
import no.ssb.klass.designer.components.common.layouts.VerticalSpacedLayout;
import no.ssb.klass.designer.util.KlassTheme;

@SuppressWarnings("serial")
public class PublicationChoiceEditor extends VerticalLayout {

    private CheckBox noPublication;
    private CheckBox nnPublication;
    private CheckBox enPublication;
    private Publishable publishable;

    public PublicationChoiceEditor() {
        createPublicationChoiceEditor(new MarginInfo(true, false, true, false));
    }

    public PublicationChoiceEditor(MarginInfo marginInfo) {
        createPublicationChoiceEditor(marginInfo);
    }

    public void createPublicationChoiceEditor(MarginInfo marginInfo) {
        Label publicationMainLabel = new Label("Publisering");
        addComponentAsFirst(publicationMainLabel);
        noPublication = new CheckBox("Bokmål");
        nnPublication = new CheckBox("Nynorsk");
        enPublication = new CheckBox("Engelsk");
        VerticalLayout publicationChoices = new VerticalSpacedLayout(
                noPublication,
                nnPublication,
                enPublication);
        publicationChoices.setMargin(new MarginInfo(true, false, true, true));
        addComponent(publicationChoices);
        setMargin(marginInfo);
        publicationMainLabel.setStyleName("v-label-bold");
        publicationChoices.addStyleName(KlassTheme.HORIZONTAL_LINE);
    }

    public void init(boolean isAdmin, Publishable publishableClassification) {
        publishable = publishableClassification;
        setValues(isAdmin, publishable);
    }

    private void setValues(boolean isAdmin, Publishable publishableClassification) {
        noPublication.setValue(publishableClassification.isPublished(Language.NB));
        nnPublication.setValue(publishableClassification.isPublished(Language.NN));
        enPublication.setValue(publishableClassification.isPublished(Language.EN));
        disableIfChecedIfNotAdmin(isAdmin, noPublication);
        disableIfChecedIfNotAdmin(isAdmin, nnPublication);
        disableIfChecedIfNotAdmin(isAdmin, enPublication);
    }

    public boolean isAnyPublishBoxChecked() {
        return nnPublication.getValue()
                || noPublication.getValue()
                || enPublication.getValue();
    }

    private void disableIfChecedIfNotAdmin(boolean isAdmin, CheckBox checkBox) {
        if (checkBox.getValue() && !isAdmin) {
            checkBox.setEnabled(false);
        }

    }

    private void removeErrorMarkings() {
        noPublication.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        nnPublication.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
        enPublication.removeStyleName(KlassTheme.TEXTFIELD_ERROR);
    }

    public boolean checkAndSetPublished(boolean isAdministrator, Publishable publishableClassification) {
        removeErrorMarkings();
        return checkAndSetPublish(noPublication, publishableClassification, Language.NB, isAdministrator)
                && checkAndSetPublish(nnPublication, publishableClassification, Language.NN, isAdministrator)
                && checkAndSetPublish(enPublication, publishableClassification, Language.EN, isAdministrator);
    }

    private boolean checkAndSetPublish(CheckBox checkBox, Publishable publishableClassification, Language language,
            boolean isAdministrator) {
        if (checkBox.getValue()) {
            String notTranslatedFields = publishableClassification.canPublish(language);
            if (notTranslatedFields.isEmpty()) {
                publishableClassification.publish(language);
            } else {
                checkBox.setValue(false);
                checkBox.addStyleName(KlassTheme.TEXTFIELD_ERROR);
                Notification.show("Følgende felt/operasjoner må utføres før publisering: " + notTranslatedFields + "\n",
                        Notification.Type.WARNING_MESSAGE);
                return false;
            }
        } else {
            if (isAdministrator) {
                publishableClassification.unpublish(language);
            }
        }
        return true;
    }

    public boolean hasChanges() {
        EqualsBuilder builder = new EqualsBuilder()
                .append(noPublication.getValue(), (Boolean) publishable.isPublished(Language.NB))
                .append(nnPublication.getValue(), (Boolean) publishable.isPublished(Language.NN))
                .append(enPublication.getValue(), (Boolean) publishable.isPublished(Language.EN));
        return !builder.isEquals();
    }
}
