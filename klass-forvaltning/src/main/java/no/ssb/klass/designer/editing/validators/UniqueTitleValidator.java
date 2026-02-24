package no.ssb.klass.designer.editing.validators;

import static com.google.common.base.Preconditions.*;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;

import com.vaadin.data.Validator;
import com.vaadin.ui.TextField;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.service.ClassificationFacade;

/**
 * @author Mads Lundemo, SSB.
 */
public class UniqueTitleValidator implements Validator {

    private final Language language;
    private final TextField prefixField;
    private final String currentName;
    private final ClassificationFacade classificationFacade;

    public UniqueTitleValidator(ClassificationFacade classificationFacade, Language language, TextField prefixField,
            String currentName) {
        this.classificationFacade = checkNotNull(classificationFacade);
        this.language = checkNotNull(language);
        this.prefixField = checkNotNull(prefixField);
        this.currentName = checkNotNull(currentName);
    }

    @Override
    public void validate(Object value) throws InvalidValueException {
        if (StringUtils.isNotEmpty((String) value)) {
            String title = prefixField.getValue() + StringUtils.trim((String) value);
            if (!title.equals(currentName)) {
                Optional<ClassificationSeries> series = classificationFacade.findOneClassificationSeriesWithName(title,
                        language);
                if (series.isPresent()) {
                    throw new InvalidValueException("Klassifikasjon med " + language.getDisplayName() + " tittel \""
                            + title + "\" eksisterer allerede");
                }
            }
        }
    }
}
