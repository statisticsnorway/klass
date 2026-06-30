package no.ssb.klass;

import no.ssb.klass.core.model.*;
import no.ssb.klass.core.service.ChangeLogService;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.SubscriberService;
import no.ssb.klass.designer.service.ClassificationFacade;
import no.ssb.klass.designer.service.InformSubscribers;
import no.ssb.klass.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;

import java.util.Optional;

import static org.mockito.Mockito.*;

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
    public void saveClassification() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");

        // when
        subject.saveClassification(classification);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void saveCorrespondenceTableNotInform() {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();

        // when
        subject.saveCorrespondenceTable(correspondenceTable, createNotInformSubscribers());

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void saveCorrespondenceTableInform() {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();

        // when
        subject.saveCorrespondenceTable(correspondenceTable, createInformSubscribers());

        // then
        verifyInformSubscribers();
    }

    @Test
    public void saveVariantNotInform() {
        // given
        ClassificationVariant variant = createVariant();

        // when
        subject.saveVariant(variant, createNotInformSubscribers());

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void saveVariantInform() {
        // given
        ClassificationVariant variant = createVariant();

        // when
        subject.saveVariant(variant, createInformSubscribers());

        // then
        verifyInformSubscribers();
    }

    @Test
    public void saveVersionNotInform() {
        // given
        ClassificationVersion version = createVersion();

        // when
        subject.saveVersion(version, createNotInformSubscribers());

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void saveVersionInform() {
        // given
        ClassificationVersion version = createVersion();

        // when
        subject.saveVersion(version, createInformSubscribers());

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteClassification() throws Exception {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");

        // when
        subject.deleteClassification(TestUtil.createUser(), classification);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteUnpublishedCorrespondenceTable() throws Exception {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();
        for (Language language : Language.values()) {
            correspondenceTable.unpublish(language);
        }

        // when
        subject.deleteCorrespondenceTable(TestUtil.createUser(), correspondenceTable);

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void deletePublishedCorrespondenceTable() throws Exception {
        // given
        CorrespondenceTable correspondenceTable = createCorrespondenceTable();
        correspondenceTable.publish(Language.getDefault());

        // when
        subject.deleteCorrespondenceTable(TestUtil.createUser(), correspondenceTable);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteUnpublisedVariant() throws Exception {
        // given
        ClassificationVariant variant = createVariant();
        for (Language language : Language.values()) {
            variant.unpublish(language);
        }

        // when
        subject.deleteVariant(TestUtil.createUser(), variant);

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void deletePublisedVariant() throws Exception {
        // given
        ClassificationVariant variant = createVariant();
        variant.publish(Language.getDefault());

        // when
        subject.deleteVariant(TestUtil.createUser(), variant);

        // then
        verifyInformSubscribers();
    }

    @Test
    public void deleteUnpublishedVersion() throws Exception {
        // given
        ClassificationVersion version = createVersion();
        for (Language language : Language.values()) {
            version.unpublish(language);
        }

        // when
        subject.deleteVersion(TestUtil.createUser(), version);

        // then
        verifyNotInformSubscribers();
    }

    @Test
    public void deletePublishedVersion() throws Exception {
        // given
        ClassificationVersion version = createVersion();
        version.publish(Language.getDefault());

        // when
        subject.deleteVersion(TestUtil.createUser(), version);

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
        verify(subscriberServiceMock, timeout(2000)).informSubscribersOfUpdatedClassification(any(), any(), any());
    }
}
