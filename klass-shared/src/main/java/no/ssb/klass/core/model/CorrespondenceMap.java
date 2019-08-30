package no.ssb.klass.core.model;

import java.util.Comparator;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "source_id", "target_id", "correspondence_table_id" }))
public class CorrespondenceMap extends BaseEntity implements Comparable<CorrespondenceMap> {
    private static Comparator<String> nullSafeStringComparator = Comparator.nullsFirst(String::compareToIgnoreCase);

    @OneToOne
    private ClassificationItem source;
    @OneToOne
    private ClassificationItem target;
    @ManyToOne(optional = false)
    private CorrespondenceTable correspondenceTable;

    public CorrespondenceMap(ClassificationItem source, ClassificationItem target) {
        assertBothNotNull(source, target);
        this.source = source;
        this.target = target;
    }

    public Optional<ClassificationItem> getSource() {
        return Optional.ofNullable(source);
    }

    public Optional<ClassificationItem> getTarget() {
        return Optional.ofNullable(target);
    }

    void setSource(ClassificationItem source) {
        assertBothNotNull(source, target);
        this.source = source;
    }

    void setTarget(ClassificationItem target) {
        assertBothNotNull(source, target);
        this.target = target;
    }

    public CorrespondenceTable getCorrespondenceTable() {
        return correspondenceTable;
    }

    void setCorrespondenceTable(CorrespondenceTable correspondenceTable) {
        this.correspondenceTable = correspondenceTable;
    }

    public boolean isDeleted() {
        return correspondenceTable.isDeleted();
    }

    @Override
    public int compareTo(CorrespondenceMap other) {
        int compare = nullSafeStringComparator.compare(getCodeFromItem(source), getCodeFromItem(other.source));
        if (compare == 0) {
            compare = nullSafeStringComparator.compare(getCodeFromItem(target), getCodeFromItem(other.target));
        }
        return compare;
    }

    boolean hasSameSourceAndTarget(CorrespondenceMap otherMap) {
        return compareTo(otherMap) == 0;
    }

    private String getCodeFromItem(ClassificationItem item) {
        if (item == null) {
            return null;
        }
        return item.getCode();
    }

    private void assertBothNotNull(ClassificationItem source, ClassificationItem target) {
        if (source == null && target == null) {
            throw new IllegalArgumentException("Both source and target is null");
        }
    }

    @Override
    public String toString() {
        return "CorrespondenceMap [source=" + source + ", target=" + target + "]";
    }
}