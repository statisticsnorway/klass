package no.ssb.klass.core.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.Disabled;

// import no.ssb.klass.core.model.ClassificationSeries;
// import no.ssb.klass.core.model.ClassificationType;
// import no.ssb.klass.core.model.ClassificationVersion;
// import no.ssb.klass.core.model.Language;
// import no.ssb.klass.core.model.StatisticalUnit;
// import no.ssb.klass.core.model.User;
// import no.ssb.klass.core.repository.ClassificationAccessRepository;
// import no.ssb.klass.core.repository.ClassificationSeriesRepository;
// import no.ssb.klass.core.repository.SearchWordsRepository;
// import no.ssb.klass.core.repository.StatisticalUnitRepository;
// import no.ssb.klass.core.repository.SubscriberRepository;
// import no.ssb.klass.core.service.dto.AccessCounterDto;
// import no.ssb.klass.core.service.dto.ClassificationReportDto;
// import no.ssb.klass.core.service.dto.ClassificationVersionReportDto;
// import no.ssb.klass.core.service.dto.StatisticalEntity;
// import no.ssb.klass.designer.ClassificationReportData;
// import no.ssb.klass.designer.VersionReportData;
// import no.ssb.klass.designer.admin.UsageStatisticsData;
// import no.ssb.klass.designer.admin.util.ContentUseStatisticRows;
// import no.ssb.klass.designer.admin.util.ContentUseStatisticRows.ReportModeChoice;
// import no.ssb.klass.designer.admin.util.UsageStatisticsRows.ReportDescription;
// import no.ssb.klass.designer.admin.util.UsageStatisticsRows.UseStatisticsModeChoice;

@Disabled(
        "API er omskrevet for å løskoble Vaadin komponenter/logikk  disse restene må splittes i service  og klient")
