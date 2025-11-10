package no.ssb.klass.core.model;

import static org.junit.jupiter.api.Assertions.*;

import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.TestUtil;
import org.junit.jupiter.api.Test;

public class ClassificationVariantTest {

  @Test
  public void hasTwoLevels() {
    // given
    ClassificationVariant subject =
        new ClassificationVariant(Translatable.create("name", Language.NB), TestUtil.createUser());

    // then
    assertEquals(2, subject.getLevels().size());
  }

  @Test
  public void isIncludeShortName() {
    // given
    ClassificationVariant subject =
        new ClassificationVariant(Translatable.create("name", Language.NB), TestUtil.createUser());

    // then
    assertEquals(false, subject.isIncludeShortName());
  }

  @Test
  public void isIncludeNotes() {
    // given
    ClassificationVariant subject =
        new ClassificationVariant(Translatable.create("name", Language.NB), TestUtil.createUser());

    // then
    assertEquals(false, subject.isIncludeNotes());
  }

  @Test
  public void addLevel() {
    // given
    ClassificationVariant subject =
        new ClassificationVariant(Translatable.create("name", Language.NB), TestUtil.createUser());

    // when
    Level level = new Level(3);
    subject.addLevel(level);

    // then expect exception
    assertEquals(subject.getLastLevel().get(), level);
  }
}
