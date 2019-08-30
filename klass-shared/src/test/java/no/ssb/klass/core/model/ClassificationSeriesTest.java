package no.ssb.klass.core.model;

import static org.junit.Assert.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import com.google.common.base.Strings;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.ConstantClockSource;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationSeriesTest {

    @Test
    public void getClassificationVersionsInRange() {
        // given
        ClassificationSeries subject = createClassificationWithThreeVersions();
        DateRange dateRange = DateRange.create("2014-01-01", "2016-01-01");

        // when
        List<ClassificationVersion> result = subject.getClassificationVersionsInRange(dateRange);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void getClassificationVersionsInRangeMoreVersions() {
        // given
        ClassificationSeries subject = createClassificationWithThreeVersions();
        DateRange dateRange = DateRange.create("2013-01-01", "2017-01-01");

        // when
        List<ClassificationVersion> result = subject.getClassificationVersionsInRange(dateRange);

        // then
        assertEquals(3, result.size());
    }

    @Test
    public void getChangeTables() {
        // given
        LocalDate start = TimeUtil.createDate("2014-01-01");
        ClassificationSeries subject = TestUtil.createClassification("classification");
        ClassificationVersion firstVersion = TestUtil.createClassificationVersion(DateRange.create(start, start
                .plusYears(2)));
        ClassificationVersion secondVersion = TestUtil.createClassificationVersion(DateRange.create(start.plusYears(2),
                start.plusYears(4)));
        subject.addClassificationVersion(firstVersion);
        subject.addClassificationVersion(secondVersion);
        secondVersion.addCorrespondenceTable(TestUtil.createCorrespondenceTable(secondVersion, firstVersion));

        // when
        List<CorrespondenceTable> result = subject.getChangeTables(DateRange.create(start, start.plusYears(4)), null);

        // then
        assertEquals(1, result.size());

    }

    @Test(expected = KlassResourceNotFoundException.class)
    public void getChangeTableNotFound() {
        // given
        LocalDate start = TimeUtil.createDate("2014-01-01");
        ClassificationSeries subject = TestUtil.createClassification("classification");
        ClassificationVersion firstVersion = TestUtil.createClassificationVersion(DateRange.create(start, start
                .plusYears(2)));
        ClassificationVersion secondVersion = TestUtil.createClassificationVersion(DateRange.create(start.plusYears(2),
                start.plusYears(4)));
        subject.addClassificationVersion(firstVersion);
        subject.addClassificationVersion(secondVersion);

        // when
        subject.getChangeTables(DateRange.create(start, start.plusYears(4)), null);

        // then expect exception
    }

    @Test
    public void getNameSupportsAllLanguages() {
        // given
        ClassificationSeries subject = TestUtil.createClassification("classification");

        // then no exception
        for (Language language : Language.values()) {
            subject.getName(language);
        }
    }

    @Test
    public void getName() {
        // given
        ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "nn", "en"), Translatable
                .create("description", Language.NB), false, Language.NB, ClassificationType.CLASSIFICATION,
                TestUtil.createUser());

        // then
        assertEquals("no", subject.getName(Language.NB));
        assertEquals("nn", subject.getName(Language.NN));
        assertEquals("en", subject.getName(Language.EN));
    }

    @Test
    public void getNameInPrimaryLanguage() {
        // given
        ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "nn", "en"), Translatable
                .create("description", Language.EN), false, Language.EN, ClassificationType.CLASSIFICATION,
                TestUtil.createUser());

        // then
        assertEquals("en", subject.getNameInPrimaryLanguage());
    }

    @Test
    public void getDescription() {
        // given
        ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "nn", "en"),
                new Translatable("no", "nn", "en"), false, Language.NB, ClassificationType.CLASSIFICATION,
                TestUtil.createUser());

        // then
        assertEquals("no", subject.getDescription(Language.NB));
        assertEquals("nn", subject.getDescription(Language.NN));
        assertEquals("en", subject.getDescription(Language.EN));
    }

    @Test
    public void setDeletedUpdatesName() {
        try {
            // given
            Date now = new Date();
            TimeUtil.setClockSource(new ConstantClockSource(now));
            ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "nn", "en"),
                    new Translatable("no", "nn", "en"), false, Language.NB, ClassificationType.CLASSIFICATION, TestUtil
                            .createUser());

            // when
            subject.setDeleted();

            // then
            assertEquals("no" + " :: " + now.getTime(), subject.getName(Language.NB));
            assertEquals("nn" + " :: " + now.getTime(), subject.getName(Language.NN));
            assertEquals("en" + " :: " + now.getTime(), subject.getName(Language.EN));
        } finally {
            TimeUtil.revertClockSource();
        }
    }

    @Test
    public void setDeletedDoNotUpdateEmptyNames() {
        try {
            // given
            Date now = new Date();
            TimeUtil.setClockSource(new ConstantClockSource(now));
            ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "", null),
                    new Translatable("no", "nn", "en"), false, Language.NB, ClassificationType.CLASSIFICATION, TestUtil
                            .createUser());

            // when
            subject.setDeleted();

            // then
            assertEquals("no" + " :: " + now.getTime(), subject.getName(Language.NB));
            assertEquals(true, Strings.isNullOrEmpty(subject.getName(Language.NN)));
            assertEquals(true, Strings.isNullOrEmpty(subject.getName(Language.EN)));
        } finally {
            TimeUtil.revertClockSource();
        }
    }

    @Test
    public void getDescriptionInPrimaryLanguage() {
        // given
        ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "nn", "en"), new Translatable(
                "no", "nn", "en"), false, Language.NN, ClassificationType.CLASSIFICATION, TestUtil.createUser());

        // then
        assertEquals("nn", subject.getDescriptionInPrimaryLanguage());
    }

    @Test
    public void getVersionByRange() {
        DateRange dateRangeNoMatch1 = DateRange.create(LocalDate.of(2016, 1, 1), LocalDate.of(2016, 12, 1));
        DateRange dateRangeMatch = DateRange.create(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 12, 1));
        DateRange dateRangeNoMatch2 = DateRange.create(LocalDate.of(2014, 1, 1), LocalDate.of(2014, 12, 1));

        ClassificationVersion classificationVersion1 = new ClassificationVersion(dateRangeNoMatch1);
        ClassificationVersion classificationVersion2 = new ClassificationVersion(dateRangeMatch);
        ClassificationVersion classificationVersion3 = new ClassificationVersion(dateRangeNoMatch2);
        ClassificationSeries subject = new ClassificationSeries(new Translatable("no", "nn", "en"),
                new Translatable("no", "nn", "en"), false, Language.NB, ClassificationType.CLASSIFICATION,
                TestUtil.createUser());
        subject.addClassificationVersion(classificationVersion1);
        subject.addClassificationVersion(classificationVersion2);
        subject.addClassificationVersion(classificationVersion3);
        ClassificationVersion matchedClassificationVersion = subject.getVersionByRange(dateRangeMatch);
        assertEquals(classificationVersion2, matchedClassificationVersion);
    }

    @Test
    public void getClassificationVersionFiltersDeleted() {
        // given
        ClassificationSeries subject = createClassificationWithThreeVersions();
        subject.getClassificationVersions().get(0).setDeleted();

        // when
        List<ClassificationVersion> result = subject.getClassificationVersions();

        // then
        assertEquals(2, result.size());
    }

    @Test
    public void getClassificationVersionsInRangeFiltersDeleted() {
        // given
        final DateRange dateRange = DateRange.create("2012-01-01", "2014-01-01");
        ClassificationSeries subject = TestUtil.createClassification("anyname");
        ClassificationVersion version = TestUtil.createClassificationVersion(dateRange);
        version.setDeleted();
        subject.addClassificationVersion(version);

        // when
        List<ClassificationVersion> result = subject.getClassificationVersionsInRange(dateRange);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getChangeTablesFiltersDeleted() {
        // given
        LocalDate start = TimeUtil.createDate("2014-01-01");
        ClassificationSeries subject = TestUtil.createClassification("classification");
        ClassificationVersion firstVersion = TestUtil.createClassificationVersion(DateRange.create(start, start
                .plusYears(2)));
        ClassificationVersion secondVersion = TestUtil.createClassificationVersion(DateRange.create(start.plusYears(2),
                start.plusYears(4)));
        subject.addClassificationVersion(firstVersion);
        subject.addClassificationVersion(secondVersion);
        firstVersion.setDeleted();
        secondVersion.setDeleted();
        secondVersion.addCorrespondenceTable(TestUtil.createCorrespondenceTable(secondVersion, firstVersion));

        // when
        List<CorrespondenceTable> result = subject.getChangeTables(DateRange.create(start, start.plusYears(4)), null);

        // then
        assertEquals(0, result.size());

    }

    @Test
    public void getNewestVersionFiltersDeleted() {
        // given
        ClassificationSeries subject = TestUtil.createClassification("anyname");
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        version.setDeleted();
        subject.addClassificationVersion(version);

        // when
        ClassificationVersion result = subject.getNewestVersion();

        // then
        assertNull(result);
    }

    @Test
    public void getClassificationVersionsSortedByFromDateReversedFiltersDeleted() {
        // given
        ClassificationSeries subject = TestUtil.createClassification("anyname");
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        version.setDeleted();
        subject.addClassificationVersion(version);

        // when
        List<ClassificationVersion> result = subject.getClassificationVersionsSortedByFromDateReversed();

        // then
        assertEquals(0, result.size());
    }

    private ClassificationSeries createClassificationWithThreeVersions() {
        ClassificationSeries classification = TestUtil.createClassification("anyname");
        classification.addClassificationVersion(TestUtil.createClassificationVersion(DateRange.create("2012-01-01",
                "2014-01-01")));
        classification.addClassificationVersion(TestUtil.createClassificationVersion(DateRange.create("2014-01-01",
                "2016-01-01")));
        classification.addClassificationVersion(TestUtil.createClassificationVersion(DateRange.create("2016-01-01",
                "2018-01-01")));
        return classification;
    }
}
