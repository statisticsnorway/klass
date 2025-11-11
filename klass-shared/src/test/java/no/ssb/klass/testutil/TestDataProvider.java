package no.ssb.klass.testutil;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.Translatable;

import java.util.Arrays;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * @author Mads Lundemo, SSB.
 */
public final class TestDataProvider {

    public static final String KOMMUNEINNDELING_NAVN_NO = "Standard for kommuneinndeling";
    public static final String KOMMUNEINNDELING_NAVN_NN = "Standard for kommuneinndeling (Nynorsk)";
    public static final String KOMMUNEINNDELING_NAVN_EN = "Standard for kommuneinndeling(English)";

    public static final String KOMMUNEINNDELING_BESKRIVELSE_NO =
            "kommune inndelingen er en administrativ inndeling av kommuner i Norge";
    public static final String KOMMUNEINNDELING_BESKRIVELSE_NN = "kommune beskrivelse";
    public static final String KOMMUNEINNDELING_BESKRIVELSE_EN = "English description";

    public static final String BYDELSINNDELING_NAVN_NO = "Standard for bydelsinndeling";
    public static final String BYDELSINNDELING_BESKRIVELSE_NO =
            "Bydel utgjør geografiske områder i en kommune";
    public static final String BADMINTON_NAVN_NO = "Standard for Badminton";
    public static final String BADMINTON_BESKRIVELSE_NO = "Testing search results for badminton";
    public static final String SPORT_NAVN_NO = "Standard for sport";
    public static final String SPORT_BESKRIVELSE_NO = "Testing search results for badminton";
    public static final String BADMINTON_KODELISTE_NAVN = "Badminton";
    public static final String BADMINTON_KODELISTE_BESKRIVELSE =
            "Testing when version owning section is different from variant owning section badminton";

    public static final String COPYRIGHTED_CODELIST_NAVN_NO = "Kodeliste for ICD-10";
    public static final String COPYRIGHTED_CODELIST_BESKRIVELSE =
            "Den internasjonale statistiske klassifikasjonen av sykdommer og beslektede helseproblemer";

    public static final String FAMILIEGRUPPERING_NAVN_NO = "Standard for gruppering av familier";
    public static final String FAMILIEGRUPPERING_BESKRIVELSE_NO =
            "Standarden beskriver de ulike familietypene som i dag brukes i SSBs familistatistikk";

    public static final String TEN_YEARS_LATER =
            (new GregorianCalendar().get(Calendar.YEAR) + 10) + "";
    public static final String TEN_YEARS_LATER_DATE =
            (new GregorianCalendar().get(Calendar.YEAR) + 10) + "-01-01";
    public static final String FIFTEEN_YEARS_LATER_DATE =
            (new GregorianCalendar().get(Calendar.YEAR) + 15) + "-01-01";
    public static final String TWENTY_YEARS_LATER_DATE =
            (new GregorianCalendar().get(Calendar.YEAR) + 20) + "-01-01";

    private TestDataProvider() {}

