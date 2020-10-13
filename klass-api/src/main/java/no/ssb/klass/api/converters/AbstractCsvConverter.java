package no.ssb.klass.api.converters;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.core.JsonGenerator;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;

import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvGenerator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;

import no.ssb.klass.api.dto.hal.ClassificationItemResource;

public abstract class AbstractCsvConverter<T> extends AbstractHttpMessageConverter<T> {
    private static final Charset DEFAULT_CHARACTER_SET = StandardCharsets.ISO_8859_1;
    private static final char DEFAULT_CSV_SEPARATOR = ';';

    public AbstractCsvConverter() {
        super(new MediaType("text", "csv"));
    }

    protected ObjectWriter createWriter(Class<?> clazz) {
        return createWriter(clazz, DEFAULT_CSV_SEPARATOR);
    }

    protected ObjectWriter createWriter(Class<?> clazz, char csvSeparator) {
        return createWriter(clazz, csvSeparator, null);
    }
    protected ObjectWriter createWriter(Class<?> clazz, char csvSeparator, List<String> csvFields) {
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema;
        if (csvFields == null) {
            schema = mapper
                    .schemaFor(clazz)
                    .withHeader()
                    .withColumnSeparator(csvSeparator);
        } else {
            mapper.enable(JsonGenerator.Feature.IGNORE_UNKNOWN);
            schema = CsvSchema.builder()
                    .addColumns(csvFields, CsvSchema.ColumnType.STRING)
                    .build().withHeader().withColumnSeparator(csvSeparator);
        }

        return mapper.writer(schema).withFeatures(CsvGenerator.Feature.ALWAYS_QUOTE_STRINGS,
                CsvGenerator.Feature.OMIT_MISSING_TAIL_COLUMNS);
    }

    protected Charset selectCharsetAndUpdateOutput(HttpOutputMessage outputMessage) {
        Charset charset = getContentTypeCharset(outputMessage.getHeaders().getContentType());
        outputMessage.getHeaders().setContentType(new MediaType("text", "csv", charset));
        return charset;
    }

    private Charset getContentTypeCharset(MediaType contentType) {
        if (contentType != null && contentType.getCharset() != null) {
            return contentType.getCharset();
        } else {
            return DEFAULT_CHARACTER_SET;
        }
    }

    @Override
    protected T readInternal(Class<? extends T> clazz, HttpInputMessage inputMessage) {
        throw new UnsupportedOperationException("Reading csv file is not supported");
    }

    /**
     * Replaces newlines with space from the field notes as they can cause trouble for users trying to open CSV with MS
     * Excel
     * 
     * @param item
     *            original item
     * @return new item where notes no longer contains newlines
     */
    ClassificationItemResource replaceNewLines(ClassificationItemResource item) {
        String notes = item.getNotes() == null ? null : item.getNotes().replaceAll("\\r\\n|\\r|\\n", " ");
        String code = item.getCode();
        String level = item.getLevel();
        String name = item.getName();
        String shortName = item.getShortName();
        String parentCode = item.getParentCode();
        LocalDate validFrom = item.getValidFrom();
        LocalDate validTo = item.getValidTo();
        return new ClassificationItemResource(code, level, name, shortName, notes, parentCode, validFrom, validTo);
    }
}
