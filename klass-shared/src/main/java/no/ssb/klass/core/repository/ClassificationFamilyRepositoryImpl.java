package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.*;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@Transactional(readOnly = true)
public class ClassificationFamilyRepositoryImpl implements ClassificationFamilyRepositoryCustom{
    @PersistenceContext
    private EntityManager em;

    @Override
    public List<ClassificationFamilySummary> findClassificationFamilySummaries(
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



    @Override
    public List<ClassificationFamilySummary> findPublicClassificationFamilySummaries(
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
}
