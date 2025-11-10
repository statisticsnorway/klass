package no.ssb.klass.api.dto.hal;

import static java.util.stream.Collectors.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import java.util.List;
import no.ssb.klass.core.model.Changelog;

public class ChangelogResource {
  private final Date changeOccured;
  private final String description;

  public ChangelogResource(Changelog changelog) {
    this.changeOccured = changelog.getChangeOccured();
    this.description = changelog.getDescription();
  }

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
  public Date getChangeOccured() {
    return changeOccured;
  }

  public String getDescription() {
    return description;
  }

  public static List<ChangelogResource> convert(List<Changelog> changelogs) {
    return changelogs.stream().map(changelog -> new ChangelogResource(changelog)).collect(toList());
  }
}
