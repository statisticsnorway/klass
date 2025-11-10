package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

/**
 * @author Mads Lundemo, SSB.
 */
@Entity
public class StatisticalUnit extends BaseEntity {
  @Column(nullable = false)
  @Convert(converter = TranslatablePersistenceConverter.class)
  private Translatable name;

  protected StatisticalUnit() {}

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
