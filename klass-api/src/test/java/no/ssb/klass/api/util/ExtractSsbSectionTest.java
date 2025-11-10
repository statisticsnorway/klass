package no.ssb.klass.api.util;

import static org.assertj.core.api.Assertions.assertThat;

import com.google.common.base.Strings;
import org.junit.jupiter.api.Test;

public class ExtractSsbSectionTest {

  @Test
  public void ExtractSsbSectionCodeTest() {
    String ssbSection = "section - sectionname";
    String extracted = extractSsbSection(ssbSection);
    assertThat(extracted).isEqualTo("section");
    assertThat(extracted).isNotEqualTo("section - sectionname");

    String ssbSection2 = "320 - Seksjon for befolkningsstatistikk ";
    String extracted2 = extractSsbSection(ssbSection2);
    assertThat(extracted2).isEqualTo("320");

    String ssbSection3 = "320 - bv jkh ";
    String extracted3 = extractSsbSection(ssbSection3);
    assertThat(extracted3).isEqualTo("320");
  }

  // Mimic private method in controller
  private String extractSsbSection(String ssbSection) {
    if (Strings.isNullOrEmpty(ssbSection)) {
      return null;
    }
    String s = ssbSection.trim();
    String[] parts = s.split(" - ", 2);
    return parts[0];
  }
}
