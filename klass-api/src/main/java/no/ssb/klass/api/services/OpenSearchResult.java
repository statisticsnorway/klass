package no.ssb.klass.api.services;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "klass")
public class OpenSearchResult {

  @Id private String uuid;

  @Field(name = "itemid", type = FieldType.Long)
  @JsonProperty("itemid")
  private Long itemId;

  private String type;
  private String language;
  private boolean copyrighted;
  private boolean published;
  private String title;
  private String description;
  private String family;
  private List<String> codes;
  private double score;

  public OpenSearchResult() {}

  public OpenSearchResult(
      String uuid,
      Long itemId,
      String type,
      String language,
      boolean copyrighted,
      boolean published,
      String title,
      String description,
      String family,
      List<String> codes) {
    this.uuid = uuid;
    this.itemId = itemId;
    this.type = type;
    this.language = language;
    this.copyrighted = copyrighted;
    this.published = published;
    this.title = title;
    this.description = description;
    this.family = family;
    this.codes = codes;
  }

  public String getUuid() {
    return uuid;
  }

  public void setUuid(String uuid) {
    this.uuid = uuid;
  }

  public Long getItemId() {
    return itemId;
  }

  public void setItemId(Long itemId) {
    this.itemId = itemId;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }

  public boolean isCopyrighted() {
    return copyrighted;
  }

  public void setCopyrighted(boolean copyrighted) {
    this.copyrighted = copyrighted;
  }

  public boolean isPublished() {
    return published;
  }

  public void setPublished(boolean published) {
    this.published = published;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getFamily() {
    return family;
  }

  public void setFamily(String family) {
    this.family = family;
  }

  public List<String> getCodes() {
    return codes;
  }

  public void setCodes(List<String> codes) {
    this.codes = codes;
  }

  public double getScore() {
    return score;
  }

  public void setScore(double score) {
    this.score = score;
  }
}
