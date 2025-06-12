package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.*;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ClassificationFamilyRepositoryImpl implements ClassificationFamilyRepositoryCustom {

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private TranslatablePersistenceConverter converter;

    @Override
    public List<ClassificationFamilySummary> findClassificationFamilySummaries(
            @Param("section") String section,
            @Param("classificationType") ClassificationType classificationType
    ){
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<ClassificationFamilySummary> cq = cb.createQuery(ClassificationFamilySummary.class);

        Root<ClassificationFamily> family = cq.from(ClassificationFamily.class);

        Join<ClassificationFamily, ClassificationSeries> classification = family.join("classificationSeriesList", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isFalse(classification.get("deleted")));

        if (classificationType != null) {
            predicates.add(cb.equal(classification.get("classificationType"), classificationType));
        }

        if (section != null) {
            Subquery<String> sectionSubquery = cq.subquery(String.class);
            Root<User> user = sectionSubquery.from(User.class);
            sectionSubquery.select(user.get("section"))
                    .where(cb.equal(user.get("id"), classification.get("contactPerson").get("id")));
            predicates.add(cb.equal(sectionSubquery, section));
        }

        classification.on(predicates.toArray(new Predicate[0]));

        cq.multiselect(
                        family.get("id"),
                        family.get("name"),
                        family.get("iconName"),
                        cb.countDistinct(classification.get("id"))
                )
                .groupBy(family.get("id"), family.get("name"), family.get("iconName"));

        return em.createQuery(cq).getResultList();
    }

    @Override
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries(
            String section, ClassificationType classificationType
    ){
        CriteriaBuilder cb = em.getCriteriaBuilder();


        CriteriaQuery<ClassificationFamilySummary> cq = cb.createQuery(ClassificationFamilySummary.class);

        Root<ClassificationFamily> family = cq.from(ClassificationFamily.class);

        Join<ClassificationFamily, ClassificationSeries> classification = family.join("classificationSeriesList", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.isFalse(classification.get("deleted")));
        predicates.add(cb.isFalse(classification.get("copyrighted")));

        if (classificationType != null) {
            predicates.add(cb.equal(classification.get("classificationType"), classificationType));
        }

        if (section != null) {
            Subquery<String> sectionSubquery = cq.subquery(String.class);
            Root<User> user = sectionSubquery.from(User.class);
            sectionSubquery.select(user.get("section"))
                    .where(cb.equal(user.get("id"), classification.get("contactPerson").get("id")));
            predicates.add(cb.equal(sectionSubquery, section));
        }

        classification.on(predicates.toArray(new Predicate[0]));

        Join<ClassificationSeries, ClassificationVersion> version =
                classification.join("classificationVersions", JoinType.LEFT);

        version.on(
                cb.and(
                        cb.isFalse(version.get("deleted")),
                        cb.isNotNull(version.get("published")),
                        cb.or(
                                cb.isTrue(version.get("published").get("published_no")),
                                cb.isTrue(version.get("published").get("published_nn")),
                                cb.isTrue(version.get("published").get("published_en"))
                        )
                )
        );

        cq.multiselect(
                        family.get("id"),
                        family.get("name"),
                        family.get("iconName"),
                        cb.countDistinct(classification.get("id"))
                )
                .groupBy(family.get("id"), family.get("name"), family.get("iconName"));

        return em.createQuery(cq).getResultList();
    }

    //@Override
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries8(
            @Param("section") String section,
            @Param("classificationType") ClassificationType classificationType
    ) {
        List resultList = em.createQuery("select new no.ssb.klass.core.repository.ClassificationFamilySummary(" +
                        "family.id," +
                        " family.name, " +
                        "  family.iconName, " +
                        "  count(classification)) " +
                        "from ClassificationFamily family " +
                        "  left join family.classificationSeriesList classification " +
                            "with classification.deleted = false" +
                            " and classification.deleted = false" +
                        "  left join classification.classificationVersions version" +
                        "       with (version.published.published_no is true or version.published.published_nn is true or version.published.published_en is true)" +
                        "       and version.deleted = false " +
                        " where (:section is null or :section = (" +
                        "       select user.section from User user where user = classification.contactPerson)) " +
                        "   and (:classificationType is null or :classificationType = classification.classificationType) " +
                        "group by family.id, family.name, family.iconName"
                ).setParameter("classificationType", classificationType)
                .setParameter("section", section)
                .getResultList();
        return resultList;
    }

    //      " left join classification.classificationVersions version"
    //@Override
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries07(
            @Param("section") String section,
            @Param("classificationType") ClassificationType classificationType
    ){
        List resultList = em.createQuery("select new no.ssb.klass.core.repository.ClassificationFamilySummary(" +
                        "family.id," +
                        " family.name, " +
                        "  family.iconName, " +
                        "  count(classification)) " +
                        "from ClassificationFamily family " +
                        "left join family.classificationSeriesList classification " +
                        "  with classification.deleted = false " +
                        "where (:section is null or :section = (" +
                        "  select user.section from User user where user = classification.contactPerson)) " +
                        "and (:classificationType is null or :classificationType = classification.classificationType) " +
                        "group by family.id, family.name, family.iconName"
                ).setParameter("classificationType", classificationType)
                .setParameter("section", section)
                .getResultList();
        return resultList;
    }

    @SuppressWarnings("unchecked")
    //@Override
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries9(
            String section, ClassificationType classificationType) {
        List<ClassificationFamilySummary> result = new ArrayList<>();
        List<Object[]> rows = em.createNativeQuery(
                // @formatter:off
                        " select family.id, family.name, family.icon_name, count(DISTINCT classification.id)" +
                        " from classification_family family" +
                        " left join classification_series classification on classification.classification_family_id = family.id" +
                        " left join statistical_classification version on version.classification_id = classification.id" +
                        " left join \"user\" usr on usr.id = classification.contact_person_id" +
                        " where (version.published_en = true or version.published_no = true or version.published_nn = true)" +
                        "  and version.deleted = false" +
                        "  and classification.deleted = false" +
                        "  and classification.copyrighted = false" +
                        "  and (:classificationType is null or :classificationType = classification.classification_type)" +
                        "  and (:section is null or usr.section = :section)" +
                        "group by family.id, family.name, family.icon_name"
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
