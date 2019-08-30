package no.ssb.klass.core.repository;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;

import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;

/**
 * Note: this class is only needed due to bug in Hibernate, see https://hibernate.atlassian.net/browse/HHH-7321.
 * <p>
 * Because of this bug in hql, we are using native sql instead, and need to do the result mapping to
 * ClassificationFamilySummary manually.
 * <p>
 * If above bug is fixed then this class and the custom interface should be deleted and an hql query should be used
 * instead.
 * <p>
 * Note: Above mentioned bug is fixed in Hibernate and is included in version 5.1. However Spring Boot is not yet using
 * this version (currently 5.0 of Hibernate)
 * 
 * <pre>
 * A possible HQL that can be included in ClassificationFamilyRepository if bug is fixed, is shown below;
 *  
 * &#64;Query("select new no.ssb.klass.core.repository.ClassificationFamilySummary(family.name, family.iconName, count(classification)) "
 *         + "from ClassificationFamily family "
 *         + "left join family.classificationSeriesList as classification with classification.deleted <> '1' "
 *         + " and (:section is null or :section = (select user.section from User user where user = classification.contactPerson)) "
 *         + " and (:classificationType is null or :classificationType = classification.classificationType)"
 *         + "group by family")
 * List<ClassificationFamilySummary> findClassificationFamilySummaries(@Param("section") String section,
 *         &#64;Param("classificationType") ClassificationType classificationType);
 * </pre>
 */
class ClassificationFamilyRepositoryImpl implements ClassificationFamilyRepositoryCustom {
    @PersistenceContext
    private EntityManager em;
    @Autowired
    private TranslatablePersistenceConverter converter;

    @SuppressWarnings("unchecked")
    @Override
    public List<ClassificationFamilySummary> findClassificationFamilySummaries(String section,
            ClassificationType classificationType) {
        List<ClassificationFamilySummary> result = new ArrayList<>();
        List<Object[]> rows = em.createNativeQuery(
                // @formatter:off
                       "select family.id, family.name, family.icon_name, count(classification.id) from classification_family family "
                     + "left outer join classification_series classification on classification.classification_family_id=family.id "
                     + "  and classification.deleted <> '1' "
                     + "  and (:section is null or :section = (select user.section from user user where user.id = classification.contact_person_id)) "
                     + "  and (:classificationType is null or :classificationType = classification.classification_type) "
                     + "group by family.id"
                // @formatter:on
        ).setParameter("section", section).setParameter("classificationType", classificationType == null ? null
                : classificationType.name()).getResultList();

        for (Object[] columns : rows) {
            result.add(new ClassificationFamilySummary(((BigInteger) columns[0]).longValue(), converter
                    .convertToEntityAttribute((String) columns[1]), (String) columns[2],
                    ((BigInteger) columns[3]).longValue()));
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries(
            String section, ClassificationType classificationType) {
        List<ClassificationFamilySummary> result = new ArrayList<>();
        List<Object[]> rows = em.createNativeQuery(
    // @formatter:off
      "select family.id, family.name, family.icon_name, count(DISTINCT(classification.id)) from classification_family family "
      + "  left outer join classification_series classification on classification.classification_family_id=family.id "
      + "  left outer join statistical_classification version on version.classification_id=classification.id "
      + " where (version.published_en = '1' or version.published_no = '1' or version.published_nn = '1') "
      + "    and version.deleted <> '1' "
      + "    and classification.deleted <> '1' "
      + "    and classification.copyrighted <> '1' "
      + "    and (:classificationType is null or :classificationType = classification.classification_type) "
      + "    and (:section is null or :section "
      + "           = (select user.section from user user where user.id = classification.contact_person_id)) "
      + "  group by family.id"
    // @formatter:on
        ).setParameter("section", section).setParameter("classificationType", classificationType == null ? null
                : classificationType.name()).getResultList();

        for (Object[] columns : rows) {
            result.add(new ClassificationFamilySummary(((BigInteger) columns[0]).longValue(), converter
                    .convertToEntityAttribute((String) columns[1]), (String) columns[2],
                    ((BigInteger) columns[3]).longValue()));
        }
        return result;
    }
}
