package no.ssb.klass.core.service.dto;

public class ContentUseStatisticDto {

  public final int numberOfClassifications;
  public final int publishedClassifications;
  public final int unpublishedClassifications;
  public final int publishedVersionsWithMissingLanguages;

  public ContentUseStatisticDto(
      int numberOfClassifications,
      int publishedClassifications,
      int unpublishedClassifications,
      int publishedVersionsWithMissingLanguages) {

    this.numberOfClassifications = numberOfClassifications;
    this.publishedClassifications = publishedClassifications;
    this.unpublishedClassifications = unpublishedClassifications;
    this.publishedVersionsWithMissingLanguages = publishedVersionsWithMissingLanguages;
  }
}
