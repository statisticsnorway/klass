package no.ssb.klass.api.migration;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import no.ssb.klass.api.util.RestConstants;

import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.BASE_PATH;
import static no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest.sourceHost;
import static no.ssb.klass.api.migration.dataintegrity.AbstractKlassApiDataIntegrityTest.targetHost;

public class KlassApiMigrationClient {

    private final RequestSpecification sourceApi;
    private final RequestSpecification targetApi;

    public KlassApiMigrationClient() {
        this.sourceApi = new RequestSpecBuilder()
                .setBaseUri(sourceHost)
                .setBasePath(BASE_PATH + RestConstants.API_VERSION_V1)
                .setContentType("application/json")
                .build();



        this.targetApi = new RequestSpecBuilder()
                .setBaseUri(targetHost)
                .setBasePath(BASE_PATH + RestConstants.API_VERSION_V1)
                .setContentType("application/json")
                .build();
    }

    public Response getFromSourceApi(String path, Map<String, ?> queryParams) {
        RequestSpecification request = RestAssured.given().spec(sourceApi);
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        request.log().all();
        return request.get(path);
    }

    public Response getFromTargetApi(String path, Map<String, ?> queryParams) {
        RequestSpecification request = RestAssured.given().spec(targetApi);
        if (queryParams != null && !queryParams.isEmpty()) {
            request.queryParams(queryParams);
        }

        request.log().all();
        return request.get(path);
    }

}
