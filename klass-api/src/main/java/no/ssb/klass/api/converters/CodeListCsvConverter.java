package no.ssb.klass.api.converters;

import com.fasterxml.jackson.databind.ObjectWriter;
import no.ssb.klass.api.dto.CodeItem;
import no.ssb.klass.api.dto.CodeList;
import org.springframework.http.HttpOutputMessage;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class CodeListCsvConverter extends AbstractCsvConverter<CodeList> {

    @Override
    protected boolean supports(Class<?> clazz) {
        return CodeList.class.equals(clazz);
    }

    @Override
    protected void writeInternal(CodeList codeList, HttpOutputMessage outputMessage) throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(codeList.codeItemsJavaType(), codeList.getCsvSeparator(), codeList.getCsvFields());
        List<CodeItem> convertedNotes = new ArrayList<>();
        codeList.getCodes().forEach(orci -> convertedNotes.add(
                (orci instanceof CodeItem.RangedCodeItem) ?
                        new CodeItem.RangedCodeItem((CodeItem.RangedCodeItem) orci, true) :
                        new CodeItem(orci, true)));
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), convertedNotes);
    }
}
