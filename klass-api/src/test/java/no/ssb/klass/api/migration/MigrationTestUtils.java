package no.ssb.klass.api.migration;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import io.restassured.path.xml.XmlPath;
import io.restassured.response.Response;
import java.net.URI;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MigrationTestUtils {

  public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

  /**
   * Compare url from two sources ignoring host.
   *
   * @param sourceHref String path
   * @param targetHref String path
   * @return True if the two paths are the same, False otherwise
   */
  public static boolean isPathEqualIgnoreHost(String sourceHref, String targetHref) {
    try {
      URL sourceUrl = new URL(sourceHref);
      URL targetUrl = new URL(targetHref);

      if (!sourceUrl.getPath().equals(targetUrl.getPath())) {
        System.out.println(
            "Url path comparison issue: \nsource url path: "
                + sourceUrl.getPath()
                + "\ntarget url path: "
                + targetUrl.getPath());
      }
      return sourceUrl.getPath().equals(targetUrl.getPath());
    } catch (Exception e) {
      e.printStackTrace();
      return false;
    }
  }

  /**
   * Extracts the date part from a full datetime object.
   *
   * @param dateTime the full datetime object as input
   * @return the extracted date as a formatted string (yyyy-MM-dd)
   */
  private static String getDate(Object dateTime) {
    String dateTimeStr = Objects.toString(dateTime, "");
    OffsetDateTime odt =
        OffsetDateTime.parse(
            dateTimeStr, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ"));

    return odt.toLocalDate().format(formatter);
  }

  /**
   * Extracts the section number from a section object by converting it to string.
   *
   * @param section the object containing section number and name (converted to string)
   * @return the section number part before the " - " delimiter, or empty string if none
   */
  private static String getSectionNumber(Object section) {
    String sectionStr = Objects.toString(section, "");
    String[] parts = sectionStr.split(" - ", 2);

    return parts[0];
  }

  /**
   * Navigates a nested map using a dot-separated path and returns the value at that path.
   *
   * @param map the root map to search within
   * @param path dot-separated keys indicating the nested path
   * @return the value found at the given path, or null if any key is missing or not a map
   */
  private static Object resolvePath(Map<String, Object> map, String path) {
    String[] parts = path.split("\\.");
    Object current = map;

    for (String part : parts) {
      if (!(current instanceof Map)) {
        return null;
      }
      current = ((Map<?, ?>) current).get(part);
    }

    return current;
  }

  /**
   * Compare error response from source and target API.
   *
   * <p>Print the status code.
   *
   * @param sourceResponse Response object from source Api
   * @param targetResponse Response object from target Api
   */
  public static boolean compareError(Integer ID, Response sourceResponse, Response targetResponse) {
    Object sourceBody = sourceResponse.getBody().asString();
    Object targetBody = targetResponse.getBody().asString();
    System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
    if (sourceResponse.getStatusCode() != targetResponse.getStatusCode()
        || !sourceBody.equals(targetBody)) {
      String sourceError =
          (ID != null)
              ? ("Source: ID: "
                  + ID
                  + ", Code: "
                  + sourceResponse.getStatusCode()
                  + ", "
                  + sourceBody)
              : ("Source: " + "Code: " + sourceResponse.getStatusCode() + ", " + sourceBody);
      String targetError =
          (ID != null)
              ? ("Target: ID: "
                  + ID
                  + ", Code: "
                  + targetResponse.getStatusCode()
                  + ", "
                  + targetBody)
              : ("Target: " + "Code: " + targetResponse.getStatusCode() + ", " + targetBody);

      System.out.println(String.join(", ", sourceError) + "\n" + String.join(", ", targetError));
      return false;
    }
    return true;
  }

  /**
   * Generates a random date between the given start and end dates (inclusive).
   *
   * @param startDate the start of the date range
   * @param endDate the end of the date range
   * @return a random {@link LocalDate} between startDate and endDate
   */
  public static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
    long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
    Random random = new Random();
    long randomDay = random.nextLong(daysBetween + 1);
    return startDate.plusDays(randomDay);
  }

  /**
   * Generates a random integer between 0 (inclusive) and the specified upper bound (exclusive).
   *
   * @param to the upper bound (exclusive) for the generated random number
   * @return a random integer >= 0 and < to
   */
  public static Integer generateRandomId(int to) {
    Random random = new Random();
    return random.nextInt(to);
  }

  /**
   * Assert that source and target returns the same status code.
   *
   * @param sourceStatusCode Status code value of request to sourceHost
   * @param targetStatusCode Status code value of request to targetHost
   * @param path Path to the request
   */
  public static void assertStatusCodesEqual(
      int sourceStatusCode, int targetStatusCode, String path) {

    assertThat(sourceStatusCode)
        .withFailMessage(FAIL_MESSAGE, path, sourceStatusCode, targetStatusCode)
        .isEqualTo(targetStatusCode);
  }

  /**
   * Converts a list of maps into a map indexed by the specified field's value.
   *
   * @param list the list of maps to be transformed
   * @param field the key to use as the new map's keys
   * @return a map where each key is the value of the specified field from the list's items, and the
   *     value is the corresponding map from the list
   */
  private static Map<Object, Map<String, Object>> mapByField(
      List<Map<String, Object>> list, String field) {
    return list.stream()
        .filter(item -> item.containsKey(field) && item.get(field) != null)
        .collect(Collectors.toMap(item -> item.get(field), Function.identity()));
  }

  /**
   * Assert response is not null.
   *
   * @param response Response object from Api
   */
  public static void assertApiResponseIsNotNull(Response response) {
    assertThat(response).withFailMessage(API_EMPTY_RESPONSE_MESSAGE).isNotNull();
  }

  /**
   * Validate one single path object.
   *
   * @param sourceResponse Response object from source Api
   * @param targetResponse Response object from target Api
   * @param pathName Name for single path
   */
  public static void validateObject(
      Response sourceResponse, Response targetResponse, String pathName) {
    Object sourceField = sourceResponse.path(pathName);
    Object targetField = targetResponse.path(pathName);

    if (sourceField == null) {
      assertThat(targetField).withFailMessage(FAIL_MESSAGE, pathName, null, targetField).isNull();
    }

    if (pathName.equals(OWNING_SECTION)) {
      String sourceSectionNumber = getSectionNumber(sourceField);
      String targetSectionNumber = getSectionNumber(targetField);
      System.out.println(
          "Comparing owning section: " + sourceSectionNumber + " -> " + targetSectionNumber);
      assertThat(sourceSectionNumber)
          .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
          .isEqualTo(targetSectionNumber);
    } else if (pathName.endsWith(HREF)) {
      String sourceHref = Objects.toString(sourceField, "");
      String targetHref = Objects.toString(targetField, "");
      if (sourceHref.isEmpty()) {
        assertThat(sourceHref)
            .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
            .isEqualTo(targetHref);
        return;
      }
      System.out.println(sourceHref + " -> " + targetHref);
      assertThat(isPathEqualIgnoreHost(sourceHref, targetHref))
          .withFailMessage(FAIL_MESSAGE, pathName, sourceHref, targetHref)
          .isTrue();
    } else {
      System.out.println(sourceField + "-> " + targetField);
      assertThat(sourceField)
          .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
          .isEqualTo(targetField);
    }
  }

  /**
   * Validates that the list specified by [pathListName] from both API responses contains the same
   * elements, regardless of order.
   *
   * @param sourceResponse Response object from source Api
   * @param targetResponse Response object from target Api
   * @param pathListName List of path names in nested list
   */
  public static void validateList(
      Response sourceResponse, Response targetResponse, String pathListName) {

    ArrayList<String> sourceList = sourceResponse.path(pathListName);
    ArrayList<String> targetList = targetResponse.path(pathListName);

    System.out.println("Checking pathname: " + pathListName);

    if (sourceList == null) {
      assertThat(targetList).withFailMessage(FAIL_MESSAGE, pathListName, null, targetList).isNull();
      return;
    }

    assertThat(targetList)
        .withFailMessage(FAIL_MESSAGE, pathListName, sourceList, targetList)
        .isNotNull();

    System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());

    assertThat(sourceList.size())
        .withFailMessage(FAIL_MESSAGE, pathListName, sourceList.size(), targetList.size())
        .isEqualTo(targetList.size());

    assertThat(sourceList.containsAll(targetList))
        .withFailMessage(FAIL_MESSAGE, pathListName, sourceList, targetList)
        .isTrue();

    assertThat(targetList.containsAll(sourceList))
        .withFailMessage(FAIL_MESSAGE, pathListName, sourceList, targetList)
        .isTrue();
  }

  /**
   * Temporary validation for migrated section names. Ensure migrated sections follow the rules for
   * migration. When core method fetches section name from code list this validation is unnecessary.
   */
  public static void validateMigratedSsbSections(Response sourceResponse, Response targetResponse) {
    String pathListName = EMBEDDED_SSB_SECTIONS;
    ArrayList<Map<String, Object>> sourceList = sourceResponse.path(pathListName);
    ArrayList<Map<String, Object>> targetList = targetResponse.path(pathListName);

    System.out.println("Checking pathname: " + pathListName);

    if (sourceList == null) {
      assertThat(targetList).withFailMessage(FAIL_MESSAGE, pathListName, null, targetList).isNull();
      return;
    }

    assertThat(targetList)
        .withFailMessage(FAIL_MESSAGE, pathListName, sourceList, targetList)
        .isNotNull();

    System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());

    assertThat(sourceList.size())
        .withFailMessage(FAIL_MESSAGE, pathListName, sourceList.size(), targetList.size())
        .isEqualTo(targetList.size());

    for (int i = 0; i < sourceList.size(); i++) {
      String sourceName = getSectionNumber(sourceList.get(i).get("name").toString());
      String targetName = getSectionNumber(targetList.get(i).get("name").toString());
      System.out.println("Checking " + sourceName + " -> " + targetName);
      if (sourceName != null) {
        assertThat(sourceName)
            .withFailMessage(FAIL_MESSAGE, pathListName, sourceName, targetName)
            .isEqualTo(targetName);
      }
    }
  }

  /**
   * Validates that the values of fields specified in [pathNames] from two API response bodies are
   * equal. Handles url paths.
   *
   * @param sourceResponse Response object from source Api
   * @param targetResponse Response object from target Api
   * @param pathNames List of path names
   */
  public static void validateItems(
      Response sourceResponse, Response targetResponse, List<String> pathNames) {

    for (String pathName : pathNames) {
      System.out.println("Checking pathname: " + pathName);

      Object sourceField = sourceResponse.path(pathName);
      Object targetField = targetResponse.path(pathName);

      if (sourceField == null) {
        assertThat(targetField).withFailMessage(FAIL_MESSAGE, pathName, null, targetField).isNull();
      }

      if (pathName.equals(OWNING_SECTION)) {
        String sourceSectionNumber = getSectionNumber(sourceField);
        String targetSectionNumber = getSectionNumber(targetField);
        System.out.println(
            "Comparing owning section: " + sourceSectionNumber + " -> " + targetSectionNumber);
        assertThat(sourceSectionNumber)
            .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
            .isEqualTo(targetSectionNumber);
      } else if (pathName.endsWith(HREF)) {
        String sourceHref = Objects.toString(sourceField, "");
        String targetHref = Objects.toString(targetField, "");
        if (sourceHref.isEmpty()) {
          assertThat(sourceHref)
              .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
              .isEqualTo(targetHref);
          return;
        }
        System.out.println(sourceHref + " -> " + targetHref);
        assertThat(isPathEqualIgnoreHost(sourceHref, targetHref))
            .withFailMessage(FAIL_MESSAGE, pathName, sourceHref, targetHref)
            .isTrue();
      } else {
        System.out.println(sourceField + " -> " + targetField);
        assertThat(sourceField)
            .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
            .isEqualTo(targetField);
      }
    }
  }

  public static void validateFilteredClassifications(
      Response sourceResponse, Response targetResponse) {
    List<Map<String, Object>> sourceList = sourceResponse.path(CLASSIFICATIONS);
    List<Map<String, Object>> targetList = targetResponse.path(CLASSIFICATIONS);

    System.out.println("Checking list name: " + CLASSIFICATIONS);
    if (sourceList == null) {
      assertThat(targetList)
          .withFailMessage(FAIL_MESSAGE, CLASSIFICATIONS, null, targetList)
          .isNull();
      return;
    }

    assertThat(targetList)
        .withFailMessage(FAIL_MESSAGE, CLASSIFICATIONS, sourceList, targetList)
        .isNotNull();

    System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());

    assertThat(sourceList.size())
        .withFailMessage(
            "Expected size to be <%d> but was <%d>", targetList.size(), sourceList.size())
        .isEqualTo(targetList.size());
  }

  /**
   * Validates that path list [listName] with fields specified in [pathNames] from two API response
   * bodies are equal.
   *
   * @param sourceResponse Response object from source Api
   * @param targetResponse Response object from target Api
   * @param listName Name of list element in path
   * @param pathNames List of fields in listName list
   * @param idField A unique key for list objects
   */
  public static void validatePathListWithObjects(
      Response sourceResponse,
      Response targetResponse,
      String listName,
      List<String> pathNames,
      String idField) {
    List<Map<String, Object>> sourceList = sourceResponse.path(listName);
    List<Map<String, Object>> targetList = targetResponse.path(listName);

    System.out.println("Checking list name: " + listName);
    if (sourceList == null) {
      assertThat(targetList).withFailMessage(FAIL_MESSAGE, listName, null, targetList).isNull();
      return;
    }

    assertThat(targetList)
        .withFailMessage(FAIL_MESSAGE, listName, sourceList, targetList)
        .isNotNull();

    System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());

    assertThat(sourceList.size())
        .withFailMessage(
            "Expected size to be <%d> but was <%d>", sourceList.size(), targetList.size())
        .isEqualTo(targetList.size());

    Map<Object, Map<String, Object>> sourceById = mapByField(sourceList, idField);
    Map<Object, Map<String, Object>> targetById = mapByField(targetList, idField);

    for (Object versionId : sourceById.keySet()) {
      Map<String, Object> versionSource = sourceById.get(versionId);
      Map<String, Object> versionTarget = targetById.get(versionId);

      for (String pathName : pathNames) {

        System.out.println("Checking pathname: " + pathName);

        Object sourceField = resolvePath(versionSource, pathName);
        Object targetField = resolvePath(versionTarget, pathName);

        if (pathName.equals(OWNING_SECTION)) {
          String sourceSectionNumber = getSectionNumber(sourceField);
          String targetSectionNumber = getSectionNumber(targetField);
          System.out.println(
              "Comparing owning section: " + sourceSectionNumber + " -> " + targetSectionNumber);
          assertThat(sourceSectionNumber)
              .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
              .isEqualTo(targetSectionNumber);
        } else if (pathName.endsWith(HREF)) {
          assertThat(
                  sourceField == null && targetField == null
                      || sourceField != null
                          && targetField != null
                          && isPathEqualIgnoreHost(sourceField.toString(), targetField.toString()))
              .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
              .isTrue();
        } else {
          assertThat(sourceField)
              .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
              .isEqualTo(targetField);
        }
      }
    }
  }

  private static XmlPath[] extractXmlPaths(Response source, Response target) {
    return new XmlPath[] {
      new XmlPath(source.getBody().asString()), new XmlPath(target.getBody().asString())
    };
  }

  /**
   * Temporary validation for migrated section names. Ensure migrated sections follow the rules for
   * migration. When core method fetches section name from code list this validation is unnecessary.
   */
  public static void validateXmlItems(
      Response sourceResponse, Response targetResponse, List<String> pathNames) {
    XmlPath xmlPathSource = extractXmlPaths(sourceResponse, targetResponse)[0];
    XmlPath xmlPathTarget = extractXmlPaths(sourceResponse, targetResponse)[1];

    for (String pathName : pathNames) {

      System.out.println("Checking pathname: " + pathName);

      Object sourcePath = xmlPathSource.get(pathName);
      Object targetPath = xmlPathTarget.get(pathName);

      if (pathName.equals(CLASSIFICATION_LAST_MODIFIED)) {
        String sourceDate = getDate(sourcePath);
        String targetDate = getDate(targetPath);

        System.out.println("Comparing timestamp values: " + sourceDate + " -> " + targetDate);

        assertThat(sourceDate)
            .withFailMessage(FAIL_MESSAGE, pathName, sourceDate, targetDate)
            .isEqualTo(targetDate);
      } else if (pathName.equals(CLASSIFICATION_OWNING_SECTION)) {
        String sourceSectionNumber = getSectionNumber(sourcePath);
        String targetSectionNumber = getSectionNumber(targetPath);
        System.out.println(
            "Comparing owning section: " + sourceSectionNumber + " -> " + targetSectionNumber);
        assertThat(sourceSectionNumber)
            .withFailMessage(FAIL_MESSAGE, pathName, sourcePath, targetPath)
            .isEqualTo(targetSectionNumber);
      } else if (pathName.endsWith(HREF)) {
        URI sourceUri = URI.create(pathName);
        URI targetUri = URI.create(pathName);

        String sourceUrl = sourceUri.getPath();
        String targetUrl = targetUri.getPath();

        assertThat(sourceUrl)
            .withFailMessage(FAIL_MESSAGE, pathName, sourceUrl, targetUrl)
            .isEqualTo(targetUrl);
      } else {

        String sourceField = String.valueOf(sourcePath);
        String targetField = String.valueOf(targetPath);

        assertThat(sourceField)
            .withFailMessage(FAIL_MESSAGE, pathName, sourcePath, targetPath)
            .isEqualTo(targetField);

        System.out.println("Source: " + xmlPathSource.get(pathName));
        System.out.println("Target: " + xmlPathTarget.get(pathName));
      }
    }
  }

  public static void validateXmlList(
      String path, Response sourceResponse, Response targetResponse, String pathName) {
    XmlPath xmlPathSource = extractXmlPaths(sourceResponse, targetResponse)[0];
    XmlPath xmlPathTarget = extractXmlPaths(sourceResponse, targetResponse)[1];

    List<String> sourceList = xmlPathSource.getList(pathName);
    List<String> targetList = xmlPathTarget.getList(pathName);
    System.out.println(sourceList.size() + " -> " + targetList.size());

    for (int i = 0; i < sourceList.size(); i++) {
      assertThat(sourceList.get(i))
          .withFailMessage(FAIL_MESSAGE, path, sourceList.get(i), targetList.get(i))
          .isEqualTo(targetList.get(i));
    }
  }

  public static void validateXmlMigratedSsbsections(
      String path, Response sourceResponse, Response targetResponse) {
    XmlPath xmlPathSource = extractXmlPaths(sourceResponse, targetResponse)[0];
    XmlPath xmlPathTarget = extractXmlPaths(sourceResponse, targetResponse)[1];

    String pathName = ENTITIES_CONTENTS_CONTENT_NAME;
    List<String> sourceList = xmlPathSource.getList(pathName);
    List<String> targetList = xmlPathTarget.getList(pathName);
    System.out.println(sourceList.size() + " -> " + targetList.size());

    for (int i = 0; i < sourceList.size(); i++) {
      String sourceSection = getSectionNumber(sourceList.get(i));
      String targetSection = getSectionNumber(targetList.get(i));
      if (sourceSection != null) {
        assertThat(sourceSection)
            .withFailMessage(FAIL_MESSAGE, path, sourceSection, targetSection)
            .startsWith(targetSection);
      }
    }
  }

  public static void validatePathListWithObjectsXml(
      Response sourceResponse, Response targetResponse, String listName, List<String> pathNames) {
    XmlPath xmlPathSource = extractXmlPaths(sourceResponse, targetResponse)[0];
    XmlPath xmlPathTarget = extractXmlPaths(sourceResponse, targetResponse)[1];

    for (String pathName : pathNames) {
      String fullPath = listName + "." + pathName;

      List<Object> sourceNodes = xmlPathSource.getList(fullPath);
      List<Object> targetNodes = xmlPathTarget.getList(fullPath);

      for (int i = 0; i < sourceNodes.size(); i++) {
        String sourceField = String.valueOf(sourceNodes.get(i));
        String targetField = String.valueOf(targetNodes.get(i));

        System.out.printf("Source: [%s] (%d chars)%n", sourceField, sourceField.length());
        System.out.printf("Target: [%s] (%d chars)%n", targetField, targetField.length());

        if (pathName.equals(CLASSIFICATION_LAST_MODIFIED) || pathName.equals(LAST_MODIFIED)) {
          String sourceDate = getDate(sourceField);
          String targetDate = getDate(targetField);
          System.out.println("Comparing timestamp values: " + sourceDate + " -> " + targetDate);

          assertThat(sourceDate)
              .withFailMessage(FAIL_MESSAGE, pathName, sourceDate, targetDate)
              .isEqualTo(targetDate);

        } else if (pathName.equals(CLASSIFICATION_OWNING_SECTION)) {
          String sourceSectionNumber = getSectionNumber(sourceField);
          String targetSectionNumber = getSectionNumber(targetField);
          System.out.println(
              "Comparing owning section: " + sourceSectionNumber + " -> " + targetSectionNumber);

          assertThat(sourceSectionNumber)
              .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
              .isEqualTo(targetSectionNumber);

        } else if (pathName.endsWith(HREF)) {
          URI sourceUri = URI.create(sourceField);
          URI targetUri = URI.create(targetField);

          String sourcePath = sourceUri.getPath();
          String targetPath = targetUri.getPath();
          assertThat(sourcePath)
              .withFailMessage(FAIL_MESSAGE, pathName, sourcePath, targetPath)
              .isEqualTo(targetPath);

        } else {
          assertThat(sourceField)
              .withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField)
              .isEqualTo(targetField);
        }
      }
    }
  }

  public static void validateCSVDocument(
      String path, Response sourceResponse, Response targetResponse) {
    System.out.println(
        sourceResponse.getBody().asString().length()
            + "-> "
            + targetResponse.getBody().asString().length());
    assertThat(sourceResponse.getBody().asString())
        .withFailMessage(
            FAIL_MESSAGE,
            path,
            sourceResponse.getBody().asString(),
            targetResponse.getBody().asString())
        .isEqualTo(targetResponse.getBody().asString());
  }
}
