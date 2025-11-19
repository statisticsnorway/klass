package no.ssb.klass.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;

import no.ssb.klass.core.util.AlphaNumericCompareUtil;

import java.time.LocalDate;

@Entity
public abstract class ClassificationItem extends BaseEntity
        implements Comparable<ClassificationItem> {
    @ManyToOne private ClassificationItem parent;

    @ManyToOne(optional = false)
    private Level level;

    protected ClassificationItem() {}

    void setParent(ClassificationItem parent) {
        this.parent = parent;
    }

    /**
     * @return code, never null
     */
    @Column(nullable = false)
    public abstract String getCode();

    /**
     * @return official name for specified language, if none empty string is returned, never null
     */
    public abstract String getOfficialName(Language language);

    /**
     * @return short name for specified language, if none empty string is returned, never null
     */
    public abstract String getShortName(Language language);

    public Level getLevel() {
        return level;
    }

    void setLevel(Level level) {
        this.level = level;
    }

    public ClassificationItem getParent() {
        return parent;
    }

    public abstract String getNotes(Language language);

    public abstract LocalDate getValidTo();

    public abstract LocalDate getValidFrom();

    public abstract ClassificationItem copy();

    public abstract boolean isReference();

    @Override
    public String toString() {
        return "ClassificationItem [code="
                + getCode()
                + ", officialName="
                + getOfficialName(Language.NB)
                + "]";
    }

    public boolean isDeleted() {
        return getLevel().isDeleted();
    }

    @Override
    public int compareTo(ClassificationItem other) {
        return AlphaNumericCompareUtil.compare(getCode(), other.getCode());
    }

    protected abstract boolean hasNotes();

    protected abstract boolean hasShortName();
}
