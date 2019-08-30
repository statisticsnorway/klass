package no.ssb.klass.core.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class Published {
    @Column(nullable = false)
    private final boolean published_no;
    @Column(nullable = false)
    private final boolean published_nn;
    @Column(nullable = false)
    private final boolean published_en;

    /*
     * Only to be used by Hibernate
     */
    protected Published() {
        this.published_no = false;
        this.published_nn = false;
        this.published_en = false;
    }

    public Published(boolean no, boolean nn, boolean en) {
        this.published_no = no;
        this.published_nn = nn;
        this.published_en = en;
    }

    public Published publish(Language language) {
        return publishInternal(language, true);
    }

    public Published unpublish(Language language) {
        return publishInternal(language, false);
    }

    private Published publishInternal(Language language, boolean publish) {
        boolean tmpNo = published_no;
        boolean tmpNn = published_nn;
        boolean tmpEn = published_en;

        switch (language) {
        case NB:
            tmpNo = publish;
            break;
        case NN:
            tmpNn = publish;
            break;
        case EN:
            tmpEn = publish;
            break;
        default:
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
        return new Published(tmpNo, tmpNn, tmpEn);
    }

    public boolean isPublished(Language language) {
        switch (language) {
        case NB:
            return published_no;
        case NN:
            return published_nn;
        case EN:
            return published_en;
        default:
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    public boolean isPublishedInAnyLanguage() {
        for (Language language : Language.values()) {
            if (isPublished(language)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "Published [no=" + published_no + ", nn=" + published_nn + ", en=" + published_en + "]";
    }

    public static Published none() {
        return new Published(false, false, false);
    }
}
