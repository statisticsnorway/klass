package no.ssb.klass.core.util;

import static org.junit.jupiter.api.Assertions.*;

import org.hibernate.metamodel.RepresentationMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import no.ssb.klass.core.model.ClassificationSeries;

public class BaseEntityInterceptorTest {
    private BaseEntityInterceptor subject;

    @BeforeEach
    public void setup() {
        subject = new BaseEntityInterceptor();
    }

    @Test
    public void instantiate() {
        // given
        final String entityName = ClassificationSeries.class.getName();
        final Long id = 1L;

        // when
        ClassificationSeries result = (ClassificationSeries) subject.instantiate(entityName, RepresentationMode.POJO, id);

        // then
        assertEquals(id, result.getId());
    }
}
