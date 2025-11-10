package no.ssb.klass.api.dto.hal;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.Language;

@JsonPropertyOrder(
    value = {
      "sourceCode",
      "sourceName",
      "targetCode",
      "targetName",
    })
public class CorrespondenceMapResource {
  private final String sourceCode;
  private final String sourceName;
  private final String targetCode;
  private final String targetName;

  public CorrespondenceMapResource(CorrespondenceMap correspondenceMap, Language language) {
    ClassificationItem sourceItem = correspondenceMap.getSource().orElse(null);
    this.sourceCode = sourceItem == null ? null : sourceItem.getCode();
    this.sourceName = sourceItem == null ? null : sourceItem.getOfficialName(language);

    ClassificationItem targetItem = correspondenceMap.getTarget().orElse(null);
    this.targetCode = targetItem == null ? null : targetItem.getCode();
    this.targetName = targetItem == null ? null : targetItem.getOfficialName(language);
  }

  public String getSourceCode() {
    return sourceCode;
  }

  public String getSourceName() {
    return sourceName;
  }

  public String getTargetCode() {
    return targetCode;
  }

  public String getTargetName() {
    return targetName;
  }

  /*
   * JAVA streams and comparators are not to happy about null values and while you got the nullFirst wrapper it wont
   * help when you feed the inner comparator with the method to call to get values
   */
  private String getNullSafeSourceCode() {
    return sourceCode == null ? "" : sourceCode;
  }

  private String getNullSafeTargetCode() {
    return targetCode == null ? "" : targetCode;
  }

  public static List<CorrespondenceMapResource> convert(
      List<CorrespondenceMap> correspondenceMaps, Language language) {
    return correspondenceMaps.stream()
        .map(correspondenceMap -> new CorrespondenceMapResource(correspondenceMap, language))
        .sorted(
            Comparator.comparing(CorrespondenceMapResource::getNullSafeSourceCode)
                .thenComparing(
                    Comparator.comparing(CorrespondenceMapResource::getNullSafeTargetCode)))
        .collect(Collectors.toList());
  }
}
