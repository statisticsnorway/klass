package no.ssb.klass.api.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.api.dto.hal.ClassificationItemResource;
import no.ssb.klass.api.dto.hal.ClassificationVariantResource;

public class ClassificationVariantCsvConverter extends AbstractCsvConverter<ClassificationVariantResource> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return ClassificationVariantResource.class.equals(clazz);
    }

    @Override
    protected void writeInternal(ClassificationVariantResource variant, HttpOutputMessage outputMessage)
            throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(ClassificationItemResource.class);
        List<ClassificationItemResource> newItems =
                variant.getClassificationItems().stream().map(this::replaceNewLines).collect(Collectors.toList());
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), newItems);
    }
}
