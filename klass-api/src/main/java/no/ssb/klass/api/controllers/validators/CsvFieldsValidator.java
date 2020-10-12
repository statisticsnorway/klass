package no.ssb.klass.api.controllers.validators;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import no.ssb.klass.api.dto.CodeChangeItem;
import no.ssb.klass.api.dto.CodeItem;
import no.ssb.klass.api.dto.CorrespondenceItem;
import org.springframework.stereotype.Component;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Component
public class CsvFieldsValidator {

    private final CsvSchema correspondenceItemSchema = new CsvMapper().schemaFor(CorrespondenceItem.RangedCorrespondenceItem.class);
    private final CsvSchema codeItemSchema = new CsvMapper().schemaFor(CodeItem.class);
    private final CsvSchema codeChangeItemSchema = new CsvMapper().schemaFor(CodeChangeItem.class);

    public void validateFieldsCorrespondenceItem(List<String> csvFields)  {
        List<String> fieldsNotFound = csvFields.stream().filter(s -> correspondenceItemSchema.column(s) == null).collect(toList());
        if(!fieldsNotFound.isEmpty()) {
            throw new IllegalArgumentException("CorrespondenceList does not contain the following field(s): " + String.join(",", fieldsNotFound));
        }
    }
    public void validateFieldsCodeItem(List<String> csvFields)  {
        List<String> fieldsNotFound = csvFields.stream().filter(s -> codeItemSchema.column(s) == null).collect(toList());
        if(!fieldsNotFound.isEmpty()) {
            throw new IllegalArgumentException("CorrespondenceList does not contain the following field(s): " + String.join(",", fieldsNotFound));
        }
    }
    public void validateFieldsChangeItemSchema(List<String> csvFields)  {
        List<String> fieldsNotFound = csvFields.stream().filter(s -> codeChangeItemSchema.column(s) == null).collect(toList());
        if(!fieldsNotFound.isEmpty()) {
            throw new IllegalArgumentException("CorrespondenceList does not contain the following field(s): " + String.join(",", fieldsNotFound));
        }
    }

}
