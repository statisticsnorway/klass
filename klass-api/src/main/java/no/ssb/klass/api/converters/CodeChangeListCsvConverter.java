package no.ssb.klass.api.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.api.dto.CodeChangeItem;
import no.ssb.klass.api.dto.CodeChangeList;

public class CodeChangeListCsvConverter extends AbstractCsvConverter<CodeChangeList> {
    @Override
    protected boolean supports(Class<?> clazz) {
        return CodeChangeList.class.equals(clazz);
    }

    @Override
    protected void writeInternal(CodeChangeList codeChangeList, HttpOutputMessage outputMessage) throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(CodeChangeItem.class, codeChangeList.getCsvSeparator());
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), codeChangeList.getCodeChanges());
    }
}