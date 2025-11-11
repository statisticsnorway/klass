package no.ssb.klass.api.converters;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.api.dto.hal.CorrespondenceMapResource;
import no.ssb.klass.api.dto.hal.CorrespondenceTableResource;

import org.springframework.http.HttpOutputMessage;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

public class CorrespondenceTableCsvConverter
        extends AbstractCsvConverter<CorrespondenceTableResource> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return CorrespondenceTableResource.class.equals(clazz);
    }

    @Override
    protected void writeInternal(
            CorrespondenceTableResource correspondenceTable, HttpOutputMessage outputMessage)
            throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(CorrespondenceMapResource.class);
        writer.writeValue(
                new OutputStreamWriter(outputMessage.getBody(), charset),
                correspondenceTable.getCorrespondenceMaps());
    }
}
