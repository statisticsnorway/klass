package no.ssb.klass.api.converters;

import com.fasterxml.jackson.databind.ObjectWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import no.ssb.klass.api.dto.CorrespondenceItemList;
import org.springframework.http.HttpOutputMessage;

public class CorrespondenceItemListCsvConverter
    extends AbstractCsvConverter<CorrespondenceItemList> {
  @Override
  protected boolean supports(Class<?> clazz) {
    return CorrespondenceItemList.class.equals(clazz);
  }

  @Override
  protected void writeInternal(
      CorrespondenceItemList correspondenceItemList, HttpOutputMessage outputMessage)
      throws IOException {
    Charset charset = selectCharsetAndUpdateOutput(outputMessage);
    ObjectWriter writer =
        createWriter(
            correspondenceItemList.classificationItemsJavaType(),
            correspondenceItemList.getCsvSeparator(),
            correspondenceItemList.getCsvFields());
    writer.writeValue(
        new OutputStreamWriter(outputMessage.getBody(), charset),
        correspondenceItemList.getCorrespondenceItems());
  }
}
