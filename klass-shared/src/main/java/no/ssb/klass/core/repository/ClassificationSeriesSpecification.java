package no.ssb.klass.core.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;

/**
 * Specifies what classifications to find.
 * <p>
 * Note: deleted and copyrighted classifications are not included in result.
 *
 */
public class ClassificationSeriesSpecification implements Specification<ClassificationSeries> {
    private final boolean includeCodelists;
    private final boolean onlyPublic;
    private final Date changedSince;

    public ClassificationSeriesSpecification(boolean includeCodelists, Date changedSince) {
        this(includeCodelists, changedSince, false);
    }

    public ClassificationSeriesSpecification(boolean includeCodelists, Date changedSince, boolean onlyPublic) {
        this.includeCodelists = includeCodelists;
        this.changedSince = changedSince;
        this.onlyPublic = onlyPublic;
    }

    @Override
    public Predicate toPredicate(Root<ClassificationSeries> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get("copyrighted"), false));
        predicates.add(cb.equal(root.get("deleted"), false));

        if (onlyPublic) {
            Path<Object> versionList = root.joinList("classificationVersions", JoinType.INNER).get("published");
            Predicate published = cb.or(cb.equal(versionList.get("published_no"), true),
                    cb.equal(versionList.get("published_nn"), true),
                    cb.equal(versionList.get("published_en"), true));
            predicates.add(published);
            query.distinct(true);
            query.orderBy(cb.asc(root.get("id")));
        }
        if (!includeCodelists) {
            predicates.add(cb.equal(root.get("classificationType"), ClassificationType.CLASSIFICATION));
        }
        if (changedSince != null) {
            predicates.add(cb.greaterThan(root.<Date> get("lastModified"), changedSince));
        }

        return andTogether(predicates, cb);
    }

    private Predicate andTogether(List<Predicate> predicates, CriteriaBuilder cb) {
        return cb.and(predicates.toArray(new Predicate[0]));
    }
}
