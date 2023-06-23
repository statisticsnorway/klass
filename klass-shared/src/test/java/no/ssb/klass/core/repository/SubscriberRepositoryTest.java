package no.ssb.klass.core.repository;

import static org.junit.Assert.*;

import java.net.URL;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.Subscriber;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.testutil.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
@Transactional
public class SubscriberRepositoryTest {

    static final String EMAIL = "email@server.com";

    @Autowired
    private SubscriberRepository subject;

    @Autowired
    private ClassificationSeriesRepository classificationSeriesRepository;

    @Autowired
    private ClassificationFamilyRepository classificationFamilyRepository;

    @Autowired
    private UserRepository userRepository;

    private Subscriber subscriber;
    private Long subscriberId;

    @Before
    public void init() {
        subscriber = new Subscriber(EMAIL);
        subject.save(subscriber);
        subscriberId = subscriber.getId();
        subject.flush();
    }

    @Test
    public void testEntity() {
        // when
        Subscriber result = subject.findOne(subscriberId);
        // then
        assertEquals(subscriberId, result.getId());
    }

    @Test
    public void findVerifiedSubscribersOfClassification() throws Exception {
        // given
        ClassificationSeries classification = createAndSaveClassification();
        String token = subscriber.addSubscription(classification, new URL("http://test.url"));
        subscriber.verify(token);
        subject.save(subscriber);

        // when
        List<Subscriber> result = subject.findVerifiedSubscribersOfClassification(classification);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void excludeNotVerifiedSubscribersOfClassification() throws Exception {
        ClassificationSeries classification = createAndSaveClassification();
        subscriber.addSubscription(classification, new URL("http://test.url"));
        subject.save(subscriber);

        // when
        List<Subscriber> result = subject.findVerifiedSubscribersOfClassification(classification);

        // then
        assertEquals(0, result.size());
    }

    private ClassificationSeries createAndSaveClassification() {
        ClassificationSeries classification = TestUtil.createClassification("anyclassification");
        classification.setContactPerson(userRepository.save(TestUtil.createUser()));
        classificationFamilyRepository.save(TestUtil.createClassificationFamily("anyfamily")).addClassificationSeries(
                classification);
        classificationSeriesRepository.save(classification);
        return classification;
    }

    @Configuration
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = { Subscriber.class, TranslatablePersistenceConverter.class })
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {

    }

}
