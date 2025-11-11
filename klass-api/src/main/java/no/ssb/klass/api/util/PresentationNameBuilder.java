package no.ssb.klass.api.util;

import com.google.common.base.Strings;

import org.springframework.util.StringUtils;

public class PresentationNameBuilder {
    private final String pattern;

    public PresentationNameBuilder(String pattern) {
        this.pattern = pattern;
    }

    public String presentationName(String code, String name, String shortName) {
        if (Strings.isNullOrEmpty(pattern)) {
            return "";
        }

        return pattern.replace("{code}", code)
                .replace("{name}", name)
                .replace("{shortname}", shortName)
                .replace("{lowercase(code)}", code.toLowerCase())
                .replace("{lowercase(name)}", name.toLowerCase())
                .replace("{lowercase(shortname)}", shortName.toLowerCase())
                .replace("{uppercase(code)}", code.toUpperCase())
                .replace("{uppercase(name)}", name.toUpperCase())
                .replace("{uppercase(shortname)}", shortName.toUpperCase())
                .replace("{capitalize(code)}", StringUtils.capitalize(code))
                .replace("{capitalize(name)}", StringUtils.capitalize(name))
                .replace("{capitalize(shortname)}", StringUtils.capitalize(shortName));
    }
}
