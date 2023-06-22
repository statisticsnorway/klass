package no.ssb.klass.core.repository;

import static org.junit.Assert.*;

import java.util.List;

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
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class StatisticalUnitRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClassificationFamilyRepository classificationFamilyRepository;

    @Autowired
    private StatisticalUnitRepository statisticalUnitRepository;

    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;

    private User user;

    @Before
    public void setup() {
        user = userRepository.save(TestUtil.createUser());
    }

    @Test
    public void getStaticalUnitsOverViewTest() {
        makeTestData();
        Page<StatisticalEntity> result = statisticalUnitRepository.getStaticalUnitsOverView(new PageRequest(0,
                Integer.MAX_VALUE));
        assertEquals(2, result.getContent().size());
        List<StatisticalEntity> resultList = result.getContent();
        assertEquals("Duppeditt", resultList.get(0).getName());
        assertEquals(Long.valueOf(2), resultList.get(0).getCount());
        assertEquals("Dingseboms", resultList.get(1).getName());
        assertEquals(Long.valueOf(1), resultList.get(1).getCount());
    }

    @Test
    public void getAllClassificationSeriesForStaticalUnitTest() {
        StatisticalUnit statisticalUnit = makeTestData();
        List<ClassificationReportDto> result = statisticalUnitRepository.getAllClassificationSeriesForStaticalUnit(
                statisticalUnit);
        assertEquals(2, result.size());
        assertEquals("Test1", result.get(0).getName());
        assertEquals("Test2", result.get(1).getName());
    }

    private StatisticalUnit makeTestData() {
        ClassificationSeries classificationSeries1 = ClassificationSeriesRepositoryTest
                .createClassificationSeriesWithVersion(user, "Test1");
        StatisticalUnit statisticalUnit1 = makeStatisticalUnit("Duppeditt");
        StatisticalUnit statisticalUnit2 = makeStatisticalUnit("Dingseboms");
        classificationSeries1.getStatisticalUnits().add(statisticalUnit1);
        classificationSeries1.getStatisticalUnits().add(statisticalUnit2);
        classificationFamilyRepository.save(TestUtil.createClassificationFamily("name1")).addClassificationSeries(
                classificationSeries1);

        ClassificationSeries classificationSeries2 = ClassificationSeriesRepositoryTest
                .createClassificationSeriesWithVersion(user, "Test2");
        classificationSeries2.getStatisticalUnits().add(statisticalUnit1);
        classificationFamilyRepository.save(TestUtil.createClassificationFamily("name2")).addClassificationSeries(
                classificationSeries2);

        classificationFamilyRepository.flush();
        classificationSeriesRepository.save(classificationSeries1);
        classificationSeriesRepository.save(classificationSeries2);
        classificationSeriesRepository.flush();
        return statisticalUnit1;
    }

    private StatisticalUnit makeStatisticalUnit(String name) {
        Translatable statisticalUnitName = new Translatable(name, null, null);
        StatisticalUnit statisticalUnit = new StatisticalUnit(statisticalUnitName);
        statisticalUnitRepository.save(statisticalUnit);
        statisticalUnitRepository.flush();
        return statisticalUnit;
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { StatisticalUnit.class, ClassificationSeries.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {
    }
}
