package no.ssb.klass.core.model;

import static org.junit.Assert.*;

import java.time.LocalDate;

import org.junit.Test;

import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.testutil.TestUtil;

public class CorrespondenceTableTest {
    private static final int ALL_LEVELS = 0;
    private static final int EXISTING_LEVEL = 1;
    private static final int NOT_EXISTING_LEVEL = 2;

    @Test
    public void correspondenceTableWithAllLevels() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable(ALL_LEVELS, ALL_LEVELS);

        // then
        assertEquals(false, subject.getSourceLevel().isPresent());
        assertEquals(false, subject.getTargetLevel().isPresent());
    }

    @Test
    public void correspondenceTableWithSpecificLevels() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable(EXISTING_LEVEL, EXISTING_LEVEL);

        // then
        assertEquals(true, subject.getSourceLevel().isPresent());
        assertEquals(true, subject.getTargetLevel().isPresent());
    }

    @Test(expected = IllegalArgumentException.class)
    public void correspondenceTableWithNotExistingSourceLevel() {
        // given
        createCorrespondenceTable(NOT_EXISTING_LEVEL, EXISTING_LEVEL);

        // then expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void correspondenceTableWithNotExistingTargetLevel() {
        // given
        createCorrespondenceTable(EXISTING_LEVEL, NOT_EXISTING_LEVEL);

        // then expect exception
    }

    @Test
    public void correspondenceTableReferringDeletedLevel() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable(EXISTING_LEVEL, EXISTING_LEVEL);

        // when
        subject.getSource().deleteLevel(subject.getSource().getLevel(EXISTING_LEVEL));

        // then
        assertEquals(false, subject.getSourceLevel().isPresent());
    }

    @Test
    public void getOccuredSourceVersionLatest() {
        // given
        final String sourceFrom = "2012-01-01";
        final String targetFrom = "2011-01-01";
        CorrespondenceTable subject = createCorrespondenceTable(sourceFrom, targetFrom);

        // when
        LocalDate result = subject.getOccured();

        // then
        assertEquals(TimeUtil.createDate(sourceFrom), result);
    }

    @Test
    public void getOccuredTargetVersionLatest() {
        // given
        final String sourceFrom = "2011-01-01";
        final String targetFrom = "2012-01-01";
        CorrespondenceTable subject = createCorrespondenceTable(sourceFrom, targetFrom);

        // when
        LocalDate result = subject.getOccured();

        // then
        assertEquals(TimeUtil.createDate(targetFrom), result);
    }

    /**
     * <pre>
     *    source   |--------------|
     *    target          |------------|
     *    expected        |-------|
     * </pre>
     */
    @Test
    public void getDateRangeSourceFirst() {
        // given
        final DateRange sourceDateRange = DateRange.create("2010-01-01", "2014-01-01");
        final DateRange targetDateRange = DateRange.create("2012-01-01", "2016-01-01");
        CorrespondenceTable subject = createCorrespondenceTable(sourceDateRange, targetDateRange);

        // when
        DateRange result = subject.getDateRange();

        // then
        DateRange expectedDateRange = DateRange.create("2012-01-01", "2014-01-01");
        assertEquals(expectedDateRange, result);
    }

    /**
     * <pre>
     *    source          |--------------|
     *    target     |------------|
     *    expected        |-------|
     * </pre>
     */
    @Test
    public void getDateRangeTargetFirst() {
        // given
        final DateRange sourceDateRange = DateRange.create("2012-01-01", "2016-01-01");
        final DateRange targetDateRange = DateRange.create("2010-01-01", "2014-01-01");
        CorrespondenceTable subject = createCorrespondenceTable(sourceDateRange, targetDateRange);

        // when
        DateRange result = subject.getDateRange();

        // then
        DateRange expectedDateRange = DateRange.create("2012-01-01", "2014-01-01");
        assertEquals(expectedDateRange, result);
    }

    /**
     * <pre>
     *    source    |------------------|
     *    target          |-------|
     *    expected        |-------|
     * </pre>
     */
    @Test
    public void getDateRangeContained() {
        // given
        final DateRange sourceDateRange = DateRange.create("2010-01-01", "2016-01-01");
        final DateRange targetDateRange = DateRange.create("2012-01-01", "2014-01-01");
        CorrespondenceTable subject = createCorrespondenceTable(sourceDateRange, targetDateRange);

        // when
        DateRange result = subject.getDateRange();

        // then
        DateRange expectedDateRange = DateRange.create("2012-01-01", "2014-01-01");
        assertEquals(expectedDateRange, result);
    }

    /**
     * <pre>
     *    source    |---------|
     *    target              |-------|
     *    expected  exception
     * </pre>
     */
    @Test(expected = IllegalArgumentException.class)
    public void getDateRangeNotOverlaping() {
        // given
        final DateRange sourceDateRange = DateRange.create("2010-01-01", "2012-01-01");
        final DateRange targetDateRange = DateRange.create("2012-01-01", "2014-01-01");
        CorrespondenceTable subject = createCorrespondenceTable(sourceDateRange, targetDateRange);

        // when
        subject.getDateRange();

        // then expect exception
    }

    @Test
    public void addChangelog() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        subject.publish(Language.NB);

        // when
        subject.addChangelog(new Changelog("user", "description"));

        // then
        assertEquals(1, subject.getChangelogs().size());
    }

    @Test(expected = IllegalStateException.class)
    public void addChangelogWhenNotPublished() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        for (Language language : Language.values()) {
            subject.unpublish(language);
        }

        // when
        subject.addChangelog(new Changelog("user", "description"));

        // then expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void addCorrespondenceMapVerifiesNotAlreadyPresent() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        subject.addCorrespondenceMap(createCorrespondenceMap("source", "target"));

        // when
        subject.addCorrespondenceMap(createCorrespondenceMap("source", "target"));

        // then expect exception
    }

    @Test
    public void updateCorrespondenceMapSource() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        CorrespondenceMap correspondenceMap = createCorrespondenceMap("source", "target");
        subject.addCorrespondenceMap(correspondenceMap);
        ClassificationItem newSourceItem = TestUtil.createClassificationItem("newSource", "source");

        // when
        subject.updateCorrespondenceMapSource(correspondenceMap, newSourceItem);

        // then
        assertEquals("newSource", correspondenceMap.getSource().get().getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCorrespondenceMapSourceVerifiesNotAlreadyPresent() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        subject.addCorrespondenceMap(createCorrespondenceMap("source", "target"));
        CorrespondenceMap correspondenceMap = createCorrespondenceMap("otherSource", "target");
        subject.addCorrespondenceMap(correspondenceMap);
        ClassificationItem sourceItem = TestUtil.createClassificationItem("source", "source");

        // when
        subject.updateCorrespondenceMapSource(correspondenceMap, sourceItem);

        // then expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCorrespondenceMapSourceDoesNotAllowBothSourceAndTargetToBeNull() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        CorrespondenceMap correspondenceMap = new CorrespondenceMap(TestUtil.createClassificationItem("code",
                "officialName"), null);
        subject.addCorrespondenceMap(correspondenceMap);

        // when
        subject.updateCorrespondenceMapSource(correspondenceMap, null);

        // then expect exception
    }

    @Test
    public void updateCorrespondenceMapTarget() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        CorrespondenceMap correspondenceMap = createCorrespondenceMap("source", "target");
        subject.addCorrespondenceMap(correspondenceMap);
        ClassificationItem newTargetItem = TestUtil.createClassificationItem("newTarget", "target");

        // when
        subject.updateCorrespondenceMapTarget(correspondenceMap, newTargetItem);

        // then
        assertEquals("newTarget", correspondenceMap.getTarget().get().getCode());
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCorrespondenceMapTargetVerifiesNotAlreadyPresent() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        subject.addCorrespondenceMap(createCorrespondenceMap("source", "target"));
        CorrespondenceMap correspondenceMap = createCorrespondenceMap("source", "otherTarget");
        subject.addCorrespondenceMap(correspondenceMap);
        ClassificationItem targetItem = TestUtil.createClassificationItem("target", "target");

        // when
        subject.updateCorrespondenceMapTarget(correspondenceMap, targetItem);

        // then expect exception
    }

    @Test(expected = IllegalArgumentException.class)
    public void updateCorrespondenceMapTargetDoesNotAllowBothSourceAndTargetToBeNull() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        CorrespondenceMap correspondenceMap = new CorrespondenceMap(null, TestUtil.createClassificationItem("code",
                "officialName"));
        subject.addCorrespondenceMap(correspondenceMap);

        // when
        subject.updateCorrespondenceMapTarget(correspondenceMap, null);

        // then expect exception
    }

    @Test
    public void alreadyContainsIdenticalMap() {
        // given
        CorrespondenceTable subject = createCorrespondenceTable();
        subject.addCorrespondenceMap(createCorrespondenceMap("source", "target"));
        CorrespondenceMap present = createCorrespondenceMap("source", "target");
        CorrespondenceMap notPresent = createCorrespondenceMap("otherSource", "otherTarget");

        // then
        assertEquals(true, subject.alreadyContainsIdenticalMap(present));
        assertEquals(false, subject.alreadyContainsIdenticalMap(notPresent));
    }

    private CorrespondenceMap createCorrespondenceMap(String sourceCode, String targetCode) {
        ClassificationItem sourceItem = TestUtil.createClassificationItem(sourceCode, "source");
        ClassificationItem targetItem = TestUtil.createClassificationItem(targetCode, "target");
        return new CorrespondenceMap(sourceItem, targetItem);
    }

    private CorrespondenceTable createCorrespondenceTable() {
        return createCorrespondenceTable("2011-01-01", "2012-01-01");
    }

    private CorrespondenceTable createCorrespondenceTable(int sourceLevelNumber, int targetLevelNumber) {
        ClassificationVersion source = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        TestUtil.createClassification("source").addClassificationVersion(source);
        source.addNextLevel();
        ClassificationVersion target = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        TestUtil.createClassification("target").addClassificationVersion(target);
        target.addNextLevel();
        return TestUtil.createCorrespondenceTable("description", source, sourceLevelNumber, target, targetLevelNumber);
    }

    private CorrespondenceTable createCorrespondenceTable(DateRange sourceDateRange, DateRange targetDateRange) {
        ClassificationVersion source = TestUtil.createClassificationVersion(sourceDateRange);
        TestUtil.createClassification("source").addClassificationVersion(source);
        ClassificationVersion target = TestUtil.createClassificationVersion(targetDateRange);
        TestUtil.createClassification("target").addClassificationVersion(target);
        return TestUtil.createCorrespondenceTable(source, target);
    }

    private CorrespondenceTable createCorrespondenceTable(String sourceFrom, String targetFrom) {
        return createCorrespondenceTable(DateRange.create(sourceFrom, "2014-01-01"), DateRange.create(targetFrom,
                "2014-01-01"));
    }
}
