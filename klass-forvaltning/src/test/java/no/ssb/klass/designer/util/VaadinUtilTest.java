package no.ssb.klass.designer.util;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

import no.ssb.klass.core.util.TimeUtil;

public class VaadinUtilTest {

    @Test
    public void getLastComponentWhenEmptyLayout() {
        // given
        HorizontalLayout layout = new HorizontalLayout();

        // when
        Optional<Component> component = VaadinUtil.getLastComponent(layout);

        // then
        assertFalse(component.isPresent());
    }

    @Test
    public void getLastComponent() {
        // given
        Button first = new Button();
        Button second = new Button();
        HorizontalLayout layout = new HorizontalLayout(first, second);

        // when
        Optional<Component> component = VaadinUtil.getLastComponent(layout);

        // then
        assertTrue(component.isPresent());
        assertEquals(second, component.get());
    }

    @Test
    public void convertToInclusive() {
        // given
        LocalDate toDate = TimeUtil.createDate(2016, 01, 01);

        // when
        LocalDate toDateInclusive = VaadinUtil.convertToInclusive(toDate);

        // then
        assertEquals(toDate.minusMonths(1), toDateInclusive);
    }

    @Test
    public void convertToExclusive() {
        // given
        LocalDate toDate = TimeUtil.createDate(2016, 12, 01);

        // when
        LocalDate toDateExclusive = VaadinUtil.convertToExclusive(toDate);

        // then
        assertEquals(toDate.plusMonths(1), toDateExclusive);
    }
}
