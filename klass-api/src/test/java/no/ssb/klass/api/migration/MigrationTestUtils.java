package no.ssb.klass.api.migration;

import io.restassured.response.Response;

import java.net.URL;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

import static no.ssb.klass.api.migration.MigrationTestConstants.FAIL_MESSAGE;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class MigrationTestUtils {

    public static Map<Object, Map<String, Object>> mapById(List<Map<String, Object>> versions) {
        return versions.stream()
                .collect(Collectors.toMap(v -> v.get("id"), Function.identity()));
    }

    /**
     * Compare two href and ignore host
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

    public static Object resolvePath(Map<String, Object> map, String path) {
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
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     */
    public static boolean compareErrorJsonResponse(Integer ID, Response sourceResponse, Response targetResponse) {
        Object sourceBody = sourceResponse.getBody().jsonPath().get("error");
        Object targetBody = targetResponse.getBody().jsonPath().get("error");

        if (sourceResponse.getStatusCode() != targetResponse.getStatusCode() || !sourceBody.equals(targetBody)){
           String sourceError = (ID != null)? ("Source: ID: " + ID + ", Code: " + sourceResponse.getStatusCode() + ", " + sourceBody) : ("Source: " +  "Code: " + sourceResponse.getStatusCode() + ", " + sourceBody);
            String targetError = (ID != null)? ("Target: ID: " + ID + ", Code: " + targetResponse.getStatusCode() + ", " + targetBody) : ("Target: " + "Code: " + targetResponse.getStatusCode() + ", " + targetBody);

            System.out.println(String.join(", ", sourceError) + "\n" + String.join(", ", targetError));
            return false;
        }
        return true;
    }

    /**
     *
     * @param sourceResponse Response object from source Api
     * @param targetResponse Response object from target Api
     */
    public static boolean compareError(Integer ID, Response sourceResponse, Response targetResponse) {
        Object sourceBody = sourceResponse.getBody().asString();
        Object targetBody = targetResponse.getBody().asString();

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

    public static String generateRandomDateTime() {
        LocalDate startDate = LocalDate.of(1800, 1, 1);
        LocalDate endDate = LocalDate.of(2030, 12, 31);

        long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
        LocalDate randomDate = startDate.plusDays(ThreadLocalRandom.current().nextLong(totalDays + 1));

        LocalTime randomTime = LocalTime.of(
                ThreadLocalRandom.current().nextInt(0, 24),
                ThreadLocalRandom.current().nextInt(0, 60),
                ThreadLocalRandom.current().nextInt(0, 60),
                ThreadLocalRandom.current().nextInt(0, 1_000_000_000)
        );

        LocalDateTime localDateTime = LocalDateTime.of(randomDate, randomTime);

        int offsetHours = ThreadLocalRandom.current().nextInt(-12, 15);
        ZoneOffset offset = ZoneOffset.ofHours(offsetHours);
        OffsetDateTime offsetDateTime = localDateTime.atOffset(offset);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        return offsetDateTime.format(formatter);
    }

    public static Integer generateRandomId(int to) {
        Random random = new Random();
        return random.nextInt(to);
    }

    /**
     * Assert that source and target returns the same status code
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

    public static Map<Object, Map<String, Object>> mapByField(List<Map<String, Object>> list, String field) {
        return list.stream()
                .filter(item -> item.containsKey(field) && item.get(field) != null)
                .collect(Collectors.toMap(
                        item -> item.get(field),
                        Function.identity()
                ));
    }
}
