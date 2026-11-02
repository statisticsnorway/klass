package no.ssb.klass.core.util;

import com.google.common.base.Strings;

import org.apache.commons.lang3.StringEscapeUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Frozen pre-optimization reader used to verify compatibility in differential tests. */
public class LegacyTranslatablePersistenceConverter extends TranslatablePersistenceConverter {
    private final Pattern noPattern;
    private final Pattern nnPattern;
    private final Pattern enPattern;

    public LegacyTranslatablePersistenceConverter() {
        this("no", "nn", "en");
    }

    public LegacyTranslatablePersistenceConverter(String no, String nn, String en) {
        super(no, nn, en);
        this.noPattern =
                Pattern.compile(
                        "<" + no + ">(.*)</" + no + ">", Pattern.MULTILINE | Pattern.DOTALL);
        this.nnPattern =
                Pattern.compile(
                        "<" + nn + ">(.*)</" + nn + ">", Pattern.MULTILINE | Pattern.DOTALL);
        this.enPattern =
                Pattern.compile(
                        "<" + en + ">(.*)</" + en + ">", Pattern.MULTILINE | Pattern.DOTALL);
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