public class StatisticsServiceImplTest {
    //    private ClassificationSeriesRepository classificationSeriesRepositoryMock;
    //    private SearchWordsRepository searchWordsRepositoryMock;
    //    private ClassificationAccessRepository classificationAccessCounterRepositoryMock;
    //    private StatisticalUnitRepository statisticalUnitRepositoryMock;
    //    private SubscriberRepository subscriberRepositoryMock;
    //    private StatisticsServiceImpl subject;
    //
    //    @BeforeEach
    //    public void setup() {
    //        classificationSeriesRepositoryMock = mock(ClassificationSeriesRepository.class);
    //        searchWordsRepositoryMock = mock(SearchWordsRepository.class);
    //        classificationAccessCounterRepositoryMock =
    // mock(ClassificationAccessRepository.class);
    //        statisticalUnitRepositoryMock = mock(StatisticalUnitRepository.class);
    //        subscriberRepositoryMock = mock(SubscriberRepository.class);
    //
    //        subject = new StatisticsServiceImpl(
    //                classificationSeriesRepositoryMock, classificationAccessCounterRepositoryMock,
    //                searchWordsRepositoryMock, statisticalUnitRepositoryMock,
    // subscriberRepositoryMock);
    //    }
    //
    //    @Test
    //    public void testGenerateContentUseStat() {
    //        String section = "007";
    //
    // when(classificationSeriesRepositoryMock.finNumberOfClassifications(any(ClassificationType.class), anyString()))
    //                .thenReturn(42);
    //
    // when(classificationSeriesRepositoryMock.findNumberOfPublishedClassifications(any(ClassificationType.class),
    //                anyString()))
    //                        .thenReturn(22);
    //
    // when(classificationSeriesRepositoryMock.findNumberOfPublishedVersionsAnyLanguages(any(ClassificationType.class),
    //                anyString()))
    //                        .thenReturn(104);
    //
    // when(classificationSeriesRepositoryMock.findNumberOfPublishedVersionsAllLanguages(any(ClassificationType.class),
    //                anyString()))
    //                        .thenReturn(55);
    //        ContentUseStatisticRows res = subject.generateContentUseStat(section,
    // ClassificationType.CLASSIFICATION);
    //        assertEquals(42, res.getRows().get(ReportModeChoice.TOTAL).intValue());
    //        assertEquals(22, res.getRows().get(ReportModeChoice.PUBLISHED).intValue());
    //        assertEquals(20, res.getRows().get(ReportModeChoice.UNPUBLISHED).intValue());
    //        assertEquals(49, res.getRows().get(ReportModeChoice.MISSING_LANG).intValue());
    //        verify(classificationSeriesRepositoryMock, times(1)).finNumberOfClassifications(eq(
    //                ClassificationType.CLASSIFICATION), eq(section));
    //        verify(classificationSeriesRepositoryMock,
    // times(1)).findNumberOfPublishedClassifications(
    //                eq(ClassificationType.CLASSIFICATION), eq(section));
    //        verify(classificationSeriesRepositoryMock,
    // times(1)).findNumberOfPublishedVersionsAnyLanguages(
    //                eq(ClassificationType.CLASSIFICATION), eq(section));
    //        verify(classificationSeriesRepositoryMock,
    // times(1)).findNumberOfPublishedVersionsAllLanguages(
    //                eq(ClassificationType.CLASSIFICATION), eq(section));
    //    }
    //
    //    @Test
    //    public void testClassificationReportTotal() {
    //        String section = "007";
    //        List<ClassificationReportDto> mockResult = makeClassificationData("A",
    // "AllClassifications");
    //
    // when(classificationSeriesRepositoryMock.getClassificationReport(any(ClassificationType.class),
    // anyString()))
    //                .thenReturn(mockResult);
    //        ClassificationReportData<?> result = subject.getPublishedReport(section,
    // ClassificationType.CLASSIFICATION,
    //                ReportModeChoice.TOTAL);
    //        checkClassificationReportData(result, "A", "AllClassifications");
    //    }
    //
    //    @Test
    //    public void testClassificationReportDataPublished() {
    //        String section = "007";
    //        List<ClassificationReportDto> mockResult = makeClassificationData("B",
    // "PublishedAllClassifications");
    //
    // when(classificationSeriesRepositoryMock.getPublishedClassificationReport(any(ClassificationType.class),
    //                anyString())).thenReturn(mockResult);
    //        ClassificationReportData<?> result = subject.getPublishedReport(section,
    // ClassificationType.CLASSIFICATION,
    //                ReportModeChoice.PUBLISHED);
    //        checkClassificationReportData(result, "B", "PublishedAllClassifications");
    //    }
    //
    //    @Test
    //    public void testClassificationReportDataUnPublished() {
    //        String section = "007";
    //        List<ClassificationReportDto> all = makeClassificationData("A", "AllClassifications");
    //        List<ClassificationReportDto> published = makeClassificationData13("B",
    // "PublishedAllClassifications");
    //
    // when(classificationSeriesRepositoryMock.getClassificationReport(any(ClassificationType.class),
    // anyString()))
    //                .thenReturn(all);
    //
    // when(classificationSeriesRepositoryMock.getPublishedClassificationReport(any(ClassificationType.class),
    //                anyString())).thenReturn(published);
    //        ClassificationReportData<?> result = subject.getPublishedReport(section,
    // ClassificationType.CLASSIFICATION,
    //                ReportModeChoice.UNPUBLISHED);
    //        checkClassificationReportDataNotPub(result, "A", "AllClassifications");
    //    }
    //
    //    @Test
    //    public void testVersionReportMissingLanguages() {
    //        String section = "007";
    //        List<ClassificationVersionReportDto> all = makeClassificationVersionData("A",
    // "AllClassifications");
    //        List<ClassificationVersionReportDto> published = makeClassificationVersionData1("A",
    // "AllClassifications");
    //
    // when(classificationSeriesRepositoryMock.getPublishedVersionsAnyLanguages(any(ClassificationType.class),
    //                anyString())).thenReturn(all);
    //
    // when(classificationSeriesRepositoryMock.getPublishedVersionsAllLanguages(any(ClassificationType.class),
    //                anyString())).thenReturn(published);
    //        VersionReportData result = (VersionReportData) subject
    //                .getPublishedReport(section, ClassificationType.CLASSIFICATION,
    // ReportModeChoice.MISSING_LANG);
    //        checkVersionReportData(result, "A");
    //    }
    //
    //    @Test
    //    public void testGetUsageStatistics() {
    //        when(classificationAccessCounterRepositoryMock.getAccessSum(any(Date.class),
    // any(Date.class))).thenReturn(42);
    //        when(searchWordsRepositoryMock.getNumberOfSearchWords(any(Date.class),
    // any(Date.class))).thenReturn(55);
    //        when(searchWordsRepositoryMock.getNumberOfMiss(any(Date.class),
    // any(Date.class))).thenReturn(25);
    //        Map<ReportDescription<UseStatisticsModeChoice>, Integer> result =
    // subject.getUsageStatistics(getFromDate(),
    //                getToDate()).getRows();
    //        assertEquals(Integer.valueOf(42),
    // result.get(UseStatisticsModeChoice.TOTAL_CLASSIFIC));
    //        assertEquals(Integer.valueOf(55),
    // result.get(UseStatisticsModeChoice.TOTAL_SEARCH_WORDS));
    //        assertEquals(Integer.valueOf(25),
    // result.get(UseStatisticsModeChoice.NUMBEROF_SEARCH_RETURNED_NULL));
    //    }
    //
    //    @Test
    //    public void testGetUsageStatisticsClassification() {
    //
    // when(classificationAccessCounterRepositoryMock.getClassificationsCount(any(Date.class),
    // any(Date.class), any(
    //                PageRequest.class)))
    //                        .thenReturn(makeTestData());
    //        UsageStatisticsData result = subject.getUsageStatistics(getFromDate(), getToDate(),
    //                UseStatisticsModeChoice.TOTAL_CLASSIFIC,
    //                PageRequest.of(0, Integer.MAX_VALUE));
    //        checkResult(result);
    //    }
    //
    //    @Test
    //    public void testGetUsageStatisticsSearchReturnedNull() {
    //        when(searchWordsRepositoryMock.getSearchWords(anyBoolean(), any(Date.class),
    // any(Date.class), any(
    //                PageRequest.class)))
    //                        .thenReturn(makeTestData());
    //        UsageStatisticsData result = subject.getUsageStatistics(getFromDate(), getToDate(),
    //                UseStatisticsModeChoice.NUMBEROF_SEARCH_RETURNED_NULL,
    //                PageRequest.of(0, Integer.MAX_VALUE));
    //        checkResult(result);
    //    }
    //
    //    @Test
    //    public void testGetUsageStatisticsTotalSearchWords() {
    //        when(searchWordsRepositoryMock.getSearchWords(any(Date.class), any(Date.class),
    // any(PageRequest.class)))
    //                .thenReturn(makeTestData());
    //        UsageStatisticsData result = subject.getUsageStatistics(getFromDate(), getToDate(),
    //                UseStatisticsModeChoice.TOTAL_SEARCH_WORDS,
    //                PageRequest.of(0, Integer.MAX_VALUE));
    //        checkResult(result);
    //    }
    //
    //    @Test
    //    public void testGetAllClassificationSeriesForStaticalUnit() {
    //        ClassificationType classificationType = ClassificationType.CLASSIFICATION;
    //        ClassificationSeries classificationSeries1 = mock(ClassificationSeries.class);
    //        ClassificationSeries classificationSeries2 = mock(ClassificationSeries.class);
    //        ClassificationSeries classificationSeries3 = mock(ClassificationSeries.class);
    //        ClassificationSeries classificationSeries4 = mock(ClassificationSeries.class);
    //        when(classificationSeries1.getNameInPrimaryLanguage()).thenReturn("C3");
    //        when(classificationSeries2.getNameInPrimaryLanguage()).thenReturn("C4");
    //        when(classificationSeries3.getNameInPrimaryLanguage()).thenReturn("C1");
    //        when(classificationSeries4.getNameInPrimaryLanguage()).thenReturn("C2");
    //        when(classificationSeries1.getClassificationType()).thenReturn(classificationType);
    //        when(classificationSeries2.getClassificationType()).thenReturn(classificationType);
    //        when(classificationSeries3.getClassificationType()).thenReturn(classificationType);
    //        when(classificationSeries4.getClassificationType()).thenReturn(classificationType);
    //
    //        User user1 = mock(User.class);
    //        User user2 = mock(User.class);
    //        when(user1.getSection()).thenReturn("S1");
    //        when(user2.getSection()).thenReturn("S2");
    //        when(user1.getFullname()).thenReturn("Donald");
    //        when(user2.getFullname()).thenReturn("Donald");
    //
    //        List<ClassificationReportDto> generatedResult = new ArrayList<>();
    //        generatedResult.add(new ClassificationReportDto(classificationSeries2, user1));
    //        generatedResult.add(new ClassificationReportDto(classificationSeries4, user2));
    //        generatedResult.add(new ClassificationReportDto(classificationSeries3, user1));
    //        generatedResult.add(new ClassificationReportDto(classificationSeries1, user2));
    //        StatisticalUnit statisticalUnit = mock(StatisticalUnit.class);
    //
    // when(statisticalUnitRepositoryMock.getAllClassificationSeriesForStaticalUnit(any(StatisticalUnit.class)))
    //                .thenReturn(generatedResult);
    //        ClassificationReportData<ClassificationReportDto> result =
    // subject.getAllClassificationSeriesForStaticalUnit(
    //                statisticalUnit);
    //        assertEquals(
    //
    // "S1;C1;Klassifikasjon;Donald;\nS1;C4;Klassifikasjon;Donald;\nS2;C2;Klassifikasjon;Donald;\nS2;C3;Klassifikasjon;Donald;\n",
    //                result.exportToExcel(';'));
    //    }
    //
    //    private Page<StatisticalEntity> makeTestData() {
    //        AccessCounterDto data1 = new AccessCounterDto("A", 1L);
    //        AccessCounterDto data2 = new AccessCounterDto("B", 2L);
    //        AccessCounterDto data3 = new AccessCounterDto("C", 3L);
    //        List<StatisticalEntity> dataList = new ArrayList<>();
    //        dataList.add(data1);
    //        dataList.add(data2);
    //        dataList.add(data3);
    //        return new PageImpl<>(dataList);
    //    }
    //
    //    private void checkResult(UsageStatisticsData result) {
    //        result.reset();
    //        String resultAsString = result.exportToExcel(';');
    //        assertEquals("A;1;\nB;2;\nC;3;\n", resultAsString);
    //    }
    //
    //    public static LocalDate getFromDate() {
    //        return getDate(2);
    //    }
    //
    //    public static LocalDate getDate(int diff) {
    //        LocalDate from = LocalDate.now();
    //        from.minusDays(diff);
    //        return from;
    //    }
    //
    //    public static LocalDate getToDate() {
    //        return LocalDate.now();
    //    }
    //
    //    private void checkClassificationReportData(ClassificationReportData<?> result, String
    // section, String name) {
    //        result.reset();
    //        assertEquals(section + '1', result.getSection());
    //        assertEquals(name + "1_NO", result.getName());
    //        assertEquals(ClassificationType.CLASSIFICATION.getDisplayName(Language.NB),
    // result.getType());
    //        assertEquals("Donald", result.getContactPerson());
    //        result.nextPost();
    //        assertEquals(section + '1', result.getSection());
    //        assertEquals(name + "2_NO", result.getName());
    //        assertEquals(ClassificationType.CLASSIFICATION.getDisplayName(Language.NB),
    // result.getType());
    //        assertEquals("Skrue", result.getContactPerson());
    //        result.nextPost();
    //        assertTrue(result.checkAndSetNextSection());
    //        assertEquals(section + '2', result.getSection());
    //        assertEquals(name + "3_NO", result.getName());
    //        assertEquals(ClassificationType.CLASSIFICATION.getDisplayName(Language.NB),
    // result.getType());
    //        assertEquals("Skrue", result.getContactPerson());
    //
    //    }
    //
    //    private void checkClassificationReportDataNotPub(ClassificationReportData<?> result,
    // String
    // section, String name) {
    //        result.reset();
    //        assertEquals(section + '1', result.getSection());
    //        assertEquals(name + "2_NO", result.getName());
    //        assertEquals(ClassificationType.CLASSIFICATION.getDisplayName(Language.NB),
    // result.getType());
    //        assertEquals("Skrue", result.getContactPerson());
    //    }
    //
    //    private void checkVersionReportData(VersionReportData result, String section) {
    //        result.reset();
    //        assertEquals(section + '1', result.getSection());
    //        assertEquals("B", result.getName());
    //        assertEquals(ClassificationType.CLASSIFICATION.getDisplayName(Language.NB),
    // result.getType());
    //        assertEquals("Skrue", result.getContactPerson());
    //        assertEquals("Nei", result.isPublishedNo());
    //        assertEquals("Ja", result.isPublishedNn());
    //        assertEquals("Nei", result.isPublishedEn());
    //        result.nextPost();
    //        assertTrue(result.checkAndSetNextSection());
    //        assertEquals(section + '2', result.getSection());
    //        assertEquals("C", result.getName());
    //        assertEquals(ClassificationType.CLASSIFICATION.getDisplayName(Language.NB),
    // result.getType());
    //        assertEquals("Skrue", result.getContactPerson());
    //        assertEquals("Ja", result.isPublishedNo());
    //        assertEquals("Nei", result.isPublishedNn());
    //        assertEquals("Ja", result.isPublishedEn());
    //
    //    }
    //
    //    private List<ClassificationReportDto> makeClassificationData(String section, String name)
    // {
    //        List<ClassificationReportDto> result = new ArrayList<>(3);
    //        result.add(new ClassificationReportDto(mockClassificationSeries(2L, name + "2_NO"),
    // mockUser(section + '1',
    //                "Skrue")));
    //        result.add(new ClassificationReportDto(mockClassificationSeries(1L, name + "1_NO"),
    // mockUser(section + '1',
    //                "Donald")));
    //        result.add(new ClassificationReportDto(mockClassificationSeries(3L, name + "3_NO"),
    // mockUser(section + '2',
    //                "Skrue")));
    //        return result;
    //    }
    //
    //    private List<ClassificationReportDto> makeClassificationData13(String section, String
    // name)
    // {
    //        List<ClassificationReportDto> result = new ArrayList<>(3);
    //        result.add(new ClassificationReportDto(mockClassificationSeries(1L, name + "1_NO"),
    // mockUser(section + '1',
    //                "Donald")));
    //        result.add(new ClassificationReportDto(mockClassificationSeries(3L, name + "3_NO"),
    // mockUser(section + '2',
    //                "Skrue")));
    //        return result;
    //    }
    //
    //    private List<ClassificationVersionReportDto> makeClassificationVersionData(String section,
    // String name) {
    //        List<ClassificationVersionReportDto> result = new ArrayList<>(3);
    //        result.add(new ClassificationVersionReportDto(mockClassificationVersion("A", true,
    // true,
    // true),
    //                mockClassificationSeries(1L, name + "1_NO"), mockUser(section + '1',
    // "Donald")));
    //        result.add(new ClassificationVersionReportDto(mockClassificationVersion("B", false,
    // true, false),
    //                mockClassificationSeries(2L, name + "2_NO"), mockUser(section + '1',
    // "Skrue")));
    //        result.add(new ClassificationVersionReportDto(mockClassificationVersion("C", true,
    // false, true),
    //                mockClassificationSeries(3L, name + "3_NO"), mockUser(section + '2',
    // "Skrue")));
    //        return result;
    //    }
    //
    //    private List<ClassificationVersionReportDto> makeClassificationVersionData1(String
    // section,
    // String name) {
    //        List<ClassificationVersionReportDto> result = new ArrayList<>(3);
    //        result.add(new ClassificationVersionReportDto(mockClassificationVersion("A", true,
    // true,
    // true),
    //                mockClassificationSeries(1L, name + "1_NO"), mockUser(section + '1',
    // "Donald")));
    //        return result;
    //    }
    //
    //    private ClassificationSeries mockClassificationSeries(Long id, String name) {
    //        ClassificationSeries classification = mock(ClassificationSeries.class);
    //        when(classification.getId()).thenReturn(id);
    //        when(classification.getNameInPrimaryLanguage()).thenReturn(name);
    //
    // when(classification.getClassificationType()).thenReturn(ClassificationType.CLASSIFICATION);
    //        return classification;
    //    }
    //
    //    private ClassificationVersion mockClassificationVersion(String name, boolean pubNo,
    // boolean
    // pubNn, boolean pubEn) {
    //        ClassificationVersion ver = mock(ClassificationVersion.class);
    //        when(ver.getNameInPrimaryLanguage()).thenReturn(name);
    //        when(ver.isPublished(eq(Language.NB))).thenReturn(pubNo);
    //        when(ver.isPublished(eq(Language.NN))).thenReturn(pubNn);
    //        when(ver.isPublished(eq(Language.EN))).thenReturn(pubEn);
    //        return ver;
    //    }
    //
    //    private User mockUser(String section, String name) {
    //        User user = mock(User.class);
    //        when(user.getSection()).thenReturn(section);
    //        when(user.getFullname()).thenReturn(name);
    //        return user;
    //    }
}
