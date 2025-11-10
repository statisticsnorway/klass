package no.ssb.klass.api.dto;

import static java.util.stream.Collectors.*;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;

@JacksonXmlRootElement(localName = "codeChangeList")
public class CodeChangeList {
  private final char csvSeparator;
  private final List<CodeChangeItem> codeChanges;
  private List<String> csvFields;

  public CodeChangeList(String csvSeparator) {
    if (csvSeparator.toCharArray().length != 1) {
      throw new IllegalArgumentException("Separator must be a single character");
    }
    this.csvSeparator = csvSeparator.charAt(0);
    this.codeChanges = new ArrayList<>();
  }

  public CodeChangeList(char csvSeparator, List<CodeChangeItem> codeChanges) {
    this.csvSeparator = csvSeparator;
    this.codeChanges = codeChanges;
  }

  @JacksonXmlElementWrapper(useWrapping = false)
  @JacksonXmlProperty(localName = "codeChangeItem")
  public List<CodeChangeItem> getCodeChanges() {
    return codeChanges;
  }

  @JsonIgnore
  public char getCsvSeparator() {
    return csvSeparator;
  }

  @JsonIgnore
  public List<String> getCsvFields() {
    return csvFields;
  }

  @JsonIgnore
  public void setCsvFields(List<String> csvFields) {
    this.csvFields = csvFields;
  }

  public CodeChangeList convert(CorrespondenceTable correspondenceTable, Language language) {
    LocalDate changeOccurred = correspondenceTable.getOccured();
    boolean isTargetOldest =
        correspondenceTable
            .getTarget()
            .getDateRange()
            .getFrom()
            .isBefore(correspondenceTable.getSource().getDateRange().getFrom());
    return new CodeChangeList(
        csvSeparator,
        correspondenceTable.getCorrespondenceMaps().stream()
            .map(
                correspondenceMap ->
                    new CodeChangeItem(correspondenceMap, isTargetOldest, changeOccurred, language))
            .collect(toList()));
  }

  public CodeChangeList merge(CodeChangeList other) {
    List<CodeChangeItem> combined = new ArrayList<>(codeChanges);
    combined.addAll(other.getCodeChanges());
    return new CodeChangeList(csvSeparator, combined);
  }
}
