package no.ssb.klass.core.model;

public interface ClassificationEntityOperations extends SoftDeletable {
    User getContactPerson();

    boolean isPublishedInAnyLanguage();

    String getNameInPrimaryLanguage();

    Language getPrimaryLanguage();

    String getCategoryName();

    ClassificationSeries getOwnerClassification();

    String getUuid();
}
