package no.ssb.klass.api.migration;

import io.restassured.response.Response;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.diff.Diff;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URI;
import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

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

            if(!sourceUrl.getPath().equals(targetUrl.getPath())){
                System.out.println(
                        "Url path comparison issue: \nsource url path: " +
                                sourceUrl.getPath() + "\ntarget url path: " +
                                targetUrl.getPath());
            }
            return sourceUrl.getPath().equals(targetUrl.getPath());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


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
     * <p>
     * Print the status code.
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     */
    public static boolean compareError(Integer ID, Response sourceResponse, Response targetResponse) {
        Object sourceBody = sourceResponse.getBody().asString();
        Object targetBody = targetResponse.getBody().asString();
        System.out.println(LOG_MESSAGE_STATUS_CODE + sourceResponse.getStatusCode());
        if (sourceResponse.getStatusCode() != targetResponse.getStatusCode() || !sourceBody.equals(targetBody)){
           String sourceError = (ID != null)? ("Source: ID: " + ID + ", Code: " + sourceResponse.getStatusCode() + ", " + sourceBody) : ("Source: " +  "Code: " + sourceResponse.getStatusCode() + ", " + sourceBody);
            String targetError = (ID != null)? ("Target: ID: " + ID + ", Code: " + targetResponse.getStatusCode() + ", " + targetBody) : ("Target: " + "Code: " + targetResponse.getStatusCode() + ", " + targetBody);

            System.out.println(String.join(", ", sourceError) + "\n" + String.join(", ", targetError));
            return false;
        }
       return true;
    }

    public static LocalDate generateRandomDate(LocalDate startDate, LocalDate endDate) {
        long daysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        Random random = new Random();
        long randomDay = random.nextLong(daysBetween + 1);
        return startDate.plusDays(randomDay);
    }

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
    public static void assertStatusCodesEqual(int sourceStatusCode, int targetStatusCode, String path){

        assertThat(sourceStatusCode)
                .withFailMessage(FAIL_MESSAGE, path, sourceStatusCode, targetStatusCode)
                .isEqualTo(targetStatusCode);
    }

    private static Map<Object, Map<String, Object>> mapByField(List<Map<String, Object>> list, String field) {
        return list.stream()
                .filter(item -> item.containsKey(field) && item.get(field) != null)
                .collect(Collectors.toMap(
                        item -> item.get(field),
                        Function.identity()
                ));
    }

    /**
     * Assert response is not null.
     * @param response Response object from Api
     */
    public static void assertApiResponseIsNotNull(Response response) {
        assertThat(response).withFailMessage(API_EMPTY_RESPONSE_MESSAGE).isNotNull();
    }

    /**
     * Validate one single path object.
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathName Name for single path
     */
    public static void validateObject(Response sourceResponse, Response targetResponse, String pathName) {
        Object sourceField = sourceResponse.path(pathName);
        Object targetField = targetResponse.path(pathName);

        if (sourceField == null) {
            assertThat(targetField)
                    .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
                    .isNull();
        }

        if (pathName.endsWith(HREF)) {
            String sourceHref = Objects.toString(sourceField, "");
            String targetHref = Objects.toString(targetField, "");
            if (sourceHref.isEmpty()) {
                assertThat(sourceHref).withFailMessage(FAIL_MESSAGE, pathName, null, targetField).isEqualTo(targetHref);
                return;
            }
            System.out.println(sourceHref + " -> " + targetHref);
            assertThat(isPathEqualIgnoreHost(sourceHref, targetHref))
                    .withFailMessage(FAIL_MESSAGE, pathName, sourceHref, targetHref)
                    .isTrue();
        } else {
            System.out.println(sourceField + "-> " + targetField);
            assertThat(sourceField).withFailMessage(FAIL_MESSAGE, pathName, sourceField, targetField).isEqualTo(targetField);
        }

    }

    /**
     * Validates that the list specified by [pathListName] from both API responses contains the same elements, regardless of order.
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathListName List of path names in nested list
     */
    public static void validateList(Response sourceResponse, Response targetResponse, String pathListName) {

        ArrayList<String> sourceList = sourceResponse.path(pathListName);
        ArrayList<String> targetList = targetResponse.path(pathListName);

        if (sourceList == null) {
            assertThat(targetList)
                    .withFailMessage(FAIL_MESSAGE, pathListName, null, targetList)
                    .isNull();
            return;
        }

        assertThat(targetList)
                .withFailMessage(FAIL_MESSAGE, pathListName, sourceList, targetList)
                .isNotNull();

        System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());
        assertThat(sourceList.size())
                .withFailMessage(FAIL_MESSAGE,
                        pathListName, sourceList.size(), targetList.size())
                .isEqualTo(targetList.size());

        assertThat(sourceList.containsAll(targetList))
                .withFailMessage(FAIL_MESSAGE,
                        pathListName, sourceList, targetList)
                .isTrue();

        assertThat(targetList.containsAll(sourceList))
                .withFailMessage(FAIL_MESSAGE,
                        pathListName, sourceList, targetList)
                .isTrue();
    }

    /**
     *  Validates that the values of fields specified in [pathNames] from two API response bodies are equal.
     *  Handles url paths.
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param pathNames List of path names
     */
    public static void validateItems(Response sourceResponse, Response targetResponse, List<String> pathNames) {

        for (String pathName : pathNames) {
            System.out.println("Checking pathname: " + pathName);
            Object sourceField = sourceResponse.path(pathName);
            Object targetField = targetResponse.path(pathName);

            if (sourceField == null) {
                assertThat(targetField)
                        .withFailMessage(FAIL_MESSAGE, pathName, null, targetField)
                        .isNull();
            }

            if (pathName.endsWith(HREF)) {
                String sourceHref = Objects.toString(sourceField, "");
                String targetHref = Objects.toString(targetField, "");
                if (sourceHref.isEmpty()) {
                    assertThat(sourceHref).withFailMessage(FAIL_MESSAGE, pathName, null, targetField).isEqualTo(targetHref);
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

    /**
     * Validates that path list [listName] with fields specified in [pathNames] from two API response bodies are equal.
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     * @param listName Name of list element in path
     * @param pathNames List of fields in listName list
     * @param idField A unique key for list objects
     */
    public static void validatePathListWithObjects(Response sourceResponse, Response targetResponse, String listName, List<String> pathNames, String idField) {
        List<Map<String, Object>> sourceList = sourceResponse.path(listName);
        List<Map<String, Object>> targetList = targetResponse.path(listName);
        if (sourceList == null) {
            assertThat(targetList)
                    .withFailMessage(FAIL_MESSAGE, listName, null, targetList)
                    .isNull();
            return;
        }

        assertThat(targetList)
                .withFailMessage(FAIL_MESSAGE, listName, sourceList, targetList)
                .isNotNull();

        assertThat(sourceList.size()).isEqualTo(targetList.size());
        System.out.println("List sizes: " + sourceList.size() + " -> " + targetList.size());

        Map<Object, Map<String, Object>> sourceById = mapByField(sourceList, idField);
        Map<Object, Map<String, Object>> targetById = mapByField(targetList, idField);

        for (Object versionId : sourceById.keySet()) {
            Map<String, Object> versionSource = sourceById.get(versionId);
            Map<String, Object> versionTarget = targetById.get(versionId);

            for (String pathName : pathNames) {

                Object sourceField = resolvePath(versionSource, pathName);
                Object targetField = resolvePath(versionTarget, pathName);

                if (pathName.endsWith(HREF)) {
                    assertThat(sourceField == null && targetField == null ||
                            sourceField != null && targetField != null && isPathEqualIgnoreHost(sourceField.toString(), targetField.toString()))
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

    public static void validateXml(Response sourceResponse, Response targetResponse) {
        System.out.println("Source: " + sourceResponse.getBody().asString());
        System.out.println("Target: " + targetResponse.getBody().asString());
    }


    // ?
    public static void validateXmlItems(Response sourceResponse, Response targetResponse, List<String> pathNames) {
        for(String pathName: pathNames) {
            Object sourceField = sourceResponse.path(pathName);
            Object targetField = targetResponse.path(pathName);
            System.out.println(sourceField + " -> " + targetField);
            assertThat(sourceField).withFailMessage(
                    FAIL_MESSAGE,
                    pathName,
                    sourceField,
                    targetField).isEqualTo(targetField);
        }

    }

    public static void validateObjectXml(String path, Response sourceResponse, Response targetResponse) {
        String sourceXml = sourceResponse.getBody().asString();
        String targetXml = targetResponse.getBody().asString();
        System.out.println(sourceXml.length() + " -> " + targetXml.length());
        Diff diff = DiffBuilder.compare(sourceXml)
                .withTest(targetXml)
                .ignoreWhitespace()
                .ignoreComments()
                .checkForSimilar()
                .withNodeFilter(node -> !isLinkElement(node))
                .build();

        assertThat(diff.hasDifferences()).withFailMessage(
                FAIL_MESSAGE,
                path,
                sourceXml,
                targetXml).isFalse();

    }

    private static boolean isLinkElement(Node node) {
        return node.getNodeType() == Node.ELEMENT_NODE && "link".equals(node.getNodeName());
    }

    // order must be ignored ?
    public static void validateLinksXml(String path, Response sourceResponse, Response targetResponse) throws Exception {
        Set<String> sourceLinks = extractNormalizedLinks(sourceResponse.getBody().asString());
        Set<String> targetLinks = extractNormalizedLinks(targetResponse.getBody().asString());

        System.out.println(sourceLinks + " -> " + targetLinks);

        assertThat(sourceLinks).withFailMessage(
                FAIL_MESSAGE,
                path,
                sourceLinks,
                targetLinks).isEqualTo(targetLinks);
    }

    private static String extractSafePath(String href) {
        // Remove URI template (anything after the first `{`)
        int braceIndex = href.indexOf('{');
        if (braceIndex >= 0) {
            href = href.substring(0, braceIndex);
        }

        try {
            URI uri = new URI(href);
            return uri.getPath(); // safe to parse now
        } catch (Exception e) {
            // fallback: basic string parsing
            int start = href.indexOf("://");
            if (start >= 0) {
                int pathStart = href.indexOf("/", start + 3);
                if (pathStart >= 0) return href.substring(pathStart);
            }
            return href; // fallback: return as-is
        }
    }

    private static Set<String> extractNormalizedLinks(String xml) throws Exception {
        Set<String> result = new HashSet<>();

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xml)));
        NodeList links = doc.getElementsByTagName("link");

        for (int i = 0; i < links.getLength(); i++) {
            Element link = (Element) links.item(i);
            String href = null;
            String rel = null;

            NodeList children = link.getChildNodes();
            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    switch (child.getNodeName()) {
                        case "href":
                            href = child.getTextContent().trim();
                            break;
                        case "rel":
                            rel = child.getTextContent().trim();
                            break;
                    }
                }
            }

            if (href == null || rel == null) continue;

            String pathAndQuery = extractSafePath(href);

            result.add(rel + " -> " + pathAndQuery);
        }

        return result;
    }

    public static void validateCSVDocument(String path, Response sourceResponse, Response targetResponse) {
        System.out.println(sourceResponse.getBody().asString().length() + "-> " + targetResponse.getBody().asString().length());
        assertThat(sourceResponse.getBody().asString()).withFailMessage(
                FAIL_MESSAGE, path, sourceResponse.getBody().asString(),
                targetResponse.getBody().asString()).isEqualTo(targetResponse.getBody().asString());
    }
}
