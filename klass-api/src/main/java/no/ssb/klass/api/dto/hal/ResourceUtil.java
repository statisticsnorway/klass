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
        String linkUri = linkBuilder.toUriComponentsBuilder().replaceQuery(null).build().toUriString();
        return new UriTemplateBuilder(linkUri);
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
        private final String template;
        private String basePath = "";
        private String[] variables;

        public UriTemplateBuilder(String template) {
            this.template = template;
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
            if (!template.startsWith("/")) {
                return UriTemplate.of(basePath + "/" + template, createParameters(variables));
            } else {
                return UriTemplate.of(basePath + template, createParameters(variables));
            }
        }
    }
}
