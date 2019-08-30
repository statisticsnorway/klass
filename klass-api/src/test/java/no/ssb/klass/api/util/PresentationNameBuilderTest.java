package no.ssb.klass.api.util;

import static org.junit.Assert.*;

import org.junit.Test;

public class PresentationNameBuilderTest {

    @Test
    public void replace() {
        // given
        PresentationNameBuilder subject = new PresentationNameBuilder("{code} - {name} - {shortname}");

        // when
        String result = subject.presentationName("newcode", "newname", "newshortname");

        // then
        assertEquals("newcode - newname - newshortname", result);
    }

    @Test
    public void lowercase() {
        // given
        PresentationNameBuilder subject = new PresentationNameBuilder(
                "{lowercase(code)} - {lowercase(name)} - {lowercase(shortname)}");

        // when
        String result = subject.presentationName("CODE", "NAME", "SHORTname");

        // then
        assertEquals("code - name - shortname", result);
    }

    @Test
    public void uppercase() {
        // given
        PresentationNameBuilder subject = new PresentationNameBuilder(
                "{uppercase(code)} - {uppercase(name)} - {uppercase(shortname)}");

        // when
        String result = subject.presentationName("code", "name", "shortname");

        // then
        assertEquals("CODE - NAME - SHORTNAME", result);
    }

    @Test
    public void capitalize() {
        // given
        PresentationNameBuilder subject = new PresentationNameBuilder(
                "{capitalize(code)} - {capitalize(name)} - {capitalize(shortname)}");

        // when
        String result = subject.presentationName("code", "name", "shortname");

        // then
        assertEquals("Code - Name - Shortname", result);
    }
}
