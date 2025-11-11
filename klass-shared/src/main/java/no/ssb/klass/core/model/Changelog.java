package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import no.ssb.klass.core.util.TimeUtil;

import java.util.Date;

@Entity
public class Changelog extends BaseEntity {
    @Column(nullable = false)
    private Date changeOccured;

    @Column(nullable = false)
    private String changedBy;

    @Column(length = 4096, nullable = false)
    private String description;

    public Changelog(String changedBy, String description) {
        this.changeOccured = TimeUtil.now();
        this.changedBy = checkNotNull(changedBy);
        this.description = checkNotNull(description);
    }

    protected Changelog() {}

    public Date getChangeOccured() {
        return changeOccured;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public String getDescription() {
        return description;
    }
}
