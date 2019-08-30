package no.ssb.klass.core.util;

import static com.google.common.base.Preconditions.*;

import java.io.Serializable;
import java.util.Objects;

import com.google.common.base.Strings;

import no.ssb.klass.core.model.Language;

public class Translatable implements Serializable {
    private final String no;
    private final String nn;
    private final String en;
    private static final Translatable EMPTY_TRANSLATABLE = new Translatable(null, null, null);

    public Translatable(String no, String nn, String en) {
        this.no = no;
        this.nn = nn;
        this.en = en;
    }

    public boolean hasLanguage(Language language) {
        return getStringInternal(language) != null;
    }

    public String getString(Language language) {
        String string = getStringInternal(language);
        return string == null ? "" : string;
    }

    public String getStringOrNull(Language language) {
        return getStringInternal(language);
    }

    private String getStringInternal(Language language) {
        checkNotNull(language);
        switch (language) {
        case NB:
            return no;
        case NN:
            return nn;
        case EN:
            return en;
        default:
            throw new IllegalArgumentException("Unsupported language: " + language);
        }
    }

    public Translatable withLanguage(String string, Language language) {
        String tmpNo = no;
        String tmpNn = nn;
        String tmpEn = en;
        switch (language) {
        case NB:
            tmpNo = string;
            break;
        case NN:
            tmpNn = string;
            break;
        case EN:
            tmpEn = string;
            break;
        default:
            throw new IllegalArgumentException("Unsupported language: " + language);
        }

        return new Translatable(tmpNo, tmpNn, tmpEn);
    }

    public boolean isEmpty() {
        if (Strings.isNullOrEmpty(no) && Strings.isNullOrEmpty(nn) && Strings.isNullOrEmpty(en)) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Translatable [no=" + no + ", nn=" + nn + ", en=" + en + "]";
    }

    public static Translatable create(String string, Language language) {
        return empty().withLanguage(string, language);
    }

    public static Translatable empty() {
        return EMPTY_TRANSLATABLE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(no, nn, en);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        Translatable other = (Translatable) obj;
        return Objects.equals(this.no, other.no) && Objects.equals(this.nn, other.nn) && Objects.equals(this.en,
                other.en);
    }
}
