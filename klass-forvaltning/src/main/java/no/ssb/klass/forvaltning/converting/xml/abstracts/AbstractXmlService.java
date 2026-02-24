package no.ssb.klass.forvaltning.converting.xml.abstracts;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import no.ssb.klass.forvaltning.converting.exception.ImportException;
import no.ssb.klass.core.model.ClassificationEntityOperations;

/**
 * @author Mads Lundemo, SSB.
 */
public abstract class AbstractXmlService<T> {

    private static final String FILE_EXT = ".xml";

    public abstract InputStream toXmlStream(T domainObject);

    public abstract void fromXmlStreamAndMerge(InputStream stream, T domainObject) throws ImportException;

    @Value("${klass.env.export.schema.baseUrl}")
    protected String SchemaBaseUrl;

    private final XmlMapper xmlMapper;


    public AbstractXmlService() {
        xmlMapper = new XmlMapper();
    }

    public String createFileName(ClassificationEntityOperations entity) {
        return windowsCompatibleFilename(entity.getNameInPrimaryLanguage()) + FILE_EXT;
    }

    protected ObjectWriter getObjectWriter(Class<?> type) {
        return xmlMapper.writerFor(type).with(SerializationFeature.INDENT_OUTPUT);
    }

    protected ObjectReader getObjectReader(Class<?> type) {
        return xmlMapper.readerFor(type);
    }

    /**
     * Windows does not allow many different characters in filenames.
     *
     * @return filename that should be safe on windows
     */
    private String windowsCompatibleFilename(String filename) {
        return filename.replaceAll("\\s+", "").replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    protected <U> List<U> readInputStream(InputStream stream, Class<U> type) throws ImportException {
        ObjectReader objectReader = getObjectReader(type);
        try {
            MappingIterator<U> mappingIterator = objectReader.readValues(stream);
            return mappingIterator.readAll();
        } catch (Exception e) {
            throw new ImportException(
                    "En feil oppstod under lesing av filen, er du sikker på at den er på riktig format?", e);
        }
    }

    protected InputStream createInputStream(Object container, ObjectWriter writer) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            writer.writeValue(outputStream, container);

            return new ByteArrayInputStream(outputStream.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("unable to convert data to InputStream", e);
        }
    }

}
