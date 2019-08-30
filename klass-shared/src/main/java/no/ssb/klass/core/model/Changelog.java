package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;

import no.ssb.klass.core.util.TimeUtil;

@Entity
public class Changelog extends BaseEntity {
    @Column(nullable = false)
    private final Date changeOccured;
    @Column(nullable = false)
    private final String changedBy;
    @Column(length = 4096, nullable = false)
    private final String description;

    public Changelog(String changedBy, String description) {
        this.changeOccured = TimeUtil.now();
        this.changedBy = checkNotNull(changedBy);
        this.description = checkNotNull(description);
    }

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
