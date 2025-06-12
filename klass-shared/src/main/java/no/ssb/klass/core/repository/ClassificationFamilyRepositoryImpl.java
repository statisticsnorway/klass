package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ClassificationFamilyRepositoryImpl implements ClassificationFamilyRepositoryCustom {

    @PersistenceContext
    private EntityManager em;

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
}
