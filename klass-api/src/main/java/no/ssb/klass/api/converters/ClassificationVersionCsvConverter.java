package no.ssb.klass.api.converters;

import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import no.ssb.klass.api.dto.hal.ClassificationItemResource;
import no.ssb.klass.api.dto.hal.ClassificationVersionResource;
import org.springframework.http.HttpOutputMessage;

public class ClassificationVersionCsvConverter
    extends AbstractCsvConverter<ClassificationVersionResource> {

  @Override
  protected boolean supports(Class<?> clazz) {
    return ClassificationVersionResource.class.equals(clazz);
  }

  @Override
  protected void writeInternal(
      ClassificationVersionResource version, HttpOutputMessage outputMessage) throws IOException {
    Charset charset = selectCharsetAndUpdateOutput(outputMessage);
    ObjectWriter writer = createWriter(ClassificationItemResource.class);
    List<ClassificationItemResource> newItems =
        version.getClassificationItems().stream()
            .map(this::replaceNewLines)
            .collect(Collectors.toList());
    writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), newItems);
  }
}
