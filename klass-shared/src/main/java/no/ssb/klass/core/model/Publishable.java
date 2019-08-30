package no.ssb.klass.core.model;

public interface Publishable {
    boolean isPublishedInAnyLanguage();
    boolean isPublished(Language languag);
    void unpublish(Language language);
    void publish(Language language);
    String canPublish(Language language);
}
