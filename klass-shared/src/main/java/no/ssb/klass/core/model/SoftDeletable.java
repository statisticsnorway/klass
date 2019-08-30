package no.ssb.klass.core.model;

/**
 * Entities implementing this are only soft deleted. I.e. when deleted is just marked as deleted, not actually removed
 * from database.
 */
public interface SoftDeletable {
    void setDeleted();

    boolean isDeleted();
}
