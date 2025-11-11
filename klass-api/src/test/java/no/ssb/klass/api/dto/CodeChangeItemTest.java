package no.ssb.klass.api.dto;

import static org.junit.jupiter.api.Assertions.*;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.util.Translatable;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class CodeChangeItemTest {
    private static final String TARGET_CODE = "target";
    private static final String SOURCE_CODE = "source";

    @Test
    public void targetVersionOldest() {
        // given
        boolean targetIsOldest = true;
        CorrespondenceMap correspondenceMap = createCorrespondenceMap();

        // when
        CodeChangeItem subject =
                new CodeChangeItem(
                        correspondenceMap, targetIsOldest, LocalDate.now(), Language.getDefault());

        // then
        assertEquals(SOURCE_CODE, subject.getNewCode());
        assertEquals(TARGET_CODE, subject.getOldCode());
    }

    @Test
    public void sourceVersionOldest() {
        // given
        boolean sourceIsOldest = false;
        CorrespondenceMap correspondenceMap = createCorrespondenceMap();

        // when
        CodeChangeItem subject =
                new CodeChangeItem(
                        correspondenceMap, sourceIsOldest, LocalDate.now(), Language.getDefault());

        // then
        assertEquals(SOURCE_CODE, subject.getOldCode());
        assertEquals(TARGET_CODE, subject.getNewCode());
    }

    private CorrespondenceMap createCorrespondenceMap() {
        ClassificationItem sourceItem = createClassificationItem(SOURCE_CODE);
        ClassificationItem targetItem = createClassificationItem(TARGET_CODE);
        return new CorrespondenceMap(sourceItem, targetItem);
    }

    private ClassificationItem createClassificationItem(String code) {
        return new ConcreteClassificationItem(
                code, Translatable.create("name", Language.getDefault()), Translatable.empty());
    }
}
