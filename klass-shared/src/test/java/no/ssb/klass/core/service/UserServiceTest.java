package no.ssb.klass.core.service;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilyRepository;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestDataProvider;
import no.ssb.klass.testutil.TestUtil;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles({ConfigurationProfiles.POSTGRES_EMBEDDED, ConfigurationProfiles.MOCK_MAILSERVER})
@Transactional
public class UserServiceTest {

    // These mocks are necessary to inject a ClassificationService bean for UserServiceImpl
    @MockBean
    private SolrTemplate solrTemplate;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ClassificationSeriesRepository seriesRepository;
    @Autowired
    private ClassificationFamilyRepository familyRepository;

    @Before
    public void init() {
        User owner = new User("owner", "user with classification", "section");
        User nonOwner = new User("non-owner", "user without classification", "section");
        User deletedSeriesOwner = new User("deleted-owner", "user with delete classification", "section");
        userRepository.save(owner);
        userRepository.save(nonOwner);
        userRepository.save(deletedSeriesOwner);
        userRepository.flush();

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
        List<User> users = userService.getUsersWithClassifications();
        assertThat(users.size(), is(1));
        assertThat(users.get(0).getUsername(), is("owner"));
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {User.class, ClassificationFamily.class, ClassificationSeries.class})
    @ComponentScan(basePackageClasses = {UserService.class, ClassificationFamilyRepository.class, ClassificationSeriesRepository.class, UserRepository.class, TranslatablePersistenceConverter.class})
    static class Config {
    }
}
