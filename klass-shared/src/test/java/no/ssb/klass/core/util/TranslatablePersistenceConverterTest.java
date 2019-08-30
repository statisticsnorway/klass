package no.ssb.klass.core.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TranslatablePersistenceConverterTest {
    private TranslatablePersistenceConverter subject;

    @Before
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

    private Translatable toDatabaseColumnAndBack(Translatable translatable) {
        String databaseColumn = subject.convertToDatabaseColumn(translatable);
        return subject.convertToEntityAttribute(databaseColumn);
    }
}