    public static ClassificationSeries createClassificationKommuneinndeling() {
        ClassificationSeries classification =
                TestUtil.createClassification(
                        KOMMUNEINNDELING_NAVN_NO,
                        KOMMUNEINNDELING_NAVN_NN,
                        KOMMUNEINNDELING_NAVN_EN,
                        KOMMUNEINNDELING_BESKRIVELSE_NO,
                        KOMMUNEINNDELING_BESKRIVELSE_NN,
                        KOMMUNEINNDELING_BESKRIVELSE_EN);

        ClassificationVersion versionFuture =
                TestUtil.createClassificationVersion(
                        DateRange.create(TestDataProvider.TEN_YEARS_LATER_DATE, null));

        Level levelFuture = TestUtil.createLevel(1);
        versionFuture.addLevel(levelFuture);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("0101", "Halden"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("0104", "Moss"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("0301", "Oslo"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("5043", "Raarvihke Røyrvik"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("1939", "Omasvuotna Storfjord Omasvuonon"),
                levelFuture.getLevelNumber(),
                null);
        classification.addClassificationVersion(versionFuture);

        ClassificationVersion version2014 =
                TestUtil.createClassificationVersion(
                        DateRange.create("2014-01-01", TEN_YEARS_LATER_DATE));

        Level level = TestUtil.createLevel(1);
        version2014.addLevel(level);
        version2014.addClassificationItem(
                TestUtil.createClassificationItem("0101", "Halden"), level.getLevelNumber(), null);
        version2014.addClassificationItem(
                TestUtil.createClassificationItem("0104", "Moss"), level.getLevelNumber(), null);
        version2014.addClassificationItem(
                TestUtil.createClassificationItem("0301", "Oslo"), level.getLevelNumber(), null);
        version2014.addClassificationItem(
                TestUtil.createClassificationItem(
                        "1739",
                        "Raarvihke Røyrvik",
                        null,
                        "13. juni 2014 ble det vedtatt at kommunenavnet skulle være tospråklig (samisk -norsk)"),
                level.getLevelNumber(),
                null);
        version2014.addClassificationItem(
                TestUtil.createClassificationItem("1939", "Omasvuotna Storfjord Omasvuonon"),
                level.getLevelNumber(),
                null);
        classification.addClassificationVersion(version2014);

        ClassificationVersion version2012 =
                TestUtil.createClassificationVersion(DateRange.create("2012-01-01", "2014-01-01"));

        Level level2012 = TestUtil.createLevel(1);
        version2012.addLevel(level2012);
        version2012.addClassificationItem(
                TestUtil.createClassificationItem("0101", "Halden"),
                level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(
                TestUtil.createClassificationItem("0104", "Moss"),
                level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(
                TestUtil.createClassificationItem("0301", "Oslo"),
                level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(
                TestUtil.createClassificationItem(
                        "1739",
                        "Røyrvik",
                        null,
                        "Tospråklig navn Raarvihke - Røyrvik innført fra 1. 1. 2015. Før het kommunen Røyrvik."),
                level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(
                TestUtil.createClassificationItem("1939", "Storfjord"),
                level2012.getLevelNumber(),
                null);
        version2012.publish(Language.NB);
        classification.addClassificationVersion(version2012);

        ClassificationVersion version2008 =
                TestUtil.createClassificationVersion(DateRange.create("2008-01-01", "2012-01-01"));

        Level level2008 = TestUtil.createLevel(1);
        version2008.addLevel(level2008);
        version2008.addClassificationItem(
                TestUtil.createClassificationItem("0101", "Halden"),
                level2008.getLevelNumber(),
                null);
        version2008.addClassificationItem(
                TestUtil.createClassificationItem("0104", "Moss"),
                level2008.getLevelNumber(),
                null);
        version2008.addClassificationItem(
                TestUtil.createClassificationItem("0301", "Oslo"),
                level2008.getLevelNumber(),
                null);
        version2008.addClassificationItem(
                TestUtil.createClassificationItem("1739", "Røyrvik"),
                level2008.getLevelNumber(),
                null);
        version2008.addClassificationItem(
                TestUtil.createClassificationItem("1939", "Storfjord"),
                level2008.getLevelNumber(),
                null);
        version2008.publish(Language.NB);
        classification.addClassificationVersion(version2008);

        return classification;
    }

    public static CorrespondenceTable createAndAddChangeCorrespondenceTable(
            ClassificationSeries kommuneClassification) {
        ClassificationVersion kommune2014 =
                kommuneClassification.getClassificationVersions().get(1);
        ClassificationVersion kommune2012 =
                kommuneClassification.getClassificationVersions().get(2);
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(kommune2014, kommune2012);
        kommune2014.addCorrespondenceTable(correspondenceTable);
        correspondenceTable.addCorrespondenceMap(
                new CorrespondenceMap(kommune2014.findItem("1739"), kommune2012.findItem("1739")));
        correspondenceTable.addCorrespondenceMap(
                new CorrespondenceMap(kommune2014.findItem("1939"), kommune2012.findItem("1939")));
        return correspondenceTable;
    }

    public static CorrespondenceTable createAndAddChangeCorrespondenceTable_withOutdatedCodes(
            ClassificationSeries kommuneClassification) {
        ClassificationVersion kommune2014 =
                kommuneClassification.getClassificationVersions().get(1);
        ClassificationVersion kommune2012 =
                kommuneClassification.getClassificationVersions().get(2);
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(kommune2014, kommune2012);
        kommune2014.addCorrespondenceTable(correspondenceTable);
        correspondenceTable.addCorrespondenceMap(
                new CorrespondenceMap(null, kommune2012.findItem("1739")));
        correspondenceTable.addCorrespondenceMap(
                new CorrespondenceMap(null, kommune2012.findItem("1939")));
        correspondenceTable.addCorrespondenceMap(
                new CorrespondenceMap(kommune2014.findItem("1939"), null));
        return correspondenceTable;
    }

    public static CorrespondenceTable createAndAddChangeCorrespondenceTableFutureVersion(
            ClassificationSeries kommuneClassification) {
        ClassificationVersion kommuneFutureDate =
                kommuneClassification.getClassificationVersions().get(0);
        ClassificationVersion kommune2014 =
                kommuneClassification.getClassificationVersions().get(1);
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(kommuneFutureDate, kommune2014);
        kommuneFutureDate.addCorrespondenceTable(correspondenceTable);
        correspondenceTable.addCorrespondenceMap(
                new CorrespondenceMap(
                        kommuneFutureDate.findItem("5043"), kommune2014.findItem("1739")));
        return correspondenceTable;
    }

    public static ClassificationSeries createClassificationBydelsinndeling() {
        ClassificationSeries classification =
                TestUtil.createClassification(
                        BYDELSINNDELING_NAVN_NO, BYDELSINNDELING_BESKRIVELSE_NO);

        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create("2014-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030101", "Gamle Oslo"),
                level.getLevelNumber(),
                null);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030102", "Grünerløkka"),
                level.getLevelNumber(),
                null);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030103", "Sagene"),
                level.getLevelNumber(),
                null);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030104", "St. Hanshaugen"),
                level.getLevelNumber(),
                null);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030105", "Frogner"),
                level.getLevelNumber(),
                null);
        version.publish(Language.EN);
        version.publish(Language.NN);
        version.publish(Language.NB);
        classification.addClassificationVersion(version);

        ClassificationVersion versionFuture =
                TestUtil.createClassificationVersion(
                        DateRange.create(TestDataProvider.TEN_YEARS_LATER_DATE, null));
        Level levelFuture = TestUtil.createLevel(1);
        versionFuture.addLevel(levelFuture);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("030101", "Gamle Oslo"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("030102", "Grünerløkka"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("030103", "Sagene ny"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("030104", "St. Hanshaugen"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.addClassificationItem(
                TestUtil.createClassificationItem("030105", "Frogner"),
                levelFuture.getLevelNumber(),
                null);
        versionFuture.publish(Language.EN);
        versionFuture.publish(Language.NN);
        versionFuture.publish(Language.NB);
        classification.addClassificationVersion(versionFuture);
        return classification;
    }

    public static ClassificationSeries createFamiliegrupperingCodelist(User user) {
        ClassificationSeries classification =
                TestUtil.createCodelist(
                        FAMILIEGRUPPERING_NAVN_NO, FAMILIEGRUPPERING_BESKRIVELSE_NO);

        classification.setContactPerson(user);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create("2006-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("1.1.1", "Enpersonfamilie, person under 30 år"),
                level.getLevelNumber(),
                null);
        ClassificationVariant variant =
                TestUtil.createClassificationVariant(
                        "Variant - Tilleggsinndeling for familier", user);
        variant.addClassificationItem(
                TestUtil.createClassificationItem("A", "Enpersonfamilie"), 1, null);
        variant.addClassificationItem(TestUtil.createClassificationItem("B", "Ektepar"), 1, null);
        variant.addClassificationItem(
                TestUtil.createClassificationItem("A_", "Enpersonfamilie"),
                2,
                variant.findItem("A"));
        variant.addClassificationItem(
                TestUtil.createClassificationItem("BA", "Ektepar med barn (yngste barn 0-17 år)"),
                2,
                variant.findItem("B"));
        variant.addClassificationItem(
                TestUtil.createClassificationItem("BB", "Ektepar uten barn 0-17 år"),
                2,
                variant.findItem("B"));
        version.addClassificationVariant(variant);
        classification.addClassificationVersion(version);

        ClassificationVersion versionFuture = createFamiliegrupperingFutureVersion(user);
        classification.addClassificationVersion(versionFuture);

        return classification;
    }

    public static ClassificationSeries createBadmintonCodeList(User user, User user1, User user2) {
        ClassificationSeries classification =
                TestUtil.createCodelist(BADMINTON_KODELISTE_NAVN, BADMINTON_KODELISTE_BESKRIVELSE);

        classification.setContactPerson(user);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create("2006-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("0.1.1", "Fjærballer pro"),
                level.getLevelNumber(),
                null);
        ClassificationVariant variant =
                TestUtil.createClassificationVariant(
                        "Variant - Tilleggsinndeling for baller", user1);
        variant.addClassificationItem(TestUtil.createClassificationItem("A", "Fjærball"), 1, null);
        variant.addClassificationItem(
                TestUtil.createClassificationItem("B", "Tennisball"), 1, null);
        variant.addClassificationItem(
                TestUtil.createClassificationItem("A_", "Fjærball"), 2, variant.findItem("A"));
        variant.addClassificationItem(
                TestUtil.createClassificationItem("BA", "Tennisball med fjær"),
                2,
                variant.findItem("B"));
        variant.addClassificationItem(
                TestUtil.createClassificationItem("BB", "Tennisball pro"),
                2,
                variant.findItem("B"));
        version.addClassificationVariant(variant);
        ClassificationVariant variant2 =
                TestUtil.createClassificationVariant(
                        "Variant - Tilleggsinndeling for racketer", user2);
        variant2.addClassificationItem(
                TestUtil.createClassificationItem("Q", "Lang racket"), 1, null);
        variant2.addClassificationItem(
                TestUtil.createClassificationItem("B", "Kort racket"), 1, null);
        version.addClassificationVariant(variant2);
        classification.addClassificationVersion(version);

        return classification;
    }

    public static ClassificationSeries createBadmintonClassification(User user) {
        ClassificationSeries classification =
                TestUtil.createClassification(BADMINTON_NAVN_NO, BADMINTON_BESKRIVELSE_NO);

        classification.setContactPerson(user);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create("2014-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030101", "Spill"), level.getLevelNumber(), null);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030102", "Konkurranser"),
                level.getLevelNumber(),
                null);
        version.publish(Language.NB);
        classification.addClassificationVersion(version);

        return classification;
    }

    public static ClassificationSeries createSportClassification(User user) {
        ClassificationSeries classification =
                TestUtil.createClassification(SPORT_NAVN_NO, SPORT_BESKRIVELSE_NO);

        classification.setContactPerson(user);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create("2014-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030105", "Badminton"),
                level.getLevelNumber(),
                null);
        version.addClassificationItem(
                TestUtil.createClassificationItem("030106", "Tennis"),
                level.getLevelNumber(),
                null);
        version.publish(Language.NB);
        classification.addClassificationVersion(version);

        return classification;
    }

    public static ClassificationSeries createCopyrightedCodeList(User user) {
        ClassificationSeries classification =
                TestUtil.createCodelist(
                        COPYRIGHTED_CODELIST_NAVN_NO, COPYRIGHTED_CODELIST_BESKRIVELSE);

        classification.setContactPerson(user);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create("2025-01-01", TestDataProvider.TEN_YEARS_LATER_DATE));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("A00-B99", "Infeksjoner"),
                level.getLevelNumber(),
                null);
        version.publish(Language.NB);
        classification.addClassificationVersion(version);
        classification.setCopyrighted(true);
        return classification;
    }

    public static ClassificationVersion createFamiliegrupperingFutureVersion(User user) {

        ClassificationVersion version =
                TestUtil.createClassificationVersion(
                        DateRange.create(TestDataProvider.TEN_YEARS_LATER_DATE, null));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(
                TestUtil.createClassificationItem("1.1.1", "Enpersonfamilie, person under 30 år"),
                level.getLevelNumber(),
                null);
        ClassificationVariant variant =
                TestUtil.createClassificationVariantFuture(
                        "Variant - Tilleggsinndeling for familier", user);
        variant.addClassificationItem(
                TestUtil.createClassificationItem("C", "Enslig person med barn"), 1, null);
        variant.addClassificationItem(
                TestUtil.createClassificationItem("CA", "Enslig person med barn 0-5 år"),
                2,
                variant.findItem("C"));
        variant.addClassificationItem(
                TestUtil.createClassificationItem("CB", "Enslig person med barn 6-17 år"),
                2,
                variant.findItem("C"));
        version.addClassificationVariant(variant);
        return version;
    }

    public static CorrespondenceTable createAndAddCorrespondenceTable(
            ClassificationSeries kommuneClassification, ClassificationSeries bydelClassification) {
        ClassificationVersion kommune = kommuneClassification.getClassificationVersions().get(1);
        ClassificationVersion bydel = bydelClassification.getClassificationVersions().get(1);
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(kommune, bydel);
        for (String bydelCode : Arrays.asList("030101", "030102", "030103")) {
            correspondenceTable.addCorrespondenceMap(
                    new CorrespondenceMap(kommune.findItem("0301"), bydel.findItem(bydelCode)));
        }
        kommune.addCorrespondenceTable(correspondenceTable);
        return correspondenceTable;
    }

    public static CorrespondenceTable createAndAddCorrespondenceTableTableFutureVersion(
            ClassificationSeries kommuneClassification, ClassificationSeries bydelClassification) {
        ClassificationVersion kommuneFuture =
                kommuneClassification.getClassificationVersions().get(0);
        ClassificationVersion bydelFuture = bydelClassification.getClassificationVersions().get(0);
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(kommuneFuture, bydelFuture);
        for (String bydelCode : Arrays.asList("030101", "030102", "030103")) {
            correspondenceTable.addCorrespondenceMap(
                    new CorrespondenceMap(
                            kommuneFuture.findItem("0301"), bydelFuture.findItem(bydelCode)));
        }
        kommuneFuture.addCorrespondenceTable(correspondenceTable);
        return correspondenceTable;
    }

    public static ClassificationVersion createClassificationVersionWithTranslations() {
        ClassificationSeries classification =
                TestUtil.createCodelist(
                        FAMILIEGRUPPERING_NAVN_NO, FAMILIEGRUPPERING_BESKRIVELSE_NO);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));
        classification.addClassificationVersion(version);
        Level level1 = TestUtil.createLevel(1);
        Level level2 = TestUtil.createLevel(2);
        Level level3 = TestUtil.createLevel(3);
        version.addLevel(level1);
        version.addLevel(level2);
        version.addLevel(level3);

        ConcreteClassificationItem item1 =
                new ConcreteClassificationItem(
                        "030101",
                        new Translatable("Norge", "Noreg", "Norway"),
                        new Translatable("no", "nn", "en"),
                        new Translatable("ikke", "ikkje", "not"));

        ConcreteClassificationItem item2 =
                new ConcreteClassificationItem(
                        "030102",
                        new Translatable("Pinnsvin", "Bustyvel", "hedgehog"),
                        new Translatable("pi", "bu", "he"));

        ConcreteClassificationItem item3 =
                new ConcreteClassificationItem(
                        "030103",
                        new Translatable("bulldoser", "stålstut", "bulldozer"),
                        new Translatable("bs", "st", "bz"));

        version.addClassificationItem(item1, 1, null);
        version.addClassificationItem(item2, 2, item1);
        version.addClassificationItem(item3, 3, item2);
        return version;
    }
}
