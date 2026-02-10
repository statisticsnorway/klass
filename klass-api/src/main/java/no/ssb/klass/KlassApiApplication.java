package no.ssb.klass;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.annotations.tags.Tag;

import no.ssb.klass.api.util.OpenApiConstants;
import no.ssb.klass.api.util.RestConstants;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchDataAutoConfiguration;
import org.springframework.boot.autoconfigure.data.elasticsearch.ElasticsearchRepositoriesAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.TomcatServletWebServerFactoryCustomizer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
        info =
                @Info(
                        title = "Klass",
                        version = "1",
                        license =
                                @License(
                                        name = "CC BY 4.0",
                                        url =
                                                "https://creativecommons.org/licenses/by/4.0/deed.no"),
                        contact = @Contact(email = "metadata@ssb.no", name = "Team Metadata"),
                        description =
"""
Klass is Statistics Norway's system for documentation of classifications and code lists. This API provides read-only access to classifications and code lists.

Classifications are "official", and in a classification, the categories at each level must be mutually exclusive and complete, i.e. the classification contains all categories that belong to the area covered by the classification. Code lists are not "official". They may be adapted to a one particular statistic and do not have to be mutually exclusive or complete.
"""),
        externalDocs =
                @ExternalDocumentation(
                        description = "API Guide",
                        url = RestConstants.CONTEXT_AND_VERSION_V1 + "/api-guide.html"),
        servers = {
            @Server(url = "https://data.ssb.no", description = "Production"),
            @Server(url = "https://data.test.ssb.no", description = "Test"),
            @Server(url = "http://localhost:8080", description = "Local"),
        },
        tags = {
            @Tag(
                    name = OpenApiConstants.Tags.CLASSIFICATIONS_TAG,
                    description = "List classifications"),
            @Tag(
                    name = OpenApiConstants.Tags.SEARCH_TAG,
                    description = "Search for classifications"),
            @Tag(
                    name = OpenApiConstants.Tags.CODES_TAG,
                    description =
"""
Get codes from a classification. A range is specified when requesting the codes, and the response will for each code indicate its valid range (validFrom/validTo). ValidTo is optional.
The format and character set used can be set in the http header.

To get a snapshot of codes valid at a specified date, use codesAt.
"""),
            @Tag(
                    name = OpenApiConstants.Tags.VERSIONS_TAG,
                    description =
                            "Used to get a classification version by ID. The ID is shown under classifications."),
            @Tag(
                    name = OpenApiConstants.Tags.VARIANTS_TAG,
                    description =
                            "Used to search codes from a classification variant. A range is specified when requesting the codes, and the response will for each code indicate its valid range (validFrom/validTo). The format and character set used, can be specified in the http header."),
            @Tag(
                    name = OpenApiConstants.Tags.CORRESPONDENCE_TABLES_TAG,
                    description =
                            "Used to get correspondence mappings between a source classification and a target classification. A range is specified when requesting the correspondence mappings, and the response will for each correspondence map indicate its valid range (validFrom/validTo). If a correspondence table is missing for parts of the range, the API will return 404 (not found). The format and character set used can be specified in the http header."),
            @Tag(
                    name = OpenApiConstants.Tags.CLASSIFICATION_FAMILIES_TAG,
                    description =
                            "Classification families group classifications pertaining to a particular subject area."),
            @Tag(
                    name = OpenApiConstants.Tags.SSB_SECTIONS_TAG,
                    description =
                            "List Statistics Norway divisions responsible for at least one classification."),
        })
@SpringBootApplication(
        exclude = {
            ElasticsearchDataAutoConfiguration.class,
            ElasticsearchRepositoriesAutoConfiguration.class,
        })
@Import(TomcatServletWebServerFactoryCustomizer.class)
@ConfigurationPropertiesScan
@ServletComponentScan
@EnableScheduling
public class KlassApiApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(KlassApiApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(KlassApiApplication.class, args);
    }
}
