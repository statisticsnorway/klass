package no.ssb.klass.core.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

import com.google.common.base.Strings;

import no.ssb.klass.core.model.Language;

/**
 * Used by Hibernate to persists {@link Translatable}s
 */
@Component
@Converter(autoApply = true)
public class TranslatablePersistenceConverter implements AttributeConverter<Translatable, String> {
    private final Pattern noPattern;
    private final Pattern nnPattern;
    private final Pattern enPattern;

    public TranslatablePersistenceConverter() {
        this("no", "nn", "en");
    }

    public TranslatablePersistenceConverter(String no, String nn, String en) {
        this.noPattern = Pattern.compile("<" + no + ">(.*)</" + no + ">", Pattern.MULTILINE | Pattern.DOTALL);
        this.nnPattern = Pattern.compile("<" + nn + ">(.*)</" + nn + ">", Pattern.MULTILINE | Pattern.DOTALL);
        this.enPattern = Pattern.compile("<" + en + ">(.*)</" + en + ">", Pattern.MULTILINE | Pattern.DOTALL);
    }

    @Override
    public String convertToDatabaseColumn(Translatable translatable) {
        if (translatable == null) {
            return null;
        }
        StringBuilder builder = new StringBuilder();
        if (translatable != null) {
            if (translatable.hasLanguage(Language.NB)) {
                builder.append("<no>").append(StringEscapeUtils.escapeXml11(translatable.getString(Language.NB)))
                        .append("</no>");
            }
            if (translatable.hasLanguage(Language.NN)) {
                builder.append("<nn>").append(StringEscapeUtils.escapeXml11(translatable.getString(Language.NN)))
                        .append("</nn>");
            }
            if (translatable.hasLanguage(Language.EN)) {
                builder.append("<en>").append(StringEscapeUtils.escapeXml11(translatable.getString(Language.EN)))
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
        String no = StringEscapeUtils.unescapeXml(find(noPattern, dbData));
        String nn = StringEscapeUtils.unescapeXml(find(nnPattern, dbData));
        String en = StringEscapeUtils.unescapeXml(find(enPattern, dbData));
        return new Translatable(no, nn, en);
    }

    private String find(Pattern pattern, String input) {
        if (Strings.isNullOrEmpty(input)) {
            return null;
        }
        Matcher matcher = pattern.matcher(input);
        if (!matcher.find()) {
            return null;
        }
        return matcher.group(1);
    }
}
