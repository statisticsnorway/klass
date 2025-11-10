package no.ssb.klass.api.applicationtest.utils;

import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.opensearch.data.client.orhlc.OpenSearchRestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * @author Mads Lundemo, SSB.
 */
@Service
public class ApplicationTestUtil {

  @Autowired private EntityManager entityManager;

  @Autowired(required = false)
  private OpenSearchRestTemplate openSearchRestTemplate;

  @Value("${klass.env.search.elasticsearch.index:klass}")
  private String indexName;

  @Transactional
  public void clearDatabase() {
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

  public void clearSearch() {
    if (openSearchRestTemplate != null) {
      try {
        openSearchRestTemplate
            .indexOps(
                org.springframework.data.elasticsearch.core.mapping.IndexCoordinates.of(indexName))
            .delete();
        openSearchRestTemplate
            .indexOps(
                org.springframework.data.elasticsearch.core.mapping.IndexCoordinates.of(indexName))
            .create();
      } catch (Exception e) {
        // Ignore if mock or not configured
      }
    }
  }
}
