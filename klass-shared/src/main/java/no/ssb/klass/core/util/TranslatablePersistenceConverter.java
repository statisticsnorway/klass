package no.ssb.klass.core.util;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import no.ssb.klass.core.model.Language;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Used by Hibernate to persists {@link Translatable}s */
@Component
@Converter(autoApply = true)
public class TranslatablePersistenceConverter implements AttributeConverter<Translatable, String> {
    private final TranslationPattern noPattern;
    private final TranslationPattern nnPattern;
    private final TranslationPattern enPattern;

    public TranslatablePersistenceConverter() {
        this("no", "nn", "en");
    }

    public TranslatablePersistenceConverter(String no, String nn, String en) {
        this.noPattern = new TranslationPattern(no);
        this.nnPattern = new TranslationPattern(nn);
        this.enPattern = new TranslationPattern(en);
    }

    @Override
    public String convertToDatabaseColumn(Translatable translatable) {
        if (translatable == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (translatable != null) {
            if (translatable.hasLanguage(Language.NB)) {
                builder.append("<no>")
                        .append(StringEscapeUtils.escapeXml11(translatable.getString(Language.NB)))
                        .append("</no>");
            }
            if (translatable.hasLanguage(Language.NN)) {
                builder.append("<nn>")
                        .append(StringEscapeUtils.escapeXml11(translatable.getString(Language.NN)))
                        .append("</nn>");
            }
            if (translatable.hasLanguage(Language.EN)) {
                builder.append("<en>")
                        .append(StringEscapeUtils.escapeXml11(translatable.getString(Language.EN)))
                        .append("</en>");
            }
        }
        return builder.toString();
    }

    @Override
    public Translatable convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        String no = unescape(noPattern.find(dbData));
        String nn = unescape(nnPattern.find(dbData));
        String en = unescape(enPattern.find(dbData));
        return new Translatable(no, nn, en);
    }

    private static String unescape(String text) {
        return text == null || text.indexOf('&') < 0 ? text : StringEscapeUtils.unescapeXml(text);
    }

    private static final class TranslationPattern {
        private final String openingTag;
        private final String closingTag;
        private final Pattern regex;

        private TranslationPattern(String language) {
            openingTag = "<" + language + ">";
            closingTag = "</" + language + ">";
            // Preserve the public constructor's historical regex semantics for custom patterns.
            regex =
                    String.valueOf(language)
                                    .chars()
                                    .anyMatch(c -> "\\.^$|?*+()[]{}".indexOf(c) >= 0)
                            ? Pattern.compile(
                                    openingTag + "(.*)" + closingTag,
                                    Pattern.MULTILINE | Pattern.DOTALL)
                            : null;
        }

        private String find(String input) {
            if (input.isEmpty()) {
                return null;
            }
            if (regex != null) {
                Matcher matcher = regex.matcher(input);
                return matcher.find() ? matcher.group(1) : null;
            }
            int start = input.indexOf(openingTag);
            if (start < 0) {
                return null;
            }
            start += openingTag.length();
            // The old greedy regex captured from the first opening to the last closing tag.
            int end = input.lastIndexOf(closingTag);
            return end < start ? null : input.substring(start, end);
        }
    }
}
