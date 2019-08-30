package no.ssb.klass.designer.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import no.ssb.klass.core.model.Changelog;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.service.ChangeLogService;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.SubscriberService;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationFacadeTest {
    private ClassificationFacade subject;
    private SubscriberService subscriberServiceMock;
    private ChangeLogService ChangeLogServiceMock;

    @Before
    public void setup() {
        subscriberServiceMock = mock(SubscriberService.class);
        ChangeLogServiceMock = mock(ChangeLogService.class);
        subject = new ClassificationFacade(mock(ClassificationService.class), mock(SearchService.class),
                subscriberServiceMock, ChangeLogServiceMock);
    }

    @Test
    public void saveAndIndexClassification() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");

        // when
        subject.saveAndIndexClassification(classification);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void saveAndIndexCorrespondenceTableNotInform() {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();

        // when
        subject.saveAndIndexCorrespondenceTable(correspondenceTable, createNotInformSubscribers());

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void saveAndIndexCorrespondenceTableInform() {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();

        // when
        subject.saveAndIndexCorrespondenceTable(correspondenceTable, createInformSubscribers());

        // then
        verifyInformSubscribers();
    }

    @Test
    public void saveAndIndexVariantNotInform() {
        // given
        ClassificationVariant variant = createVariant();

        // when
        subject.saveAndIndexVariant(variant, createNotInformSubscribers());

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void saveAndIndexVariantInform() {
        // given
        ClassificationVariant variant = createVariant();

        // when
        subject.saveAndIndexVariant(variant, createInformSubscribers());

        // then
        verifyInformSubscribers();
    }

    @Test
    public void saveAndIndexVersionNotInform() {
        // given
        ClassificationVersion version = createVersion();

        // when
        subject.saveAndIndexVersion(version, createNotInformSubscribers());

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void saveAndIndexVersionInform() {
        // given
        ClassificationVersion version = createVersion();

        // when
        subject.saveAndIndexVersion(version, createInformSubscribers());

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteAndIndexClassification() throws Exception {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");

        // when
        subject.deleteAndIndexClassification(TestUtil.createUser(), classification);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteAndIndexUnpublishedCorrespondenceTable() throws Exception {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();
        for (Language language : Language.values()) {
            correspondenceTable.unpublish(language);
        }

        // when
        subject.deleteAndIndexCorrespondenceTable(TestUtil.createUser(), correspondenceTable);

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void deleteAndIndexPublishedCorrespondenceTable() throws Exception {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();
        correspondenceTable.publish(Language.getDefault());

        // when
        subject.deleteAndIndexCorrespondenceTable(TestUtil.createUser(), correspondenceTable);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteAndIndexUnpublisedVariant() throws Exception {
        // given
        ClassificationVariant variant = createVariant();
        for (Language language : Language.values()) {
            variant.unpublish(language);
        }

        // when
        subject.deleteAndIndexVariant(TestUtil.createUser(), variant);

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void deleteAndIndexPublisedVariant() throws Exception {
        // given
        ClassificationVariant variant = createVariant();
        variant.publish(Language.getDefault());

        // when
        subject.deleteAndIndexVariant(TestUtil.createUser(), variant);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteAndIndexUnpublishedVersion() throws Exception {
        // given
        ClassificationVersion version = createVersion();
        for (Language language : Language.values()) {
            version.unpublish(language);
        }

        // when
        subject.deleteAndIndexVersion(TestUtil.createUser(), version);

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void deleteAndIndexPublishedVersion() throws Exception {
        // given
        ClassificationVersion version = createVersion();
        version.publish(Language.getDefault());

        // when
        subject.deleteAndIndexVersion(TestUtil.createUser(), version);

        // then
        verifyInformSubscribers();
    }

    private InformSubscribers createInformSubscribers() {
        return InformSubscribers.createWhenPublished(Optional.of(new Changelog("changedBy", "description")));
    }

    private CorrespondenceTable createCorrespondenceTable() {
        return TestUtil.createCorrespondenceTable(createVersion(), createVersion());
    }

    private InformSubscribers createNotInformSubscribers() {
        return InformSubscribers.createNotInformSubscribers();
    }

    private ClassificationVersion createVersion() {
        ClassificationSeries classification = TestUtil.createClassification("name");
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        classification.addClassificationVersion(version);
        return version;
    }

    private ClassificationVariant createVariant() {
        ClassificationVariant variant = TestUtil.createClassificationVariant("name", TestUtil.createUser());
        ClassificationVersion version = createVersion();
        version.addClassificationVariant(variant);
        return variant;
    }

    private void verifyNotInformSubscribers() {
        verify(subscriberServiceMock, never()).informSubscribersOfUpdatedClassification(any(), any(), any());
    }

    private void verifyInformSubscribers() {
        verify(subscriberServiceMock).informSubscribersOfUpdatedClassification(any(), any(), any());
    }
}
