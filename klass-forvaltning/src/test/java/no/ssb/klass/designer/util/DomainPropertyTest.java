package no.ssb.klass.designer.util;

import static org.junit.Assert.*;

import java.beans.PropertyDescriptor;

import org.junit.Test;
import org.springframework.beans.BeanUtils;

public class DomainPropertyTest {

    @Test
    public void checkDomainProperties() {
        for (DomainProperty domainProperty : DomainProperty.values()) {
            PropertyDescriptor property = BeanUtils.getPropertyDescriptor(domainProperty.getClazz(), domainProperty
                    .getProperty());
            assertNotNull(createErrorMessage("No property: ", domainProperty), property);
            assertNotNull(createErrorMessage("No read method for property: ", domainProperty), property.getReadMethod());
        }
    }

    private String createErrorMessage(String part, DomainProperty property) {
        return part + ": " + property.getProperty() + " in class: " + property.getClazz().getSimpleName();
    }
}
