package no.ssb.klass.api.converters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

import org.springframework.http.HttpOutputMessage;

import com.fasterxml.jackson.databind.ObjectWriter;

import no.ssb.klass.api.dto.CodeList;

public class CodeListCsvConverter extends AbstractCsvConverter<CodeList> {
    @Override
    protected boolean supports(Class<?> clazz) {
        return CodeList.class.equals(clazz);
    }

    @Override
    protected void writeInternal(CodeList codeList, HttpOutputMessage outputMessage) throws IOException {
        Charset charset = selectCharsetAndUpdateOutput(outputMessage);
        ObjectWriter writer = createWriter(codeList.codeItemsJavaType(), codeList.getCsvSeparator(), codeList.getCsvFields());
        writer.writeValue(new OutputStreamWriter(outputMessage.getBody(), charset), codeList.getCodes());
    }
}