package no.ssb.klass.core.repository;

import static org.junit.Assert.*;

import java.util.List;

import javax.transaction.Transactional;

import com.google.common.collect.Lists;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class CorrespondenceTableRepositoryTest {
    @Autowired
    private CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired
    private ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired
    private UserRepository userRepository;
    private User user;
    private ClassificationFamily classificationFamily;

    @Before
    public void setup() {
        user = userRepository.save(TestUtil.createUser());
        classificationFamily = classificationFamilyRepository.save(TestUtil.createClassificationFamily("family"));
    }

    @Test
    public void findBySource() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");

        // when
        List<CorrespondenceTable> result = correspondenceTableRepository.findBySource(correspondenceTable.getSource());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findBySourceNotFound() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");
        ClassificationVersion notSourceVersion = correspondenceTable.getTarget();

        // when
        List<CorrespondenceTable> result = correspondenceTableRepository.findBySource(notSourceVersion);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findByTarget() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");

        // when
        List<CorrespondenceTable> result = correspondenceTableRepository.findByTarget(correspondenceTable.getTarget());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findBySourceInAndTargetIn() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");
        List<ClassificationVersion> versions = Lists.newArrayList(correspondenceTable.getSource(), correspondenceTable
                .getTarget());

        // when
        List<CorrespondenceTable> result = correspondenceTableRepository.findBySourceInAndTargetIn(versions, versions);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findBySourceInAndTargetInNotFound() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");
        List<ClassificationVersion> versions = Lists.newArrayList(correspondenceTable.getSource());

        // when
        List<CorrespondenceTable> result = correspondenceTableRepository.findBySourceInAndTargetIn(versions, versions);

        // then
        assertEquals(0, result.size());
    }

    private CorrespondenceTable createAndSaveCorrespondenceTable(String name) {
        ClassificationVersion source = createAndSaveClassificationWithVersion(name + 1);
        ClassificationVersion target = createAndSaveClassificationWithVersion(name + 2);
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(source, target);
        return correspondenceTableRepository.save(correspondenceTable);
    }

    private ClassificationVersion createAndSaveClassificationWithVersion(String classificationName) {
        ClassificationSeries classification = TestUtil.createClassification(classificationName);
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        classification.addClassificationVersion(version);
        classification.setContactPerson(user);
        classificationFamily.addClassificationSeries(classification);
        classificationSeriesRepository.save(classification);
        return version;
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { CorrespondenceTable.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {
    }
}
