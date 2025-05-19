package no.ssb.klass.api.migration.dataintegrity;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static no.ssb.klass.api.migration.MigrationTestConstants.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class KlassApiClassificationsTest extends AbstractKlassApiDataIntegrityTest {

    @Test
    void getClassificationsResultIsEqualSize() {
        assumeTrue(responseKlassApiSourceHost.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiTargetHost.getStatusCode() == 200, TARGET_API_CHECK);

        int klassApiSourceHostTotalElements = responseKlassApiSourceHost.path(TOTAL_ELEMENTS);
        int klassApiTargetHostTotalElements = responseKlassApiTargetHost.path(TOTAL_ELEMENTS);

        assertThat(klassApiSourceHostTotalElements).isEqualTo(klassApiTargetHostTotalElements);
        assertThat(klassApiSourceHostTotalElements).isEqualTo(classificationsIdsSourceHost.size());
    }

    @Test
    void getClassificationsPageSizeIsEqual(){
        assumeTrue(responseKlassApiSourceHost.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiTargetHost.getStatusCode() == 200, TARGET_API_CHECK);

        assertThat(sourceHostClassificationsPage.size()).isEqualTo(targetHostClassificationsPage.size());
        String classificationsPageSourceHost = responseKlassApiSourceHost.path(EMBEDDED_PAGE);
        String classificationsPageTargetHost = responseKlassApiTargetHost.path(EMBEDDED_PAGE);
        assertThat(classificationsPageSourceHost).isEqualTo(classificationsPageTargetHost);
    }

    @Test
    void getClassificationsItems(){
        assumeTrue(responseKlassApiSourceHost.getStatusCode() == 200, SOURCE_API_CHECK);
        assumeTrue(responseKlassApiTargetHost.getStatusCode() == 200, TARGET_API_CHECK);

        for (int i = 0; i < sourceHostClassificationsPage.size(); i++) {
            assertThat(
                    sourceHostClassificationsPage.get(i).get(NAME)).isEqualTo(
                            targetHostClassificationsPage.get(i).get(NAME));
            assertThat(
                    sourceHostClassificationsPage.get(i).get(ID)).isEqualTo(
                            targetHostClassificationsPage.get(i).get(ID));
            assertThat(
                    sourceHostClassificationsPage.get(i).get(CLASSIFICATION_TYPE)).isEqualTo(
                            targetHostClassificationsPage.get(i).get(CLASSIFICATION_TYPE));

            Map<String, Object> classification = sourceHostClassificationsPage.get(i);

            assertThat(classification.get(LINKS)).isNotEqualTo(
                    targetHostClassificationsPage.get(i).get(LINKS));
        }

    }

}
