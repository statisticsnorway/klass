package no.ssb.klass.api.dto.hal;

import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "ssbSections")
public class SsbSectionResource extends KlassResource {
  private final String name;

  public SsbSectionResource(String name) {
    super(null);
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
