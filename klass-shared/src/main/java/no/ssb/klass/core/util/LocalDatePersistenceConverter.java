package no.ssb.klass.core.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Used by Hibernate to persists {@link LocalDate}s
 */
@Converter(autoApply = true)
public class LocalDatePersistenceConverter implements AttributeConverter<LocalDate, String> {
    private final DateTimeFormatter formatter;

    public LocalDatePersistenceConverter() {
        formatter = DateTimeFormatter.ISO_LOCAL_DATE;
    }

    @Override
    public String convertToDatabaseColumn(LocalDate entityValue) {
        if (entityValue != null) {
            return entityValue.format(formatter);
        }
        return null;
    }

    @Override
    public LocalDate convertToEntityAttribute(String databaseValue) {
        if (databaseValue != null) {
            return LocalDate.parse(databaseValue, formatter);
        }
        return null;
    }
}
