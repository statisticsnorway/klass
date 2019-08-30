package no.ssb.klass.core.util;

import static org.junit.Assert.*;

import org.hibernate.EntityMode;
import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.model.ClassificationSeries;

public class BaseEntityInterceptorTest {
    private BaseEntityInterceptor subject;

    @Before
    public void setup() {
        subject = new BaseEntityInterceptor();
    }

    @Test
    public void instantiate() {
        // given
        final String entityName = ClassificationSeries.class.getName();
        final Long id = 1L;

        // when
        ClassificationSeries result = (ClassificationSeries) subject.instantiate(entityName, EntityMode.POJO, id);

        // then
        assertEquals(id, result.getId());
    }
}
