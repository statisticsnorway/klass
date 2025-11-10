package no.ssb.klass.core.model;

import static com.google.common.base.Preconditions.*;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import no.ssb.klass.core.util.Translatable;

/** A ClassificationVariant supports 2 levels only. The 2 levels are created when constructed. */
@Entity
@DiscriminatorValue("variant")
public class ClassificationVariant extends StatisticalClassification {

  private static final String VARIANT_NAME_SEPERATOR_NB = " - variant av ";
  private static final String VARIANT_NAME_SEPERATOR_NN = " - variant av ";
  private static final String VARIANT_NAME_SEPERATOR_EN = " - variant of ";

  @ManyToOne private ClassificationVersion classificationVersion;
  private Translatable name;
  @ManyToOne private User contactPerson;

  /** Creates a ClassificationVariant. Note that 2 levels are added. */
  public ClassificationVariant(Translatable name, User contactPerson) {
    super(Translatable.empty());
    this.name = checkNotNull(name);
    this.contactPerson = checkNotNull(contactPerson);
    checkArgument(!name.isEmpty(), "Name is empty");
    super.addLevel(new Level(1));
    super.addLevel(new Level(2));
  }

  protected ClassificationVariant() {}

  public void setClassificationVersion(ClassificationVersion classificationVersion) {
    this.classificationVersion = classificationVersion;
  }

  public ClassificationVersion getClassificationVersion() {
    return classificationVersion;
  }

  @Override
  public Language getPrimaryLanguage() {
    return classificationVersion.getClassification().getPrimaryLanguage();
  }

  @Override
  public String getCategoryName() {
    return "Variant";
  }

  @Override
  public ClassificationSeries getOwnerClassification() {
    return classificationVersion.getClassification();
  }

  public String getNameBase(Language language) {
    return name.getString(language);
  }

  public void setNameBase(Language language, String value) {
    name = name.withLanguage(value, language);
  }

  public String getFullName(Language language) {
    return getNameBase(language) + getFullPostfix(language);
  }

  private String getFullPostfix(Language language) {
    String postfix = getVariantPostfix(language) + classificationVersion.getName(language);
    String date = getDatePostfix(validFrom, validTo);
    return ' ' + date + ' ' + postfix;
  }

  public static String getVariantPostfix(Language language) {
    switch (language) {
      case NB:
        return VARIANT_NAME_SEPERATOR_NB;
      case NN:
        return VARIANT_NAME_SEPERATOR_NN;
      case EN:
        return VARIANT_NAME_SEPERATOR_EN;
      default:
        return "";
    }
  }

  @Override
  public String getNameInPrimaryLanguage() {
    return getFullName(getPrimaryLanguage());
  }

  @Override
  public User getContactPerson() {
    return contactPerson;
  }

  public void updateContactPerson(User contactPerson) {
    this.contactPerson = checkNotNull(contactPerson);
  }

  @Override
  public boolean isIncludeShortName() {
    return false;
  }

  @Override
  public boolean isIncludeNotes() {
    return false;
  }

  @Override
  public boolean isDeleted() {
    return super.isDeleted() || classificationVersion.isDeleted();
  }

  @Override
  public String canPublish(Language language) {
    if (!classificationVersion.isPublished(language)) {
      return "Publiser versjon '" + classificationVersion.getName(language) + "'";
    } else {
      return "";
    }
  }
}
