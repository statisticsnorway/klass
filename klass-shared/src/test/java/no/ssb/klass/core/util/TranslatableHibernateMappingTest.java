package no.ssb.klass.core.util;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.EMBEDDED;

import static org.junit.jupiter.api.Assertions.*;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.ConcreteClassificationItem;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.testutil.TestUtil;

import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.type.BasicType;
import org.hibernate.type.descriptor.java.MutabilityPlan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

@SpringBootTest(
        classes = TranslatableHibernateMappingTest.Config.class,
        webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = {
            "spring.jpa.show-sql=false",
            "spring.jpa.properties.hibernate.generate_statistics=true",
            "spring.jpa.properties.hibernate.cache.use_second_level_cache=false"
        })
@ActiveProfiles({ConfigurationProfiles.POSTGRES_EMBEDDED, "translatable-mapping-test"})
@AutoConfigureEmbeddedDatabase(
        provider = EMBEDDED,
        type = AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@Transactional
class TranslatableHibernateMappingTest {
    @Autowired private EntityManager entityManager;
    @Autowired private EntityManagerFactory entityManagerFactory;
    private SessionFactoryImplementor factory;
    private long itemId;

    @BeforeEach
    void persistFixture() {
        factory = entityManagerFactory.unwrap(SessionFactoryImplementor.class);
        User user = TestUtil.createUser();
        entityManager.persist(user);
        ClassificationFamily family = TestUtil.createClassificationFamily("Mapping fixture");
        entityManager.persist(family);
        ClassificationSeries series = TestUtil.createClassification("Translations");
        series.setContactPerson(user);
        family.addClassificationSeries(series);
        ClassificationVersion version =
                new ClassificationVersion(DateRange.create("2020-01-01", "2030-01-01"));
        series.addClassificationVersion(version);
        version.addLevel(new Level(1));
        ConcreteClassificationItem item =
                new ConcreteClassificationItem(
                        "01",
                        new Translatable("Navn \u00e6\u00f8\u00e5", null, "Name"),
                        Translatable.empty(),
                        new Translatable("A & B < C", "", null));
        version.addClassificationItem(item, 1, null);
        entityManager.persist(series);
        entityManager.flush();
        itemId = item.getId();
        entityManager.clear();
        factory.getStatistics().clear();
    }

    @Test
    void allTranslatedMappingsUseImmutableSnapshots() {
        int attributes = 0;
        for (var entity : factory.getMappingMetamodel().streamEntityDescriptors().toList()) {
            for (int i = 0; i < entity.getPropertyTypes().length; i++) {
                var type = entity.getPropertyTypes()[i];
                if (type instanceof BasicType<?> basic
                        && basic.getReturnedClass() == Translatable.class) {
                    var plan = basic.getJavaTypeDescriptor().getMutabilityPlan();
                    assertFalse(plan.isMutable(), () -> entity.getEntityName() + ": " + plan);
                    assertFalse(basic.isMutable());
                    assertFalse(
                            entity.findAttributeMapping(entity.getPropertyNames()[i])
                                    .getExposedMutabilityPlan()
                                    .isMutable());
                    for (Translatable value :
                            new Translatable[] {
                                null,
                                Translatable.empty(),
                                new Translatable("", null, ""),
                                new Translatable(
                                        "\u00e6\u00f8\u00e5\n", "A & B < C", "\ud83d\ude00")
                            }) {
                        assertSame(value, basic.deepCopy(value, factory));
                    }
                    attributes++;
                }
            }
        }
        assertTrue(
                attributes >= 3, "Must inspect real entity mappings, not just a standalone type");
    }

    @Test
    void managedEntitySnapshotReusesImmutableValueWithoutRoundTrip() {
        ConcreteClassificationItem item = loadItem();
        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
        var entry = session.getPersistenceContextInternal().getEntry(item);
        var persister = entry.getPersister();
        String[] names = persister.getPropertyNames();
        int translations = 0;
        for (int i = 0; i < names.length; i++) {
            if (persister.getPropertyTypes()[i].getReturnedClass() == Translatable.class) {
                assertSame(
                        persister.getPropertyValue(item, i), entry.getLoadedState()[i], names[i]);
                translations++;
            }
        }
        assertEquals(3, translations);
    }

    @Test
    void replacementUpdatesPersistWithoutMakingTheEntityImmutable() {
        ConcreteClassificationItem item = loadItem();
        item.setOfficialName("New & <name> \ud83d\ude00", Language.EN);
        item.setShortName("Kort", Language.NB);
        item.setNotes(null, Language.NB);
        item.setNotes("English notes", Language.EN);
        item.setCode("02");
        entityManager.flush();
        assertEquals(1, factory.getStatistics().getEntityUpdateCount());
        entityManager.clear();
        ConcreteClassificationItem reloaded = loadItem();
        assertEquals("Navn \u00e6\u00f8\u00e5", reloaded.getOfficialName(Language.NB));
        assertEquals("New & <name> \ud83d\ude00", reloaded.getOfficialName(Language.EN));
        assertEquals("Kort", reloaded.getShortName(Language.NB));
        assertEquals("", reloaded.getShortName(Language.EN));
        assertEquals("", reloaded.getNotes(Language.NB));
        assertEquals("", reloaded.getNotes(Language.NN));
        assertEquals("English notes", reloaded.getNotes(Language.EN));
        assertEquals("02", reloaded.getCode());
    }

    @Test
    void equalReplacementDoesNotIssueAnUpdate() {
        ConcreteClassificationItem item = loadItem();
        item.setOfficialName("Navn \u00e6\u00f8\u00e5", Language.NB);
        item.setShortName(null, Language.EN);
        item.setNotes("", Language.NN);
        entityManager.flush();
        assertEquals(0, factory.getStatistics().getEntityUpdateCount());
    }

    @Test
    void emptyAndAbsentLanguagesRemainDistinctAcrossReplacementAndReload() {
        ConcreteClassificationItem item = loadItem();
        item.setShortName("", Language.EN);
        entityManager.flush();
        assertEquals(1, factory.getStatistics().getEntityUpdateCount());
        entityManager.clear();
        item = loadItem();
        Translatable shortName = translatedProperty(item, "shortName");
        assertTrue(shortName.hasLanguage(Language.EN));
        assertEquals("", shortName.getStringOrNull(Language.EN));
        assertNull(shortName.getStringOrNull(Language.NB));
        item.setShortName(null, Language.EN);
        entityManager.flush();
        entityManager.clear();
        assertEquals(Translatable.empty(), translatedProperty(loadItem(), "shortName"));
    }

    @Test
    void immutableCachePlanSupportsNullEmptyAndSerialization() throws Exception {
        MutabilityPlan<Translatable> plan =
                factory.getTypeConfiguration()
                        .getJavaTypeRegistry()
                        .<Translatable>getDescriptor(Translatable.class)
                        .getMutabilityPlan();
        SessionImplementor session = entityManager.unwrap(SessionImplementor.class);
        for (Translatable value :
                new Translatable[] {
                    null,
                    Translatable.empty(),
                    new Translatable("", null, ""),
                    new Translatable("\u00e6\u00f8\u00e5", "A & B < C", "\ud83d\ude00")
                }) {
            Serializable cached = plan.disassemble(value, session);
            assertSame(value, cached);
            assertEquals(value, plan.assemble(serializeAndRead(cached), session));
        }
    }

    private ConcreteClassificationItem loadItem() {
        return entityManager.find(ConcreteClassificationItem.class, itemId);
    }

    private Translatable translatedProperty(ConcreteClassificationItem item, String name) {
        return (Translatable)
                factory.getMappingMetamodel()
                        .getEntityDescriptor(ConcreteClassificationItem.class)
                        .getPropertyValue(item, name);
    }

    private Serializable serializeAndRead(Serializable value) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream output = new ObjectOutputStream(bytes)) {
            output.writeObject(value);
        }
        try (ObjectInputStream input =
                new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return (Serializable) input.readObject();
        }
    }

    @Configuration
    @Profile("translatable-mapping-test")
    @EnableAutoConfiguration
    @EntityScan(basePackageClasses = ClassificationFamily.class)
    @ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
    static class Config {}
}
