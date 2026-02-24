package no.ssb.klass.designer.service;

import java.util.Optional;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationEntityOperations;

/**
 * Holds whether subscribers of classification shall be informed of change done to classification.
 * <p>
 * The rules are:
 * <ul>
 * <li>If changed version/variant/correspondenceTable was unpublished, but changed to published, subscribers shall be
 * informed</li>
 * <li>If changed version/variant/correspondenceTable was unpublished, and after change still unpublished, subscribers
 * shall not be informed</li>
 * <li>If changed version/variant/correspondenceTable is published, and operator provides a ChangeLog, subscribers shall
 * be informed</li>
 * <li>If changed version/variant/correspondenceTable is published, and operator does not provide a ChangeLog,
 * subscribers shall not be informed. It is then assumed that change was related to unpublished language</li>
 * </ul>
 */
public final class InformSubscribers {
    private final boolean informSubscribers;
    private final String descriptionOfChange;

    private InformSubscribers(boolean relevantChange, String descriptionOfChange) {
        this.informSubscribers = relevantChange;
        this.descriptionOfChange = descriptionOfChange;
    }

    /**
     * true if subscribers shall be informed of change, false otherwise
     */
    public boolean isInformSubscribers() {
        return informSubscribers;
    }

    /**
     * Description of change that shall be presented to subscribers
     */
    public String getDescriptionOfChange() {
        return descriptionOfChange;
    }

    /**
     * Creates a new instance which dictates that subscribers shall not be informed of change
     */
    public static InformSubscribers createNotInformSubscribers() {
        return new InformSubscribers(false, "");
    }

    /**
     * Creates a new instance for an originally unpublished version/variant/correspondenceTable. Will dictate to inform
     * subscribers if updated to published.
     */
    public static InformSubscribers createWhenWasUnpublished(ClassificationEntityOperations classificationEntity) {
        if (classificationEntity.isPublishedInAnyLanguage()) {
            return new InformSubscribers(true, classificationEntity.getNameInPrimaryLanguage()
                    + " er blitt publisert.");
        } else {
            return new InformSubscribers(false, "");
        }
    }

    /**
     * Creates a new instance for an originally published version/variant/correspondenceTable. Will dictate to inform
     * subscribers if an ChangeLog is provided. If no ChangeLog is provided it is then assumed that change was related
     * to unpublished language.
     */
    public static InformSubscribers createWhenPublished(Optional<Changelog> changelog) {
        if (changelog.isPresent()) {
            return new InformSubscribers(true, changelog.get().getDescription());
        } else {
            return new InformSubscribers(false, "");
        }
    }
}
