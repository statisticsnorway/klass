package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class ReferencingClassificationItem extends ClassificationItem {
    @OneToOne
    private final ClassificationItem reference;

    // For Hibernate
    protected ReferencingClassificationItem() {
        this.reference = null;
    }

    public ReferencingClassificationItem(ClassificationItem classificationItem) {
        this.reference = checkNotNull(classificationItem);
    }

    @Override
    public String getCode() {
        return reference.getCode();
    }

    @Override
    public String getOfficialName(Language language) {
        return reference.getOfficialName(language);
    }

    @Override
    public String getShortName(Language language) {
        return reference.getShortName(language);
    }

    @Override
    public String getNotes(Language language) {
        return reference.getNotes(language);
    }

    @Override
    public LocalDate getValidTo() {
        return reference.getValidTo();
    }

    @Override
    public LocalDate getValidFrom() {
        return reference.getValidFrom();
    }

    @Override
    public ClassificationItem copy() {
        return new ReferencingClassificationItem(reference);
    }

    @Override
    public boolean isReference() {
        return true;
    }

    @Override
    public String getUuid() {
        return reference.getUuid();
    }

    @Override
    boolean hasNotes() {
        return false;
    }

    @Override
    boolean hasShortName() {
        return false;
    }

    public ClassificationItem getReferenceItem() {
        return reference;
    }

    @Override
    public String toString() {
        return "ReferencingClassificationItem [reference=" + reference + "]";
    }
}
