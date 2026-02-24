package no.ssb.klass.designer.util;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;

/**
 * Lists expected properties to be found in the domain classes. Use this enum instead of strings for referring to
 * properties in Vaadin classes.
 * <p>
 * A unit test is written that ensures that the properties actually exists in the domain classes, giving sort of type
 * safety.
 */
public enum DomainProperty {
    CLASSIFICATION_NAME(ClassificationSeries.class, "nameInPrimaryLanguage"),
    VERSION_NAME(ClassificationVersion.class, "nameInPrimaryLanguage"),
    VARIANT_NAME(ClassificationVariant.class, "nameInPrimaryLanguage");

    private final Class<?> clazz;
    private final String property;

    DomainProperty(Class<?> clazz, String property) {
        this.clazz = clazz;
        this.property = property;
    }

    Class<?> getClazz() {
        return clazz;
    }

    public String getProperty() {
        return property;
    }
}
