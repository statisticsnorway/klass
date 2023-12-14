package no.ssb.klass.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.ReferencingClassificationItem;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilyRepository;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.ClassificationSeriesSpecification;
import no.ssb.klass.core.repository.ClassificationVariantRepository;
import no.ssb.klass.core.repository.ClassificationVersionRepository;
import no.ssb.klass.core.repository.CorrespondenceMapRepository;
import no.ssb.klass.core.repository.CorrespondenceTableRepository;
import no.ssb.klass.core.repository.ReferencingClassificationItemRepository;
import no.ssb.klass.core.repository.StatisticalUnitRepository;
import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.testutil.TestUtil;

public class ClassificationServiceImplTest {
    private ClassificationServiceImpl subject;
    private ClassificationFamilyRepository classificationFamilyRepositoryMock;
    private ClassificationSeriesRepository classificationSeriesRepositoryMock;
    private ClassificationVersionRepository classificationVersionRepositoryMock;
    private ClassificationVariantRepository classificationVariantRepositoryMock;
    private CorrespondenceTableRepository correspondenceTableRepositoryMock;
    private ReferencingClassificationItemRepository referencingClassificationItemRepositoryMock;
    private CorrespondenceMapRepository correspondenceMapRepositoryMock;
    private StatisticalUnitRepository statisticalUnitRepositoryMock;
    private SearchService searchServiceMock;
    private UserRepository userRepositoryMock;

    @BeforeEach
    public void setup() {
        classificationFamilyRepositoryMock = mock(ClassificationFamilyRepository.class);
        classificationSeriesRepositoryMock = mock(ClassificationSeriesRepository.class);
        classificationVersionRepositoryMock = mock(ClassificationVersionRepository.class);
        classificationVariantRepositoryMock = mock(ClassificationVariantRepository.class);
        correspondenceTableRepositoryMock = mock(CorrespondenceTableRepository.class);
        referencingClassificationItemRepositoryMock = mock(ReferencingClassificationItemRepository.class);
        correspondenceMapRepositoryMock = mock(CorrespondenceMapRepository.class);
        statisticalUnitRepositoryMock = mock(StatisticalUnitRepository.class);
        searchServiceMock = mock(SearchService.class);
        userRepositoryMock = mock(UserRepository.class);
        subject = new ClassificationServiceImpl(classificationFamilyRepositoryMock, classificationSeriesRepositoryMock,
                classificationVersionRepositoryMock, classificationVariantRepositoryMock,
                correspondenceTableRepositoryMock, referencingClassificationItemRepositoryMock,
                correspondenceMapRepositoryMock, statisticalUnitRepositoryMock, searchServiceMock, userRepositoryMock);
    }

