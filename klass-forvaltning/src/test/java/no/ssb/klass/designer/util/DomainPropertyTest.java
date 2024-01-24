package no.ssb.klass.designer.util;

import static org.junit.jupiter.api.Assertions.*;

import java.beans.PropertyDescriptor;

import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;

public class DomainPropertyTest {

    @Test
    public void checkDomainProperties() {
        for (DomainProperty domainProperty : DomainProperty.values()) {
            PropertyDescriptor property = BeanUtils.getPropertyDescriptor(domainProperty.getClazz(), domainProperty
                    .getProperty());
            assertNotNull(property, createErrorMessage("No property: ", domainProperty));
            assertNotNull(property.getReadMethod(), createErrorMessage("No read method for property: ", domainProperty));
        }
    }

    private String createErrorMessage(String part, DomainProperty property) {
        return part + ": " + property.getProperty() + " in class: " + property.getClazz().getSimpleName();
    }
}
