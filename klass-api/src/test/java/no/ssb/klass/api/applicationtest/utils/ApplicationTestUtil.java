package no.ssb.klass.api.applicationtest.utils;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
public class ApplicationTestUtil {

    @Autowired private EntityManager entityManager;

    @Value("${klass.env.search.elasticsearch.index:klass}")
    private String indexName;

    @Transactional
    public void clearDatabase() {
        entityManager.createQuery("delete from Subscription").executeUpdate();
        entityManager.createQuery("delete from Subscriber").executeUpdate();
        entityManager.createQuery("delete from Changelog").executeUpdate();
        entityManager.createQuery("delete from CorrespondenceMap").executeUpdate();
        entityManager.createQuery("delete from ClassificationItem").executeUpdate();
        entityManager.createQuery("delete from Level").executeUpdate();
        entityManager.createQuery("delete from ClassificationVariant").executeUpdate();
        entityManager.createQuery("delete from CorrespondenceTable").executeUpdate();
        entityManager.createQuery("delete from ClassificationVersion").executeUpdate();
        entityManager.createQuery("delete from ClassificationAccessCounter").executeUpdate();
        entityManager.createQuery("delete from ClassificationSeries").executeUpdate();
        entityManager.createQuery("delete from ClassificationFamily").executeUpdate();
        entityManager.createQuery("delete from User").executeUpdate();
        entityManager.createQuery("delete from StatisticalUnit").executeUpdate();
    }

}
