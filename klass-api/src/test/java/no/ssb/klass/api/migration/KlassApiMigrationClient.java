package no.ssb.klass.api.migration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import no.ssb.klass.api.util.RestConstants;

import java.util.Map;
import java.util.Objects;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest.sourceHost;
import static no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest.targetHost;

public class KlassApiMigrationClient {

    private final RequestSpecification sourceApi;
    private final RequestSpecification targetApi;

    public KlassApiMigrationClient() {
        this.sourceApi = new RequestSpecBuilder()
                .setBaseUri(sourceHost)
                .setBasePath(BASE_PATH + RestConstants.API_VERSION_V1)
                .build();



        this.targetApi = new RequestSpecBuilder()
                .setBaseUri(targetHost)
                .setBasePath(BASE_PATH + RestConstants.API_VERSION_V1)
                .build();
    }

    public boolean isApiAvailable(String host) {
        try {
            Response response = RestAssured.get(host + "/ping");
            return response.getStatusCode() == 200;
        } catch (Exception e) {
            return false;
        }
    }

    public Response getFromSourceApi(String path, Map<String, ?> queryParams, String headerAcceptType) {
        RequestSpecification request = RestAssured.given().spec(sourceApi);
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        String contentTypeValue;
        contentTypeValue = Objects.requireNonNullElse(headerAcceptType, "application/json");
        request.log().all();
        return request.header(ACCEPT, contentTypeValue).get(path);
    }

    public Response getFromTargetApi(String path, Map<String, ?> queryParams, String headerAcceptType) {
        RequestSpecification request = RestAssured.given().spec(targetApi);
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }
        String contentTypeValue;
        contentTypeValue = Objects.requireNonNullElse(headerAcceptType, "application/json");
        request.log().all();
        return request.header(ACCEPT, contentTypeValue).get(path);
    }

}
