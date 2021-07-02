package no.ssb.klass.core.service;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.StatisticalClassification;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;

import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

final class ClassificationServiceHelper {

    private ClassificationServiceHelper() {
        // Utility class
    }

    static List<CodeDto> findVariantClassificationCodes(ClassificationSeries classification, String variantName,
                                                        Language language, DateRange dateRange, Boolean includeFuture) {
        boolean found = false;
        List<CodeDto> codes = new ArrayList<>();
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            if (version.isPublished(language) && version.showVersion(includeFuture)) {
                // search for variant name stem first
                ClassificationVariant variant = findClassificationVariant(variantName, language, version);
                if (variant == null || variant.isDraft()) {
                    continue;
                }
                if (variant.getDateRange().overlaps(dateRange)) {
                    if (!variant.isPublished(language)) {
                        throw new KlassResourceNotFoundException("ClassificationVariant: " + variant.getFullName(
                                language) + " "
                                + variant.getDateRange() + ". Is not published in language: "
                                + language);
                    }
                    found = true;
                    codes.addAll(mapClassificationItemsToCodes(variant, variant.getDateRange(), language));
                }
            }
        }
        if (!found) {
            throw new KlassResourceNotFoundException(classification.getName(language)
                    + " does not have a variant named: '" + variantName+"' in requested date range");
        }
        return codes;
    }

    private static ClassificationVariant findClassificationVariant(String variantName, Language language, ClassificationVersion version) {
        ClassificationVariant variant;
        variant = version.findVariantByNameBase(variantName, language);
        if (variant == null) {
            // if not found by stem check full name
            variant = version.findVariantByFullName(variantName, language);
        }
        return variant;
    }

    static List<CodeDto> findClassificationCodes(ClassificationSeries classification, DateRange dateRange,
                                                 Language language, Boolean includeFuture) {
        List<CodeDto> codes = new ArrayList<>();
        for (ClassificationVersion version : classification.getClassificationVersions()) {
            if (version.isDraft()) {
                continue;
            }
            if (version.getDateRange().overlaps(dateRange) && version.showVersion(includeFuture)) {
                if (!version.isPublished(language)) {
                    throw new KlassResourceNotFoundException("ClassificationVersion: " + version
                            .getDateRange() + ". Is not published in language: " + language);
                }
                codes.addAll(mapClassificationItemsToCodes(version, version.getDateRange(), language));
            }
        }

        return codes;
    }

    static List<CorrespondenceDto> findCorrespondences(ClassificationSeries sourceClassification,
                                                       ClassificationSeries targetClassification,
                                                       DateRange dateRange, Language language,
                                                       Boolean includeFuture, Boolean inverted) {
        List<CorrespondenceDto> correspondences = new ArrayList<>();
        for (ClassificationVersion version : sourceClassification.getClassificationVersions()) {
            if (version.isDraft()) {
                continue;
            }
            if (version.getDateRange().overlaps(dateRange) && version.showVersion(includeFuture)) {
                List<CorrespondenceTable> tables = getCorrespondenceTablesWithTarget(version, targetClassification,
                        version.getDateRange(), language);
                for (CorrespondenceTable correspondenceTable : tables) {
                    correspondences.addAll(mapCorrespondenceMapsToCorrespondences(correspondenceTable,
                            version.getDateRange().subRange(correspondenceTable.getTarget().getDateRange()),
                            language, inverted));
                }
            }
        }

        return correspondences;
    }

    private static List<CorrespondenceTable> getCorrespondenceTablesWithTarget(ClassificationVersion version,
            ClassificationSeries targetClassification, DateRange sourceDateRange, Language language) {
        List<CorrespondenceTable> correspondenceTables = new ArrayList<>();
        for (CorrespondenceTable correspondenceTable : version.getCorrespondenceTablesWithTarget(
                targetClassification)) {
            if (correspondenceTable.isDraft()) {
                continue;
            }
            if (!correspondenceTable.isPublished(language)) {
                throw new KlassResourceNotFoundException("CorrespondenceTable: " + correspondenceTable.getName(
                        language) + " " + correspondenceTable.getDateRange() + ". Is not published in language: "
                        + language);
            }
            if (correspondenceTable.getTarget().getDateRange().overlaps(sourceDateRange)) {
                correspondenceTables.add(correspondenceTable);
            }
        }
//        if (correspondenceTables.isEmpty()) {
//            throw new KlassResourceNotFoundException(createCorrespondenceNotFoundErrorMessage(
//                    version, targetClassification));
//        }
        return correspondenceTables;
    }

    public static String createCorrespondenceNotFoundErrorMessage(ClassificationVersion version,
            ClassificationSeries targetClassification) {
        return "Classification Version: '" + version.getName(Language.getDefault())
                + "' has no correspondence table with Classification: '" + targetClassification.getName(Language
                        .getDefault()) + "'";
    }

    private static List<CorrespondenceDto> mapCorrespondenceMapsToCorrespondences(
            CorrespondenceTable correspondenceTable, DateRange subRange, Language language, Boolean inverted) {
        List<CorrespondenceDto> correspondences = new ArrayList<>();
        for (CorrespondenceMap correspondenceMap : correspondenceTable.getCorrespondenceMaps()) {
            ClassificationItem source = correspondenceMap.getSource().orElse(null);
            ClassificationItem target = correspondenceMap.getTarget().orElse(null);
            correspondences.add(new CorrespondenceDto(inverted ? target : source, inverted ? source : target, subRange, language));
        }
        return correspondences;
    }

    private static List<CodeDto> mapClassificationItemsToCodes(StatisticalClassification variant,
                                                               DateRange dateRange, Language language) {
        List<CodeDto> codes = new ArrayList<>();
        for (Level level : variant.getLevels()) {
            codes.addAll(toCodes(level.getClassificationItems(), level, dateRange, language));
        }
        return codes;
    }

    private static List<CodeDto> toCodes(List<ClassificationItem> classificationItems, Level level, DateRange dateRange,
                                         Language language) {
        return classificationItems.stream().map(item -> new CodeDto(level, item, dateRange, language)).collect(toList());
    }

}
