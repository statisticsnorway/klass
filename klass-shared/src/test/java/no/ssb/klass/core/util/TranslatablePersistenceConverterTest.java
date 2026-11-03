package no.ssb.klass.core.util;

import static org.junit.jupiter.api.Assertions.*;

import no.ssb.klass.core.model.Language;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

public class TranslatablePersistenceConverterTest {
    private TranslatablePersistenceConverter subject;

    @BeforeEach
    public void setup() {
        subject = new TranslatablePersistenceConverter();
    }

    @Test
    public void symmetryWhenTranslatableIsNull() {
        Translatable translatable = null;
        assertNull(toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsEmpty() {
        Translatable translatable = Translatable.empty();
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsNorwegian() {
        Translatable translatable = new Translatable("no", null, null);
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsNewNorwegian() {
        Translatable translatable = new Translatable(null, "nn", null);
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsEnglish() {
        Translatable translatable = new Translatable(null, null, "en");
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsAllLanguages() {
        Translatable translatable = new Translatable("no", "nn", "en");
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsAllEmptyString() {
        Translatable translatable = new Translatable("", "", "");
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void symmetryWhenTranslatableIsAllNull() {
        Translatable translatable = new Translatable(null, null, null);
        assertEquals(translatable, toDatabaseColumnAndBack(translatable));
    }

    @Test
    public void preservesPersistedFormatAndEscapesExactlyOnce() {
        Translatable translation =
                new Translatable("A & B <no>", "", "\"quoted\" 'text' &amp; \u00e6\u00f8\u00e5");
        String persisted =
                "<no>A &amp; B &lt;no&gt;</no><nn></nn>"
                        + "<en>&quot;quoted&quot; &apos;text&apos; &amp;amp; \u00e6\u00f8\u00e5</en>";
        assertEquals(persisted, subject.convertToDatabaseColumn(translation));
        assertEquals(translation, subject.convertToEntityAttribute(persisted));
    }

    @Test
    public void preservesUnicodeMultilineAndXmlLikeText() {
        Translatable translation =
                new Translatable(
                        "\u00c6\u00d8\u00c5 \u00e6\u00f8\u00e5\n\r\t\u6f22\u5b57 \ud83d\ude00",
                        "<nn>nested</nn> &unknown; &#65; &amp;",
                        "A\u0001B\u000bC");
        assertEquals(translation, toDatabaseColumnAndBack(translation));
    }

    @Test
    public void distinguishesMissingEmptyAndNull() {
        assertNull(subject.convertToEntityAttribute(null));
        assertEquals(Translatable.empty(), subject.convertToEntityAttribute(""));
        Translatable result = subject.convertToEntityAttribute("<nn></nn>");
        assertFalse(result.hasLanguage(Language.NB));
        assertTrue(result.hasLanguage(Language.NN));
        assertEquals("", result.getStringOrNull(Language.NN));
        assertFalse(result.hasLanguage(Language.EN));
    }

    @Test
    public void preservesLenientFragmentAndGreedyMatchingBehavior() {
        String[] inputs = {
            "not XML",
            "<no/>",
            "<NO>ignored</NO><no attr=\"x\">ignored</no>",
            "</no>prefix<en>English</en><nn>Nynorsk</nn><no>Bokm\u00e5l</no>suffix",
            "<no>first</no><no>last</no>",
            "<no>outer<no>inner</no>tail</no>",
            "</no><no>unclosed",
            "<no>one</no><no>unclosed",
            "<no>unclosed<nn>yes</nn>",
            "<en>&#65; &#x1F600; &amp;lt; &unknown; &amp</en>",
            "<no>A\nB\rC\u0085D\u2028E\u2029F</no>",
            "<no></no><nn></nn><en></en>",
            "<no>&#0; &#xD800; &AMP; &#xZZ; &#65 &#9999999999999999999999999;</no>",
            "<no>&#x110000;</no>"
        };
        assertEquals(
                new Translatable("first</no><no>last", null, null),
                subject.convertToEntityAttribute(inputs[4]));
        assertMatchesLegacy(subject, new LegacyTranslatablePersistenceConverter(), inputs);
    }

    @Test
    public void supportsStabasLanguageTagsWithoutChangingWriteFormat() {
        TranslatablePersistenceConverter stabas =
                new TranslatablePersistenceConverter("nb", "nn", "en");
        assertEquals(
                new Translatable("Bokm\u00e5l", "Nynorsk", "English"),
                stabas.convertToEntityAttribute(
                        "<en>English</en><nb>Bokm\u00e5l</nb><nn>Nynorsk</nn>"));
        assertEquals(
                "<no>Bokm\u00e5l</no>",
                stabas.convertToDatabaseColumn(new Translatable("Bokm\u00e5l", null, null)));
    }

    @Test
    public void preservesCustomConstructorPatternSemantics() {
        for (String tag : new String[] {"nb", "", null, "n.", "n[ob]", "(no)", "n+", "n\\w"}) {
            assertMatchesLegacy(
                    new TranslatablePersistenceConverter(tag, "nn", "en"),
                    new LegacyTranslatablePersistenceConverter(tag, "nn", "en"),
                    "",
                    "<no>first</no><nb>second</nb><nn>Nynorsk</nn>",
                    "<null>value</null><>empty tag</>",
                    "<n.>literal</n.><no>pattern</no>");
        }
        assertThrows(
                java.util.regex.PatternSyntaxException.class,
                () -> new TranslatablePersistenceConverter("[", "nn", "en"));
    }

    @Test
    public void matchesLegacyForLongAndIncompleteFragments() {
        assertMatchesLegacy(
                subject,
                new LegacyTranslatablePersistenceConverter(),
                "<no>" + "Long plain text \u00e6\u00f8\u00e5\n".repeat(1_000) + "</no>",
                "<no>" + "&amp; &lt; &#x1F600; ".repeat(1_000) + "</no>",
                "<no>unfinished".repeat(100),
                "</no>earlier closing<no>unfinished",
                "<no>first</no>" + "<no>unfinished".repeat(100));
    }

    @Test
    public void matchesLegacyForGeneratedTranslationsAndFragments() {
        Random random = new Random(20261102);
        String[] pieces = {
            "",
            "ordinary",
            "\u00e6\u00f8\u00e5",
            "\u6f22\u5b57\ud83d\ude00",
            "\n\r\t",
            "&",
            "&amp;",
            "&amp;lt;",
            "&#65;",
            "&#x1F600;",
            "&unknown;",
            "<no>",
            "</no>",
            "<nn>",
            "</nn>",
            "<en>",
            "</en>",
            "<nb>",
            "</nb>",
            "<no/>",
            "<no attr='x'>",
            "\"'",
            "\u0001"
        };
        for (String norwegian : new String[] {"no", "nb"}) {
            TranslatablePersistenceConverter reader =
                    new TranslatablePersistenceConverter(norwegian, "nn", "en");
            TranslatablePersistenceConverter legacy =
                    new LegacyTranslatablePersistenceConverter(norwegian, "nn", "en");
            for (int i = 0; i < 5_000; i++) {
                StringBuilder fragment = new StringBuilder();
                for (int j = random.nextInt(25); j > 0; j--) {
                    fragment.append(pieces[random.nextInt(pieces.length)]);
                }
                String text = fragment.toString();
                assertMatchesLegacy(reader, legacy, text);
                Translatable translation =
                        new Translatable(
                                random.nextBoolean() ? text : null,
                                random.nextBoolean() ? text : "",
                                random.nextBoolean() ? text : null);
                assertMatchesLegacy(reader, legacy, subject.convertToDatabaseColumn(translation));
            }
        }
    }

    private void assertMatchesLegacy(
            TranslatablePersistenceConverter reader,
            TranslatablePersistenceConverter legacy,
            String... inputs) {
        for (String input : inputs) {
            Translatable expected;
            try {
                expected = legacy.convertToEntityAttribute(input);
            } catch (IllegalArgumentException expectedException) {
                IllegalArgumentException actualException =
                        assertThrows(
                                expectedException.getClass(),
                                () -> reader.convertToEntityAttribute(input));
                assertEquals(expectedException.getMessage(), actualException.getMessage());
                continue;
            }
            assertEquals(expected, reader.convertToEntityAttribute(input), () -> "Input: " + input);
        }
    }

    private Translatable toDatabaseColumnAndBack(Translatable translatable) {
        String databaseColumn = subject.convertToDatabaseColumn(translatable);
        return subject.convertToEntityAttribute(databaseColumn);
    }
}
