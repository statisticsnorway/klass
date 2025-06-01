package no.ssb.klass.api.migration.dataintegrity.codes;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.MigrationTestUtils.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class KlassApiClassificationCodesAtCsvTest extends AbstractKlassApiCodesTest {

    @Test
    void getOneClassificationCodesAt(){
        int classificationId = 2;
        date = "2001-01-01";

        Map<String, Object> paramDate = new HashMap<>();
        paramDate.put(DATE, date);

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @ParameterizedTest
    @MethodSource("rangeProviderClassificationIds")
    void getClassificationCodesAt(Integer classificationId) {

        System.out.println("Start test for ID " + classificationId + " at " + Instant.now());

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsDate,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsDate,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }

        System.out.println("End test for ID " + classificationId + " at " + Instant.now());

    }

    @Test
    void getClassificationCodesAtLanguageEn() {

        int classificationId = 11;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageEnAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageEnAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesAtLanguageNn() {
        int classificationId = 6;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsLanguageNnAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsLanguageNnAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesAtIncludeFuture() {

        int classificationId = 6;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsIncludeFutureAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsIncludeFutureAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesAtSelectCode() {

        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodeAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodeAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesAtSelectCodes() {

        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectCodesAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectCodesAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesAtSelectLevel() {

        int classificationId = 17;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsSelectLevelAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsSelectLevelAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

    @Test
    void getClassificationCodesAtCsvSeparator() {
        int classificationId = 6;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsCsvSeparatorAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsCsvSeparatorAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }


    @Test
    void getClassificationCodesAtCsvFields() {
        int classificationId = 6;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsCsvFieldsAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsCsvFieldsAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateCSVDocument(path, sourceResponse, targetResponse);
        }
    }


    // Nb the presentation name pattern has no values
    @Test
    void getClassificationCodesAtPresentationPattern() {
        int classificationId = 131;

        String path = getCodesAtPath(classificationId);
        sourceResponse = klassApiMigrationClient.getFromSourceApi(path, paramsPresentationCodePatternAt,TEXT_CSV);
        targetResponse = klassApiMigrationClient.getFromTargetApi(path, paramsPresentationCodePatternAt,TEXT_CSV);

        assertApiResponseIsNotNull(sourceResponse);

        assertStatusCodesEqual(sourceResponse.getStatusCode(), targetResponse.getStatusCode(), path);

        if(sourceResponse.getStatusCode() != 200) {
            assertThat(compareError(classificationId, sourceResponse, targetResponse)).isTrue();
        }
        else{
            validateList(sourceResponse, targetResponse, CODES);
        }
    }

}
