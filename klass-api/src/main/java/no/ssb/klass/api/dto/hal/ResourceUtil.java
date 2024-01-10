package no.ssb.klass.api.dto.hal;

import java.util.ArrayList;
import java.util.List;

import org.springframework.hateoas.TemplateVariable;
import org.springframework.hateoas.TemplateVariables;
import org.springframework.hateoas.UriTemplate;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;

public final class ResourceUtil {
    private ResourceUtil() {
        // Utility class
    }

    public static UriTemplateBuilder createUriTemplateBuilder(WebMvcLinkBuilder linkBuilder) {
        return new UriTemplateBuilder(linkBuilder);
    }

    public static UriTemplate createUriTemplate(WebMvcLinkBuilder linkBuilder, String... parameters) {
        String baseUri = linkBuilder.toUriComponentsBuilder().replaceQuery(null).build().toUriString();

        return UriTemplate.of(baseUri, createParameters(parameters));
    }

    private static TemplateVariables createParameters(String... parameters) {
        List<TemplateVariable> templateVariables = new ArrayList<>();
        for (String parameter : parameters) {
            templateVariables.add(new TemplateVariable(parameter, TemplateVariable.VariableType.REQUEST_PARAM));
        }
        return new TemplateVariables(templateVariables);
    }

    /*
      * Helper class for building an UriTemplate with optional basePath and variables.
     */
    public static class UriTemplateBuilder {
        private final WebMvcLinkBuilder linkBuilder;
        private String basePath = "";
        private String[] variables;

        public UriTemplateBuilder(WebMvcLinkBuilder linkBuilder) {
            this.linkBuilder = linkBuilder;
        }

        public UriTemplateBuilder variables(String... variables) {
            this.variables = variables;
            return this;
        }

        public UriTemplateBuilder basePath(String basePath) {
            this.basePath = basePath;
            return this;
        }

        public UriTemplate build() {
            if (basePath.endsWith("/")) {
                basePath = basePath.substring(0, basePath.length() - 1);
            }
            String originalPath = linkBuilder.toUri().getPath();
            String template = linkBuilder.toUriComponentsBuilder()
                    .replaceQuery(null)
                    .replacePath(basePath + originalPath)
                    .build().toUriString();
            return UriTemplate.of(template, createParameters(variables));
        }
    }
}
