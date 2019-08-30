package no.ssb.klass.core.model;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class LanguageTest {

    @Test
    public void prioritizedContainsAllLanguages() {
        // given
        Language[] prioritizedLanguages = Language.getDefaultPrioritizedOrder();
        List<Language> allLanguages = Lists.newArrayList(Language.values());

        // when
        for (Language prioritizedLanguage : prioritizedLanguages) {
            allLanguages.remove(prioritizedLanguage);
        }

        // then
        assertEquals(0, allLanguages.size());
    }
}
