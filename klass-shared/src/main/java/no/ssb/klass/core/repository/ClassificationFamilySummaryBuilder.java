package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
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
     * @param families the list of classification families to summarize
     * @return a list of {@link ClassificationFamilySummary} representing summaries of the input families
     */
    public List<ClassificationFamilySummary> buildClassificationSummaries(List<ClassificationFamily> families) {
        return families.stream()
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
        return new ClassificationFamilySummary(
                family.getId(),
                family.getTranslatableName(),
                family.getIconPath(),
                validSeriesCount
        );
    }

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
    public List<ClassificationFamily> getPublicClassificationFamiliesFilterCopyrighted(){
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();

        for (ClassificationFamily family : families) {
            logger.info("Total classification series in family {}: {}", family.getName(), family.getClassificationSeries().size());
            long count = family.getClassificationSeries().stream()
                    .filter(series -> !series.isCopyrighted())
                    .count();
            logger.info("Total non-copyrighted classification series in family {}: {}", family.getName(), count);
        }

        return families;
    }

    public List<ClassificationFamily> getPublicClassificationFamiliesFilterClassificationDeleted(){
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();

        for (ClassificationFamily family : families) {
            logger.info("Total classification series in family {}: {}", family.getName(), family.getClassificationSeries().size());
            long count = family.getClassificationSeries().stream()
                    .filter(series -> !series.isDeleted())
                    .count();
            logger.info("Total non-deleted classification series in family {}: {}", family.getName(), count);
        }

        return families;
    }

    public List<ClassificationFamily> getPublicClassificationFamiliesFilterVersionDeleted(){
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();

        for (ClassificationFamily family : families) {
            logger.info("Total classification series in family '{}': {}", family.getName(), family.getClassificationSeries().size());

            for (ClassificationSeries series : family.getClassificationSeries()) {
                List<ClassificationVersion> filteredVersions = series.getClassificationVersions().stream()
                        .filter(version -> !version.isDeleted())
                        .collect(Collectors.toList());

                logger.info("Non-deleted versions in series '{}': {}", series.getId(), filteredVersions.size());


                logger.info("Non-deleted versions in series '{}': {}", series.getId(), filteredVersions);

            }
        }

        return families;
    }

    public List<ClassificationFamily> getPublicClassificationFamiliesFilterVersionNotPublished(){
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();

        for (ClassificationFamily family : families) {
            logger.info("Total classification series in family '{}': {}", family.getName(), family.getClassificationSeries().size());

            for (ClassificationSeries series : family.getClassificationSeries()) {
                logger.info("Versions in series '{}': {}", series.getId(), series.getClassificationVersions().size());
                List<ClassificationVersion> filteredVersions = series.getClassificationVersions().stream()
                        .filter(version -> !version.isPublishedInAnyLanguage())
                        .toList();

                logger.info("Published versions in series '{}': {}", series.getId(), filteredVersions.size());

            }
        }

        return families;
    }

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
