package no.ssb.klass.core.repository;

import java.util.List;

import no.ssb.klass.core.model.ClassificationType;

interface ClassificationFamilyRepositoryCustom {
    /**
     * Find all classificationFamilySummaries
     * 
     * @param section
     *            null means all sections
     * @param classificationType
     *            null means all classificationTypes
     * @return classificationFamilySummaries
     */
    List<ClassificationFamilySummary> findClassificationFamilySummaries(String section,
            ClassificationType classificationType);

    List<ClassificationFamilySummary> findPublicClassificationFamilySummaries(
            String section, ClassificationType classificationType);
}
