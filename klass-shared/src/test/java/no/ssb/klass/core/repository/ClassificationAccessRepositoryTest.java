package no.ssb.klass.core.repository;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationAccessCounter;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.IncrementableClockSource;
import no.ssb.klass.testutil.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class ClassificationAccessRepositoryTest {

    private IncrementableClockSource clockSource;
    private User user;

    @Autowired
    private ClassificationAccessRepository classificationAccessRepository;

    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;

    @Autowired
    private ClassificationFamilyRepository classificationFamilyRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EntityManager entityManager;

    @Before
    public void setup() {
        clockSource = new IncrementableClockSource(new Date().getTime());
        TimeUtil.setClockSource(clockSource);
        user = userRepository.save(TestUtil.createUser());
    }

    @Test
    public void getAccessSumTest() {
        createClassificationSeries("Test1");
        createClassificationSeries("Test2");
        int i = classificationAccessRepository.getAccessSum(SearchWordsRepositoryTest.getFromDate(),
                SearchWordsRepositoryTest.getToDate());
        assertEquals(4, i);
    }

    @Test
    public void getClassificationsCountTest() {
        createClassificationSeries("Test1");
        createClassificationSeries("Test2");
        Page<StatisticalEntity> getClassificationsCount = classificationAccessRepository.getClassificationsCount(
                SearchWordsRepositoryTest.getFromDate(), SearchWordsRepositoryTest.getToDate(), new PageRequest(0,
                        100));
        assertEquals(2, getClassificationsCount.getTotalElements());
        List<StatisticalEntity> resultList = getClassificationsCount.getContent();
        assertEquals("Test1", resultList.get(0).getName());
        assertEquals(Long.valueOf(2), resultList.get(0).getCount());
        assertEquals("Test2", resultList.get(1).getName());
        assertEquals(Long.valueOf(2), resultList.get(1).getCount());
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { ClassificationAccessCounter.class, ClassificationSeries.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {
    }

    private void createClassificationSeries(String name) {
        ClassificationSeries classification = ClassificationSeriesRepositoryTest.createClassificationSeriesWithVersion(
                user, name);
        classificationFamilyRepository.save(TestUtil.createClassificationFamily(name)).addClassificationSeries(
                classification);
        classificationSeriesRepository.save(classification);
        classificationSeriesRepository.flush();
        entityManager.detach(classification);
        ClassificationAccessCounter classificationAccessCounter1 = new ClassificationAccessCounter(classification);
        ClassificationAccessCounter classificationAccessCounter2 = new ClassificationAccessCounter(classification);

        classificationAccessRepository.save(classificationAccessCounter1);
        classificationAccessRepository.save(classificationAccessCounter2);
        classificationAccessRepository.flush();
    }
}
