package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import com.google.common.base.Strings;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import java.time.LocalDate;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

@Entity
public class ConcreteClassificationItem extends ClassificationItem {
  private String code;

  @Column(length = 2048)
  @Convert(converter = TranslatablePersistenceConverter.class)
  private Translatable officialName;

  @Column(length = 1024)
  @Convert(converter = TranslatablePersistenceConverter.class)
  private Translatable shortName;

  @Column(length = 6000)
  @Convert(converter = TranslatablePersistenceConverter.class)
  private Translatable notes;

  @Column protected LocalDate validFrom;
  @Column protected LocalDate validTo;

  protected ConcreteClassificationItem() {}

  public ConcreteClassificationItem(
      String code, Translatable officialName, Translatable shortName) {
    this(code, officialName, shortName, Translatable.empty());
  }

  public ConcreteClassificationItem(
      String code, Translatable officialName, Translatable shortName, Translatable notes) {
    this(code, officialName, shortName, notes, null, null);
  }

  public ConcreteClassificationItem(
      String code,
      Translatable officialName,
      Translatable shortName,
      Translatable notes,
      LocalDate validFrom,
      LocalDate validTo) {
    checkArgument(!Strings.isNullOrEmpty(code));
    checkNotNull(officialName);
    checkArgument(!officialName.isEmpty());
    this.code = code;
    this.officialName = officialName;
    this.shortName = checkNotNull(shortName);
    this.notes = checkNotNull(notes);
    this.validFrom = validFrom;
    this.validTo = validTo;
  }

  /**
   * @return code, never null
   */
  @Override
  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = checkNotNull(code);
  }

  /**
   * @return official name for specified language, if none empty string is returned, never null
   */
  @Override
  public String getOfficialName(Language language) {
    return officialName.getString(language);
  }

  public void setOfficialName(String officialName, Language language) {
    checkNotNull(officialName);
    this.officialName = this.officialName.withLanguage(officialName, language);
  }

  /**
   * @return short name for specified language, if none empty string is returned, never null
   */
  @Override
  public String getShortName(Language language) {
    return shortName.getString(language);
  }

  public void setShortName(String shortName, Language language) {
    this.shortName = this.shortName.withLanguage(shortName, language);
  }

  public void setNotes(String notes, Language language) {
    this.notes = this.notes.withLanguage(notes, language);
  }

  @Override
  public String getNotes(Language language) {
    return notes.getString(language);
  }

  public LocalDate getValidFrom() {
    return validFrom;
  }

  public void setValidFrom(LocalDate validFrom) {
    this.validFrom = validFrom;
  }

  @Override
  public LocalDate getValidTo() {
    return validTo;
  }

  public void setValidTo(LocalDate validTo) {
    this.validTo = validTo;
  }

  @Override
  public ClassificationItem copy() {
    ConcreteClassificationItem copy = new ConcreteClassificationItem(code, officialName, shortName);
    copy.notes = notes;
    copy.validFrom = validFrom;
    copy.validTo = validTo;
    return copy;
  }

  @Override
  public boolean isReference() {
    return false;
  }

  @Override
  boolean hasNotes() {
    return !notes.isEmpty();
  }

  @Override
  boolean hasShortName() {
    return !shortName.isEmpty();
  }

  @Override
  public String toString() {
    return "ConcreteClassificationItem [code=" + code + "]";
  }
}
