package no.ssb.klass.core.repository;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestDataProvider;
import no.ssb.klass.testutil.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * @author Mads Lundemo, SSB.
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest()
@ActiveProfiles({ConfigurationProfiles.POSTGRES_EMBEDDED, ConfigurationProfiles.MOCK_MAILSERVER})
@Transactional
// This test exposes a bug. The bug is fixed in klass-shared v2.x versions since this functionality is
// only used in Klass forvaltning which uses klass-shared v2.x
@Disabled
public class UserRepositoryTest {

    @Autowired
    private UserRepository testSubject;
    @Autowired
    private ClassificationSeriesRepository seriesRepository;
    @Autowired
    private ClassificationFamilyRepository familyRepository;

    @BeforeEach
    public void init() {
        User owner = new User("owner", "user with classification", "section");
        User nonOwner = new User("non-owner", "user without classification", "section");
        User deletedSeriesOwner = new User("deleted-owner", "user with delete classification", "section");
        testSubject.save(owner);
        testSubject.save(nonOwner);
        testSubject.save(deletedSeriesOwner);
        testSubject.flush();

        ClassificationFamily befolkning = familyRepository.save(TestUtil.createClassificationFamily("Befolkning"));
        ClassificationSeries series = TestDataProvider.createFamiliegrupperingCodelist(owner);
        series.setClassificationFamily(befolkning);
        seriesRepository.save(series);

        ClassificationFamily by = familyRepository.save(TestUtil.createClassificationFamily("by"));
        ClassificationSeries deleted = TestDataProvider.createClassificationBydelsinndeling();
        deleted.setClassificationFamily(by);
        deleted.setContactPerson(deletedSeriesOwner);
        deleted.setDeleted();
        seriesRepository.save(deleted);
        seriesRepository.flush();
    }

    @Test
    public void getUserIdsWithClassifications() {
        Set<BigInteger> userIds = testSubject.getUserIdsForUsersWithClassifications();
        assertThat(userIds.size(), is(1));
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {User.class})
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {
    }
}
