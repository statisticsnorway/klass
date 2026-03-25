package no.ssb.klass.core.repository;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Lists;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import jakarta.transaction.Transactional;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.*;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@ActiveProfiles({
    ConfigurationProfiles.POSTGRES_EMBEDDED,
    ConfigurationProfiles.MOCK_MAILSERVER,
    ConfigurationProfiles.MOCK_SEARCH
})
@Transactional
@AutoConfigureEmbeddedDatabase(
        provider = EMBEDDED,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
public class CorrespondenceTableRepositoryTest {
    @Autowired private CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired private ClassificationSeriesRepository classificationSeriesRepository;
    @Autowired private ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired private UserRepository userRepository;
    private User user;
    private ClassificationFamily classificationFamily;

    @BeforeEach
    public void setup() {
        user = userRepository.save(TestUtil.createUser());
        classificationFamily =
                classificationFamilyRepository.save(TestUtil.createClassificationFamily("family"));
    }

    @Test
    public void findBySource() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");

        // when
        List<CorrespondenceTable> result =
                correspondenceTableRepository.findBySource(correspondenceTable.getSource());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findBySourceNotFound() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");
        ClassificationVersion notSourceVersion = correspondenceTable.getTarget();

        // when
        List<CorrespondenceTable> result =
                correspondenceTableRepository.findBySource(notSourceVersion);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findByTarget() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");

        // when
        List<CorrespondenceTable> result =
                correspondenceTableRepository.findByTarget(correspondenceTable.getTarget());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findBySourceInAndTargetIn() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");
        List<ClassificationVersion> versions =
                Lists.newArrayList(
                        correspondenceTable.getSource(), correspondenceTable.getTarget());

        // when
        List<CorrespondenceTable> result =
                correspondenceTableRepository.findBySourceInAndTargetIn(versions, versions);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findBySourceInAndTargetInNotFound() {
        // given
        CorrespondenceTable correspondenceTable = createAndSaveCorrespondenceTable("name");
        List<ClassificationVersion> versions = Lists.newArrayList(correspondenceTable.getSource());

        // when
        List<CorrespondenceTable> result =
                correspondenceTableRepository.findBySourceInAndTargetIn(versions, versions);

        // then
        assertEquals(0, result.size());
    }

    private CorrespondenceTable createAndSaveCorrespondenceTable(String name) {
        ClassificationVersion source = createAndSaveClassificationWithVersion(name + 1);
        ClassificationVersion target = createAndSaveClassificationWithVersion(name + 2);
        CorrespondenceTable correspondenceTable =
                TestUtil.createCorrespondenceTable(source, target);
        return correspondenceTableRepository.save(correspondenceTable);
    }

    private ClassificationVersion createAndSaveClassificationWithVersion(
            String classificationName) {
        ClassificationSeries classification = TestUtil.createClassification(classificationName);
        ClassificationVersion version =
                TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        classification.addClassificationVersion(version);
        classification.setContactPerson(user);
        classificationFamily.addClassificationSeries(classification);
        classificationSeriesRepository.save(classification);
        return version;
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = {CorrespondenceTable.class})
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {}
}
