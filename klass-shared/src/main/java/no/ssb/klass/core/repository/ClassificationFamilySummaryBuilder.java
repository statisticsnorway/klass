package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ClassificationFamilySummaryBuilder {
    private static final Logger logger = LoggerFactory.getLogger(ClassificationFamilySummaryBuilder.class);

    private final ClassificationFamilyRepository classificationFamilyRepository;

    public ClassificationFamilySummaryBuilder(ClassificationFamilyRepository classificationFamilyRepository) {
        this.classificationFamilyRepository = classificationFamilyRepository;
    }

    /**
     * Builds a list of {@link ClassificationFamilySummary} objects from a list of
     * {@link ClassificationFamily} entities, filtered by section and classification type if provided.
     *
     * <p>Each summary includes the family's ID, translatable name, icon path, and
     * the count of public classification series that match the given section and classification type.</p>
     *
     * <p>If {@code section} and/or {@code classificationType} is {@code null}, that filter is ignored.</p>
     *
     * @param section the section to filter classification series by; if {@code null}, no section filtering is applied
     * @param classificationType the classification type to filter classification series by; if {@code null}, no type filtering is applied
     * @return a list of {@link ClassificationFamilySummary} representing summaries of the filtered families
     */
    public List<ClassificationFamilySummary> buildPublicClassificationSummaries(String section, ClassificationType classificationType) {
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();
        return families.stream()
                .filter(family -> (section == null && classificationType == null)
                        || family.getClassificationSeries().stream()
                        .anyMatch(series ->
                                series.getContactPerson() != null &&
                                        (section == null || section.equals(series.getContactPerson().getSection())) &&
                                        (classificationType == null || classificationType.equals(series.getClassificationType()))
                        ))
                .map(family -> toPublicClassificationFamilySummary(family, section, classificationType))
                .collect(Collectors.toList());
    }

    // .map(this::toPublicClassificationFamilySummary)
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
    private ClassificationFamilySummary toPublicClassificationFamilySummary(ClassificationFamily family, String section, ClassificationType classificationType) {
        long validSeriesCount = family.getClassificationSeriesBySectionAndClassificationType(section, classificationType).size();
        //long validSeriesCount = family.getPublicClassificationSeries().size();
        logger.trace("validSeriesCount: {}", validSeriesCount);
        return new ClassificationFamilySummary(
                family.getId(),
                family.getTranslatableName(),
                family.getIconPath(),
                validSeriesCount
        );
    }

    // getClassificationSeriesBySectionAndClassificationType
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
}
