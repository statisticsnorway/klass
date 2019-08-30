package no.ssb.klass.core.model;

import java.util.Date;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.PreUpdate;
import javax.persistence.Version;

import no.ssb.klass.core.util.TimeUtil;

@MappedSuperclass
public abstract class BaseEntity {
    @Id
    @GeneratedValue
    private Long id;
    @Column(nullable = false)
    private final String uuid;
    @Column(nullable = false)
    private Date lastModified;
    @Version
    private int version;

    protected BaseEntity() {
        uuid = UUID.randomUUID().toString();
        lastModified = TimeUtil.now();
    }

    public Long getId() {
        return id;
    }

    public String getUuid() {
        return uuid;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @PreUpdate
    protected void update() {
        lastModified = TimeUtil.now();
    }

    @Override
    public int hashCode() {
        return uuid.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (getClass() != other.getClass()) {
            return false;
        }

        return uuid.equals(((BaseEntity) other).uuid);
    }

    /**
     * Any initialisation needed after Hibernate has recreated instance. Typically initialise any transient fields.
     */
    public void init() {
    }
}
