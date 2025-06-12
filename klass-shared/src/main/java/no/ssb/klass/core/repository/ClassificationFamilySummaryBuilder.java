package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationFamily;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ClassificationFamilySummaryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ClassificationFamilySummaryBuilder.class);

    private final ClassificationFamilyRepository classificationFamilyRepository;

    public ClassificationFamilySummaryBuilder(ClassificationFamilyRepository classificationFamilyRepository) {
        this.classificationFamilyRepository = classificationFamilyRepository;
    }

    public List<ClassificationFamily> findPublicClassificationFamilies(){
        return classificationFamilyRepository.findAll();
    }

    /**
     * Builds a list of {@link ClassificationFamilySummary} objects from a list of
     * {@link ClassificationFamily} entities.
     *
     * <p>Each summary includes the family's ID, translatable name, icon path, and
     * count of public classification series.</p>
     *
     * @return a list of {@link ClassificationFamilySummary} representing summaries of the input families
     */
    public List<ClassificationFamilySummary> buildPublicClassificationSummaries(String section) {
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();
        return families.stream()
                .filter(family -> family.getClassificationSeries().stream()
                        .anyMatch(series -> series.getContactPerson() != null
                                && section.equals(series.getContactPerson().getSection())))
                .map(this::toPublicClassificationFamilySummary)
                .collect(Collectors.toList());
    }

    /**
     * Converts a {@link ClassificationFamily} into a {@link ClassificationFamilySummary}
     * including only public classification series.
     *
     * <p>The summary contains the family's ID, translatable name, icon path,
     * and the count of public classification series.</p>
     *
     * @param family the classification family to summarize
     * @return a {@link ClassificationFamilySummary} representing the public summary of the family
     */
    private ClassificationFamilySummary toPublicClassificationFamilySummary(ClassificationFamily family) {
        long validSeriesCount = family.getPublicClassificationSeries().size();
        logger.trace("validSeriesCount: {}", validSeriesCount);
        return new ClassificationFamilySummary(
                family.getId(),
                family.getTranslatableName(),
                family.getIconPath(),
                validSeriesCount
        );
    }

    /**
     * Builds a list of {@link ClassificationFamilySummary} objects from a list of
     * {@link ClassificationFamily} entities.
     *
     * <p>Each summary includes the family's ID, translatable name, icon path, and
     * count of classification series.</p>
     *
     * @param families the list of classification families to summarize
     * @return a list of {@link ClassificationFamilySummary} representing summaries of the input families
     */
    public List<ClassificationFamilySummary> buildClassificationSummaries(List<ClassificationFamily> families) {
        return families.stream()
                .map(this::toClassificationFamilySummary)
                .collect(Collectors.toList());
    }

    /**
     * Converts a {@link ClassificationFamily} into a {@link ClassificationFamilySummary}
     *
     * <p>The summary contains the family's ID, translatable name, icon path,
     * and the count of classification series.</p>
     *
     * @param family the classification family to summarize
     * @return a {@link ClassificationFamilySummary} representing the public summary of the family
     */
    private ClassificationFamilySummary toClassificationFamilySummary(ClassificationFamily family) {
        long validSeriesCount = family.getClassificationSeries().size();
        return new ClassificationFamilySummary(
                family.getId(),
                family.getTranslatableName(),
                family.getIconPath(),
                validSeriesCount
        );
    }

    // Temp methods for dev
    public Map<String, Long> countValidClassificationSeriesPerFamilyMethod() {
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();

        return families.stream()
                .collect(Collectors.toMap(
                        ClassificationFamily::getName,
                        family -> (long) family.getPublicClassificationSeries().size()
                ));
    }

    public Map<String, Long> countValidClassificationSeriesPerFamily() {
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();

        return families.stream()
                .collect(Collectors.toMap(
                        ClassificationFamily::getName,
                        family -> family.getClassificationSeries().stream()
                                .filter(series -> !series.isDeleted() && !series.isCopyrighted())
                                .filter(series -> series.getClassificationVersions().stream()
                                        .anyMatch(version -> !version.isDeleted() && version.isPublishedInAnyLanguage()))
                                .count()
                ));
    }
}
