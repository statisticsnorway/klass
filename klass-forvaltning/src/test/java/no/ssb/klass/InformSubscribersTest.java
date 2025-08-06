package no.ssb.klass;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.testutil.TestUtil;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InformSubscribersTest {
    @Test
    public void createNotInformSubscribers() {
        Assertions.assertFalse(InformSubscribers.createNotInformSubscribers().isInformSubscribers());
    }

    @Test
    public void createWhenWasUnpublishedAndStillUnpublished() {
        // given
        ClassificationVersion version = createUnpublishedVersion();
        InformSubscribers subject = InformSubscribers.createWhenWasUnpublished(version);

        // when
        boolean result = subject.isInformSubscribers();

        // then
        assertFalse(result);
    }

    @Test
    public void createWhenWasUnpublishedAndNowPublished() {
        // given
        ClassificationVersion version = createUnpublishedVersion();
        version.publish(Language.getDefault());
        InformSubscribers subject = InformSubscribers.createWhenWasUnpublished(version);

        // when
        boolean result = subject.isInformSubscribers();

        // then
        assertTrue(result);
    }

    @Test
    public void createWhenPublishedWithoutChangelog() {
        // given
        ClassificationVersion version = createUnpublishedVersion();
        version.publish(Language.getDefault());
        InformSubscribers subject = InformSubscribers.createWhenPublished(Optional.ofNullable(null));

        // when
        boolean result = subject.isInformSubscribers();

        // then
        assertFalse(result);
    }

    @Test
    public void createWhenPublishedWithChangelog() {
        // given
        ClassificationVersion version = createUnpublishedVersion();
        version.publish(Language.getDefault());
        InformSubscribers subject = InformSubscribers.createWhenPublished(Optional.of(new Changelog("changedBy",
                "description")));

        // when
        boolean result = subject.isInformSubscribers();

        // then
        assertTrue(result);
    }

    private ClassificationVersion createUnpublishedVersion() {
        ClassificationSeries classification = TestUtil.createClassification("name");
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        for (Language language : Language.values()) {
            version.unpublish(language);
        }
        classification.addClassificationVersion(version);

        return version;
    }
}