    @Test
    public void findAll() {
        // given
        final Pageable pageable = createPageable();
        when(classificationSeriesRepositoryMock.findAll(any(ClassificationSeriesSpecification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        // when
        Page<ClassificationSeries> result = subject.findAll(false, null, createPageable());

        // then
        verify(classificationSeriesRepositoryMock, times(1)).findAll(any(ClassificationSeriesSpecification.class), eq(
                pageable));
        assertEquals(0, result.getSize());
    }

    @Test
    public void findAllClassificationSeries() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setDeleted();
        when(classificationSeriesRepositoryMock.findAll()).thenReturn(Lists.newArrayList(classification));

        // when
        List<ClassificationSeries> result = subject.findAllClassificationSeries();

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void getClassificationSeries() {
        // given
        final Long id = 1L;
        when(classificationSeriesRepositoryMock.findById(id)).thenReturn(Optional.of(
                TestUtil.createClassificationWithId(id, "name")));

        // when
        ClassificationSeries result = subject.getClassificationSeries(id);

        // then
        verify(classificationSeriesRepositoryMock, times(1)).findById(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void getClassificationSeriesFiltersDeleted() {
        // given
        final Long id = 1L;
        ClassificationSeries classification = TestUtil.createClassificationWithId(id, "name");
        classification.setDeleted();
        when(classificationSeriesRepositoryMock.findById(id)).thenReturn(Optional.of(classification));

        // when
        Assertions.assertThrows(KlassResourceNotFoundException.class, () ->
                subject.getClassificationSeries(id));

        // then expect exception
    }

    @Test
    public void getClassificationVariant() {
        // given
        final Long id = 1L;
        ClassificationVariant variant = TestUtil.createClassificationVariant("name", TestUtil.createUser());
        variant.setId(id);
        createClassificationVersion().addClassificationVariant(variant);
        when(classificationVariantRepositoryMock.findById(id)).thenReturn(Optional.of(variant));

        // when
        ClassificationVariant result = subject.getClassificationVariant(id);

        // then
        verify(classificationVariantRepositoryMock, times(1)).findById(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void getClassificationVariantFiltersDeleted() {
        // given
        final Long id = 1L;
        ClassificationVariant variant = TestUtil.createClassificationVariant("name", TestUtil.createUser());
        variant.setId(id);
        variant.setDeleted();
        when(classificationVariantRepositoryMock.findById(id)).thenReturn(Optional.of(variant));

        // when
        Assertions.assertThrows(KlassResourceNotFoundException.class, () ->
                subject.getClassificationVariant(id));

        // then expect exception
    }

    @Test
    public void getClassificationVersion() {
        // given
        final Long id = 1L;
        ClassificationVersion version = createClassificationVersion();
        version.setId(id);
        when(classificationVersionRepositoryMock.findById(id)).thenReturn(Optional.of(version));

        // when
        ClassificationVersion result = subject.getClassificationVersion(id);

        // then
        verify(classificationVersionRepositoryMock, times(1)).findById(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void getClassificationVersionFiltersDeleted() {
        // given
        final Long id = 1L;
        ClassificationVersion version = TestUtil.createClassificationVersionWithTable(id,
                TestUtil.anyDateRange(), "name");
        version.setDeleted();
        when(classificationVersionRepositoryMock.findById(id)).thenReturn(Optional.of(version));

        // when
        Assertions.assertThrows(KlassResourceNotFoundException.class, () ->
                subject.getClassificationVersion(id));

        // then expect exception
    }

    @Test
    public void getCorrespondenceTable() {
        // given
        final Long id = 1L;
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(createClassificationVersion(),
                createClassificationVersion());
        correspondenceTable.setId(id);
        when(correspondenceTableRepositoryMock.findById(id)).thenReturn(Optional.of(correspondenceTable));

        // when
        CorrespondenceTable result = subject.getCorrespondenceTable(id);

        // then
        verify(correspondenceTableRepositoryMock, times(1)).findById(id);
        assertEquals(id, result.getId());
    }

    @Test
    public void getCorrespondenceTableFiltersDeleted() {
        // given
        final Long id = 1L;
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(createClassificationVersion(),
                createClassificationVersion());
        correspondenceTable.setId(id);
        correspondenceTable.setDeleted();
        when(correspondenceTableRepositoryMock.findById(id)).thenReturn(Optional.of(correspondenceTable));

        // when
        Assertions.assertThrows(KlassResourceNotFoundException.class, () ->
                subject.getCorrespondenceTable(id));

        // then expect exception
    }

    @Test
    public void findByIdClassificationSeriesWithName() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");
        when(classificationSeriesRepositoryMock.findByNameNoIgnoreCase(anyString())).thenReturn(classification);
        when(classificationSeriesRepositoryMock.findByNameNnIgnoreCase(anyString())).thenReturn(classification);
        when(classificationSeriesRepositoryMock.findByNameEnIgnoreCase(anyString())).thenReturn(classification);

        // then
        assertEquals(true, subject.findOneClassificationSeriesWithName("name", Language.NB).isPresent());
        assertEquals(true, subject.findOneClassificationSeriesWithName("name", Language.NN).isPresent());
        assertEquals(true, subject.findOneClassificationSeriesWithName("name", Language.EN).isPresent());
    }

    @Test
    public void findByIdClassificationSeriesWithNameFiltersDeleted() {
        // given
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setDeleted();
        when(classificationSeriesRepositoryMock.findByNameNoIgnoreCase(anyString())).thenReturn(classification);
        when(classificationSeriesRepositoryMock.findByNameNnIgnoreCase(anyString())).thenReturn(classification);
        when(classificationSeriesRepositoryMock.findByNameEnIgnoreCase(anyString())).thenReturn(classification);

        // then
        assertEquals(false, subject.findOneClassificationSeriesWithName("name", Language.NB).isPresent());
        assertEquals(false, subject.findOneClassificationSeriesWithName("name", Language.NN).isPresent());
        assertEquals(false, subject.findOneClassificationSeriesWithName("name", Language.EN).isPresent());
    }

    @Test
    public void testDeleteClasificationWithWrongUser() throws Exception {
        User mockUser = mock(User.class);
        User mockOwner = mock(User.class);
        ClassificationSeries classificationSeriesMock = mock(ClassificationSeries.class);
        when(mockUser.getUsername()).thenReturn("Donald");
        when(mockOwner.getUsername()).thenReturn("Skrue");
        when(classificationSeriesMock.getNameInPrimaryLanguage()).thenReturn("test");
        when(classificationSeriesMock.getCategoryName()).thenReturn("test");
        when(classificationSeriesMock.getContactPerson()).thenReturn(mockOwner);
        when(classificationSeriesRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(classificationSeriesMock));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        Assertions.assertThrows(KlassMessageException.class, () ->
                subject.deleteNotIndexClassification(mockUser, classificationSeriesMock));
    }

    @Test
    public void testDeleteClasificationPublished() throws Exception {
        User mockUser = mock(User.class);
        ClassificationSeries classificationSeriesMock = mock(ClassificationSeries.class);
        when(mockUser.getUsername()).thenReturn("Donald");
        when(classificationSeriesMock.getContactPerson()).thenReturn(mockUser);
        when(classificationSeriesMock.getNameInPrimaryLanguage()).thenReturn("test");
        when(classificationSeriesMock.getCategoryName()).thenReturn("test");
        when(classificationSeriesMock.isPublishedInAnyLanguage()).thenReturn(true);
        when(classificationSeriesRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(classificationSeriesMock));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        Assertions.assertThrows(KlassMessageException.class, () ->
                subject.deleteNotIndexClassification(mockUser, classificationSeriesMock));
    }

    @Test
    public void testDeleteNotOwnerClasificationWithSuperUser() throws Exception {
        User mockUser = mock(User.class);
        User mockOwner = mock(User.class);
        ClassificationSeries classification = mock(ClassificationSeries.class);
        when(mockUser.getUsername()).thenReturn("Donald");
        when(mockUser.isAdministrator()).thenReturn(true); // Believe it or not
        when(mockOwner.getUsername()).thenReturn("Skrue");
        when(classification.getContactPerson()).thenReturn(mockOwner);
        when(classification.isPublishedInAnyLanguage()).thenReturn(false);
        when(classification.getOwnerClassification()).thenReturn(classification);
        when(classificationSeriesRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(classification));
        when(classificationSeriesRepositoryMock.save(classification)).thenReturn(classification);
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        subject.deleteNotIndexClassification(mockUser, classification);
        verify(classificationSeriesRepositoryMock, times(1)).save(classification);
    }

    @Test
    public void testDeletePublishedClasificationWithSuperUser() throws Exception {
        User mockUser = mock(User.class);
        User mockOwner = mock(User.class);
        ClassificationSeries classification = mock(ClassificationSeries.class);
        when(mockUser.getUsername()).thenReturn("Donald");
        when(mockUser.isAdministrator()).thenReturn(true); // Believe it or not
        when(mockOwner.getUsername()).thenReturn("Skrue");
        when(classification.getContactPerson()).thenReturn(mockOwner);
        when(classification.getOwnerClassification()).thenReturn(classification);
        when(classification.isPublishedInAnyLanguage()).thenReturn(true);
        when(classificationSeriesRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(classification));
        when(classificationSeriesRepositoryMock.save(classification)).thenReturn(classification);
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        subject.deleteNotIndexClassification(mockUser, classification);
        verify(classificationSeriesRepositoryMock, times(1)).save(classification);
    }

    @Test
    public void testDeleteClasificationNotPublished() throws Exception {
        User mockUser = mock(User.class);
        when(mockUser.getUsername()).thenReturn("Donald");
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setContactPerson(mockUser);
        classification.setId(1L);

        when(classificationSeriesRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(classification));
        when(userRepositoryMock.findById(any(Long.class))).thenReturn(Optional.of(mockUser));
        when(classificationSeriesRepositoryMock.save(classification)).thenReturn(classification);
        subject.deleteNotIndexClassification(classification.getContactPerson(), classification);
        verify(classificationSeriesRepositoryMock, times(1)).save(classification);
    }



    @Test
    public void testThatSaveClassificationVersionUpdatesClassificationLastModified() {
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setId(1L);

        ClassificationVersion mock = Mockito.mock(ClassificationVersion.class);
        when(mock.getOwnerClassification()).thenReturn(classification);

        when(classificationVersionRepositoryMock.save(mock)).thenReturn(mock);
        subject.saveNotIndexVersion(mock);
        verify(classificationSeriesRepositoryMock, times(1)).updateClassificationLastModified(eq(1L), any(Date.class));
    }

    @Test
    public void testThatSaveClassificationVariantUpdatesClassificationLastModified() {
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setId(1L);

        ClassificationVariant mock = Mockito.mock(ClassificationVariant.class);
        when(mock.getOwnerClassification()).thenReturn(classification);

        when(classificationVariantRepositoryMock.save(mock)).thenReturn(mock);
        subject.saveNotIndexVariant(mock);
        verify(classificationSeriesRepositoryMock, times(1)).updateClassificationLastModified(eq(1L), any(Date.class));
    }

    @Test
    public void testThatSaveCorrespondenceTableUpdatesClassificationLastModified() {
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.setId(1L);

        CorrespondenceTable mock = Mockito.mock(CorrespondenceTable.class);
        when(mock.getOwnerClassification()).thenReturn(classification);

        when(correspondenceTableRepositoryMock.save(mock)).thenReturn(mock);
        subject.saveNotIndexCorrespondenceTable(mock);
        verify(classificationSeriesRepositoryMock, times(1)).updateClassificationLastModified(eq(1L), any(Date.class));
    }

    @Test
    public void testSaveStatisticalUnit() {
        StatisticalUnit input = new StatisticalUnit(Translatable.empty().withLanguage("test", Language.NN));
        when(statisticalUnitRepositoryMock.save(any(StatisticalUnit.class))).thenAnswer(invocation -> {
            StatisticalUnit answer = (StatisticalUnit) invocation.getArguments()[0];
            answer.setId(1L);
            return answer;
        });
        assertThat(input.getId()).isNull();
        StatisticalUnit output = subject.saveStatisticalUnit(input);
        verify(statisticalUnitRepositoryMock, times(1)).save(input);
        assertThat(output.getId()).isEqualTo(1L);
        assertThat(output.getName(Language.NN)).isEqualTo("test");

    }

    @Test
    public void testFindAllStatisticalUnits() {
        List<StatisticalUnit> statisticalUnitsValues = Arrays.asList(new StatisticalUnit(Translatable.create("name",
                Language.NB)), new StatisticalUnit(Translatable.create("name", Language.NB)));
        when(statisticalUnitRepositoryMock.findAll()).thenReturn(statisticalUnitsValues);

        List<StatisticalUnit> statisticalUnits = subject.findAllStatisticalUnits();
        assertThat(statisticalUnits.size()).isEqualTo(2);
        verify(statisticalUnitRepositoryMock, times(1)).findAll();
    }

    @Test
    public void copyClassificationVersion() throws Exception {
        String introduction = "TEST 2016";
        Translatable derived = new Translatable("Basert på", null, null);
        Translatable legalbase = new Translatable("Forordning", null, null);
        Translatable publications = new Translatable("Publikasjoner", null, null);

        when(classificationVersionRepositoryMock.save(any(ClassificationVersion.class))).then(
                new Answer<ClassificationVersion>() {
                    @Override
                    public ClassificationVersion answer(InvocationOnMock invocation) throws Throwable {
                        return (ClassificationVersion) invocation.getArguments()[0];
                    }

                });

        LocalDate from = LocalDate.of(2016, 1, 1);
        LocalDate to = LocalDate.of(2016, 3, 1);
        DateRange range = DateRange.create(from, to);

        LocalDate fromCopy = LocalDate.of(2016, 4, 1);
        LocalDate toCopy = LocalDate.of(2016, 12, 1);
        DateRange rangeCopy = DateRange.create(fromCopy, toCopy);

        Language language = Language.NB;
        ClassificationVersion classificationVersion = new ClassificationVersion(range);
        classificationVersion.setIntroduction(introduction, language);
        classificationVersion.setDerivedFrom(derived.getString(language), language);
        classificationVersion.setLegalBase(legalbase.getString(language), language);
        classificationVersion.setPublications(publications.getString(language), language);
        classificationVersion.addLevel(new Level(1));
        classificationVersion.addLevel(new Level(2));
        ClassificationItem item11 = TestUtil.createClassificationItem("1.1", "Test 1-1");
        ClassificationItem item12 = TestUtil.createClassificationItem("1.2", "Test 1-2");
        classificationVersion.addClassificationItem(item11, 1, null);
        classificationVersion.addClassificationItem(item12, 1, null);
        ClassificationItem item21 = TestUtil.createClassificationItem("2.1", "Test 2-1");
        ClassificationItem item22 = TestUtil.createClassificationItem("2.2", "Test 2-2");
        classificationVersion.addClassificationItem(item21, 2, item11);
        classificationVersion.addClassificationItem(item22, 2, item12);
        ClassificationSeries classificationSeries = new ClassificationSeries(Translatable.create("TEST", language),
                Translatable.create("TST", language), false, Language.NB, ClassificationType.CLASSIFICATION,
                new User("Donald", "Doanld Duck", "Gakk"));
        classificationSeries.addClassificationVersion(classificationVersion);

        when(classificationVersionRepositoryMock.findById(anyLong())).thenReturn(Optional.of(classificationVersion));

        ClassificationVersion classificationVersionCopy = subject.copyClassificationVersion(classificationVersion,
                rangeCopy);
        assertEquals(rangeCopy, classificationVersionCopy.getDateRange());
        assertEquals(introduction, classificationVersionCopy.getIntroduction(Language.NB));
        assertEquals(derived.getString(Language.NB), classificationVersionCopy.getDerivedFrom(Language.NB));
        assertEquals(legalbase.getString(Language.NB), classificationVersionCopy.getLegalBase(Language.NB));
        assertEquals(publications.getString(Language.NB), classificationVersionCopy.getPublications(Language.NB));
        List<ClassificationItem> classificationItems = classificationVersionCopy.getAllClassificationItems();
        assertEquals(4, classificationItems.size());
        findEqualClassificationItem(classificationItems, item11);
        findEqualClassificationItem(classificationItems, item12);
        findEqualClassificationItem(classificationItems, item21);
        findEqualClassificationItem(classificationItems, item22);
    }

    @Test
    public void findReferencesOfClassificationItemNoReferences() {
        // given

        // when
        Set<String> result = subject.findReferencesOfClassificationItem(createClassificationItemWithId());

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findReferencesOfClassificationItemReferencingItem() {
        // given
        ReferencingClassificationItem item = new ReferencingClassificationItem(createClassificationItem());
        ClassificationVersion version = createClassificationVersion();
        version.addNextLevel();
        version.addClassificationItem(item, 1, null);

        when(referencingClassificationItemRepositoryMock.findByReference(any(), anyBoolean()))
                .thenReturn(Lists.newArrayList(item));

        // when
        Set<String> result = subject.findReferencesOfClassificationItem(createClassificationItemWithId());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findReferencesOfClassificationItemWithCorrespondenceMapSource() {
        // given
        ClassificationVersion source = createClassificationVersion();
        ClassificationVersion target = createClassificationVersion();
        CorrespondenceMap map = new CorrespondenceMap(createClassificationItem(), createClassificationItem());
        TestUtil.createCorrespondenceTable(source, target).addCorrespondenceMap(map);

        when(correspondenceMapRepositoryMock.findBySourceOrTarget(any(ClassificationItem.class), anyBoolean()))
                .thenReturn(Sets.newHashSet(map));

        // when
        Set<String> result = subject.findReferencesOfClassificationItem(createClassificationItemWithId());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findReferencesOfClassificationItemWithCorrespondenceMapTarget() {
        // given
        ClassificationVersion source = createClassificationVersion();
        ClassificationVersion target = createClassificationVersion();
        CorrespondenceMap map = new CorrespondenceMap(createClassificationItem(), createClassificationItem());
        TestUtil.createCorrespondenceTable(source, target).addCorrespondenceMap(map);

        when(correspondenceMapRepositoryMock.findBySourceOrTarget(any(ClassificationItem.class), anyBoolean()))
                .thenReturn(Sets.newHashSet(map));

        // when
        Set<String> result = subject.findReferencesOfClassificationItem(createClassificationItemWithId());

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findCorrespondenceTablesWithSource() {
        // given
        ClassificationVersion source = createClassificationVersion();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(source,
                createClassificationVersion());
        when(correspondenceTableRepositoryMock.findBySource(source)).thenReturn(Lists.newArrayList(
                correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesWithSource(source);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findCorrespondenceTablesWithSourceFiltersDeleted() {
        // given
        ClassificationVersion source = createClassificationVersion();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(source,
                createClassificationVersion());
        correspondenceTable.setDeleted();
        when(correspondenceTableRepositoryMock.findBySource(source)).thenReturn(Lists.newArrayList(
                correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesWithSource(source);

        // then
        assertEquals(0, result.size());
    }

    @Test
    public void findCorrespondenceTablesWithTarget() {
        // given
        ClassificationVersion target = createClassificationVersion();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(createClassificationVersion(),
                target);
        when(correspondenceTableRepositoryMock.findByTarget(target)).thenReturn(Lists.newArrayList(
                correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesWithTarget(target);

        // then
        assertEquals(1, result.size());
    }

    @Test
    public void findCorrespondenceTablesWithTargetFiltersDeleted() {
        // given
        ClassificationVersion target = createClassificationVersion();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(createClassificationVersion(),
                target);
        correspondenceTable.setDeleted();
        when(correspondenceTableRepositoryMock.findByTarget(target)).thenReturn(Lists.newArrayList(
                correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesWithTarget(target);

        // then
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findCorrespondenceTablesBetween() {
        // given
        ClassificationVersion sourceVersion = createClassificationVersion();
        ClassificationVersion targetVersion = createClassificationVersion();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable("description", sourceVersion, 0,
                targetVersion, 0);
        when(correspondenceTableRepositoryMock.findBySourceInAndTargetIn(anyList(), anyList())).thenReturn(Lists
                .newArrayList(correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesBetween(sourceVersion, null, targetVersion,
                null);

        // then
        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findCorrespondenceTablesBetweenWithSpecifiedLevels() {
        // given
        ClassificationVersion sourceVersion = createClassificationVersion();
        sourceVersion.addNextLevel();
        ClassificationVersion targetVersion = createClassificationVersion();
        targetVersion.addNextLevel();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable("description", sourceVersion, 1,
                targetVersion, 1);
        when(correspondenceTableRepositoryMock.findBySourceInAndTargetIn(anyList(), anyList())).thenReturn(Lists
                .newArrayList(correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesBetween(sourceVersion, sourceVersion
                .getFirstLevel().get(), targetVersion, targetVersion.getFirstLevel().get());

        // then
        assertEquals(1, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findCorrespondenceTablesBetweenNotMatchingLevels() {
        // given
        ClassificationVersion sourceVersion = createClassificationVersion();
        sourceVersion.addNextLevel();
        ClassificationVersion targetVersion = createClassificationVersion();
        targetVersion.addNextLevel();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable("description", sourceVersion, 1,
                targetVersion, 1);
        when(correspondenceTableRepositoryMock.findBySourceInAndTargetIn(anyList(), anyList())).thenReturn(Lists
                .newArrayList(correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesBetween(sourceVersion, sourceVersion
                .getFirstLevel()
                .get(), targetVersion, null);

        // then
        assertEquals(0, result.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void findCorrespondenceTablesBetweenHandlesTargetFirst() {
        // given
        ClassificationVersion sourceVersion = createClassificationVersion();
        sourceVersion.addNextLevel();
        ClassificationVersion targetVersion = createClassificationVersion();
        targetVersion.addNextLevel();
        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable("description", sourceVersion, 1,
                targetVersion, 1);
        when(correspondenceTableRepositoryMock.findBySourceInAndTargetIn(anyList(), anyList())).thenReturn(Lists
                .newArrayList(correspondenceTable));

        // when
        List<CorrespondenceTable> result = subject.findCorrespondenceTablesBetween(targetVersion, targetVersion
                .getFirstLevel().get(), sourceVersion, sourceVersion.getFirstLevel().get());

        // then
        assertEquals(1, result.size());
    }

    private ClassificationVersion createClassificationVersion() {
        ClassificationVersion version = TestUtil.createClassificationVersion(TestUtil.anyDateRange());
        ClassificationSeries classification = TestUtil.createClassification("name");
        classification.addClassificationVersion(version);
        return version;
    }

    private ClassificationItem createClassificationItem() {
        return TestUtil.createClassificationItem("code", "officialName");
    }

    public ClassificationItem createClassificationItemWithId() {
        ClassificationItem item = createClassificationItem();
        item.setId(1L);
        return item;
    }

    private boolean findEqualClassificationItem(List<ClassificationItem> copyedItems, ClassificationItem item) {
        for (ClassificationItem copyedItem : copyedItems) {
            if (copyedItem.getCode().equals(item.getCode()) && copyedItem.getLevel() == item.getLevel()) {
                return true;
            }
        }
        return false;
    }

    private Pageable createPageable() {
        return PageRequest.of(0, 3);
    }

}
