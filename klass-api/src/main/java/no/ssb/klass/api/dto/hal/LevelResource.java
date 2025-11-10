package no.ssb.klass.api.dto.hal;

import java.util.List;
import java.util.stream.Collectors;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;

public class LevelResource {
  private final int levelNumber;
  private final String levelName;

  public LevelResource(Level level, Language language) {
    this.levelNumber = level.getLevelNumber();
    this.levelName = level.getName(language);
  }

  public int getLevelNumber() {
    return levelNumber;
  }

  public String getLevelName() {
    return levelName;
  }

  public static List<LevelResource> convert(List<Level> levels, Language language) {
    return levels.stream()
        .map(level -> new LevelResource(level, language))
        .collect(Collectors.toList());
  }
}
