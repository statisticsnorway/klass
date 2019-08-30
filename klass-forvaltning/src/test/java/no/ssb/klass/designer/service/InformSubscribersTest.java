package no.ssb.klass.designer.service;

import static org.junit.Assert.*;

import java.util.Optional;

import org.junit.Test;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.testutil.TestUtil;

public class InformSubscribersTest {
    @Test
    public void createNotInformSubscribers() {
        assertEquals(false, InformSubscribers.createNotInformSubscribers().isInformSubscribers());
    }

    @Test
    public void createWhenWasUnpublishedAndStillUnpublished() {
        // given
        ClassificationVersion version = createUnpublishedVersion();
        InformSubscribers subject = InformSubscribers.createWhenWasUnpublished(version);

        // when
        boolean result = subject.isInformSubscribers();

        // then
        assertEquals(false, result);
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
        assertEquals(true, result);
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
        assertEquals(false, result);
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
        assertEquals(true, result);
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
