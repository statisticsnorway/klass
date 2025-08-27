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
        return buildClassificationSummariesInternal(section, classificationType, true);
    }

    /**
     * Builds a list of {@link ClassificationFamilySummary} objects from a list of
     * {@link ClassificationFamily} entities, filtered by section and classification type if provided.
     *
     * <p>Each summary includes the family's ID, translatable name, icon path, and
     * the count of classification series that match the given section and classification type.</p>
     *
     * <p>If {@code section} and/or {@code classificationType} is {@code null}, that filter is ignored.</p>
     *
     * @param section the section to filter classification series by; if {@code null}, no section filtering is applied
     * @param classificationType the classification type to filter classification series by; if {@code null}, no type filtering is applied
     * @return a list of {@link ClassificationFamilySummary} representing summaries of the filtered families
     */
    public List<ClassificationFamilySummary> buildClassificationSummaries(String section, ClassificationType classificationType) {
        return buildClassificationSummariesInternal(section, classificationType, false);
    }

    private List<ClassificationFamilySummary> buildClassificationSummariesInternal(String section, ClassificationType classificationType, boolean publicOnly) {
        List<ClassificationFamily> families = classificationFamilyRepository.findAll();
        return families.stream()
                .filter(family -> hasMatchingSeries(family, section, classificationType))
                .map(family -> toClassificationFamilySummary(family, section, classificationType, publicOnly))
                .collect(Collectors.toList());
    }

    private boolean hasMatchingSeries(ClassificationFamily family, String section, ClassificationType classificationType) {
        return section == null && classificationType == null
                || family.getClassificationSeries().stream()
                .anyMatch(series ->
                        series.getContactPerson() != null &&
                                (section == null || section.startsWith(series.getContactPerson().getSection())) &&
                                (classificationType == null || classificationType.equals(series.getClassificationType()))
                );
    }

    private ClassificationFamilySummary toClassificationFamilySummary(ClassificationFamily family, String section, ClassificationType classificationType, boolean publicOnly) {
        long validSeriesCount = family.getClassificationSeriesBySectionAndClassificationType(section, classificationType, publicOnly).size();
        if (publicOnly) {
            logger.trace("validSeriesCount: {}", validSeriesCount);
        }
        return new ClassificationFamilySummary(
                family.getId(),
                family.getTranslatableName(),
                family.getIconPath(),
                validSeriesCount
        );
    }
}
