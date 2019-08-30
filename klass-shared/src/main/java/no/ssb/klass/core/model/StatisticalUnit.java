package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import javax.persistence.Column;
import javax.persistence.Entity;

import no.ssb.klass.core.util.Translatable;

/**
 * @author Mads Lundemo, SSB.
 */
@Entity
public class StatisticalUnit extends BaseEntity {
    @Column(nullable = false)
    private Translatable name;

    public StatisticalUnit(Translatable name) {
        this.name = checkNotNull(name);
        checkArgument(!name.isEmpty(), "Name is empty");
    }

    public String getName(Language language) {
        return name.getString(language);
    }

    public void setName(String value, Language language) {
        checkNotNull(value);
        name = name.withLanguage(value, language);
    }
}
