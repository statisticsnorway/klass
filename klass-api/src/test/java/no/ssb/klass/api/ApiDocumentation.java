package no.ssb.klass.api;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import no.ssb.klass.api.config.MockConfig;
import no.ssb.klass.api.config.TestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.SolrResultPage;
import org.springframework.http.MediaType;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.generate.RestDocumentationGenerator;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import no.ssb.klass.solr.config.ConfigurationProfiles;
import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationItem;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceMap;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.Level;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.service.search.SolrSearchResult;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.core.util.Translatable;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.TestUtil;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { TestConfig.class, MockConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles(ConfigurationProfiles.H2_INMEMORY)
public class ApiDocumentation {
    private static final int CLASS_ID_FAMILIEGRUPPERING = 17;
    private static final int CLASS_ID_GREENHOUSE_GASES = 84;
    private static final long CLASS_FAMILY_BEFOLKNING = 3L;
    private static final long CLASS_VARIANT_POLITIDISTRIKT = 2L;
    private static final long CLASS_VARIANT_KOMMUNEINNDELING = 7L;
    private static final int CLASS_ID_BYDELSINNDELING = 103;
    private static final int CLASS_ID_KOMMUNEINNDELING = 131;
    @Rule
    public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");
    private RestDocumentationResultHandler documentationHandler;
    @Autowired
    private WebApplicationContext context;
    @Autowired
    private ClassificationService classificationServiceMock;
    @Autowired
    private SearchService searchServiceMock;

    private MockMvc mockMvc;

    private String server = "data.ssb.no";
    private int port = 80;
    @Value("${klass.env.api.path}")
    private String contextPath;

    @Before
    public void setup() {
        this.documentationHandler = document("{method-name}", preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));
        // FIXME: preprocessResponse prettyPrint breaks CSVs since 2017
        // https://docs.spring.io/spring-restdocs/docs/1.2.6.RELEASE/reference/html5/#customizing-requests-and-responses-preprocessors-pretty-print

        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
                .apply(documentationConfiguration(this.restDocumentation).uris().withHost(server).withPort(port))
                .alwaysDo(this.documentationHandler).build();
    }

    @After
    public void teardown() {
        reset(classificationServiceMock);
    }

    @Test
    public void errorExample() throws Exception {
        when(classificationServiceMock.getClassificationSeries(anyObject())).thenThrow(
                new KlassResourceNotFoundException("Classification not found with id = 99999"));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/99999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Classification not found with id = 99999"));
        // @formatter:on
    }

    @Test
    public void classificationFamiliesExample() throws Exception {
        when(classificationServiceMock.findPublicClassificationFamilySummaries(any(String.class), any(
                ClassificationType.class))).thenReturn(Lists.newArrayList(new ClassificationFamilySummary(3L,
                Translatable.create("Befolkning", Language.getDefault()),
                "iconName", 1)));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classificationfamilies").accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.classificationFamilies").description("An array of ClassificationFamilies"),
                                fieldWithPath("_embedded.classificationFamilies[].name").description("ClassificationFamily name"),
                                fieldWithPath("_embedded.classificationFamilies[].numberOfClassifications")
                                        .description("Number of classifications belonging to classificationFamily"),
                                fieldWithPath("_embedded.classificationFamilies[]._links").description("Link to classificationFamily"),
                                fieldWithPath("_links").description("<<classification-families-links,Links>> to other resources"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void classificationFamiliesOptionalParametersExample() throws Exception {
        when(classificationServiceMock.findAllClassificationFamilySummaries(any(String.class), any(
                ClassificationType.class))).thenReturn(new ArrayList<>());
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classificationfamilies?ssbSection=714&includeCodelists=true&language=nb")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                ssbSectionParameterDescription("counting number of"),
                                includeCodelistsParameterDescription("counting number of"),
                                languageDescription())))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void classificationFamilyExample() throws Exception {
        ClassificationFamily family = createClassificationFamily();
        when(classificationServiceMock.getClassificationFamily(any(Long.class))).thenReturn(family);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classificationfamilies/" + CLASS_FAMILY_BEFOLKNING).accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request")),
                        relaxedResponseFields(
                                fieldWithPath("name").description("ClassificationFamily name"),
                                fieldWithPath("classifications").description("Array of classifications"),
                                fieldWithPath("_links").description("<<classification-family-links,Links>> to operations on classificationFamily"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void classificationFamilyOptionalParametersExample() throws Exception {
        ClassificationFamily family = createClassificationFamily();
        when(classificationServiceMock.getClassificationFamily(any(Long.class))).thenReturn(family);
        // @formatter:off        
        this.mockMvc.perform(getWithContext("/classificationfamilies/" + CLASS_FAMILY_BEFOLKNING + "?ssbSection=714&includeCodelists=true&language=nb")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                ssbSectionParameterDescription("listing"),
                                includeCodelistsParameterDescription("listing"),
                                languageDescription())))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void ssbSectionsExample() throws Exception {
        when(classificationServiceMock.findResponsibleSectionsWithPublishedVersions()).thenReturn(Sets.newHashSet(
                "Seksjon for primærnæringsstatistikk (420)"));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/ssbsections").accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request")),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.ssbSections").description("Array of ssb sections"),
                                fieldWithPath("_embedded.ssbSections[].name").description("Name of ssb section"),
                                fieldWithPath("_links").description("<<ssb-sections-links,Links>> to operations on ssb sections"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void classificationsExample() throws Exception {
        List<ClassificationSeries> classifications = Lists.newArrayList(createClassificationKommuneinndeling(),
                createClassificationBydelsinndeling(), createClassificationFamiliegruppering(TestUtil.createUser()));
        when(classificationServiceMock.findAllPublic(anyBoolean(), any(Date.class), any(Pageable.class))).then(
                i -> createPage(i.getArgumentAt(2, Pageable.class), classifications));
        // @formatter:off        
        this.mockMvc.perform(getWithContext("/classifications").accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(links(
                                halLinks(),
                                linkWithRel("self").description("The current request"),
                                linkWithRel("search").description("Link to search for classifications")),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.classifications").description("An array of Classifications"),
                                fieldWithPath("_embedded.classifications[].name").description("Classification name"),
                                fieldWithPath("_embedded.classifications[].lastModified").description("Last modification time of classification"),
                                fieldWithPath("_embedded.classifications[]._links").description("Link to classification"),
                                fieldWithPath("_links").description("<<classifications-links,Links>> to other resources"),
                                fieldWithPath("page").description("Describes number of classifications returned, see <<_page, page>>"))))
                .andExpect(status().isOk());
        // @formatter:on        
    }

    @Test
    public void classificationsOptionalParametersExample() throws Exception {
        when(classificationServiceMock.findAllPublic(anyBoolean(), any(Date.class), any(Pageable.class))).then(
                i -> createPage(i.getArgumentAt(2, Pageable.class), new ArrayList<>()));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications?includeCodelists=true&changedSince=2015-01-01T00:00:00.000-0000")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                includeCodelistsDescription(),
                                changedSinceDescription())))
                .andExpect(status().isOk());
        // @formatter:on        
    }

    @Test
    public void searchExample() throws Exception {
        when(searchServiceMock.publicSearch(any(String.class), any(Pageable.class), any(String.class), anyBoolean()))
                .then(i -> createSearchPage(i.getArgumentAt(2, Pageable.class)));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/search?query=kommuner").accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current search")),
                        relaxedResponseFields(
                                fieldWithPath("_embedded.searchResults").description("An array of search results"),
                                fieldWithPath("_embedded.searchResults[].snippet").description("A line containing match for the words searched"),
                                fieldWithPath("_embedded.searchResults[].searchScore")
                                        .description("Represents this classifications relevans for the search"),
                                fieldWithPath("_embedded.searchResults[]._links").description("Link to classification that matched search"),
                                fieldWithPath("_links").description("<<search-links,Links>> to other resources"),
                                fieldWithPath("page").description("Describes number of classifications returned, see <<_page, page>>"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void searchOptionalParametersExample() throws Exception {
        when(searchServiceMock.publicSearch(any(String.class), any(Pageable.class), any(String.class), anyBoolean()))
                .then(i -> createSearchPage(i.getArgumentAt(1, Pageable.class)));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/search?query=kommuner&includeCodelists=true&ssbSection=370")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                parameterWithName("query").description("[Mandatory] specifies search terms"),
                                includeCodelistsDescription(),
                                ssbSectionParameterDescription("searching"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void classificationExample() throws Exception {
        when(classificationServiceMock.getClassificationSeries(anyObject())).thenReturn(
                createClassificationKommuneinndeling());
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING).accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request"),
                                linkWithRel("codes").description("Used for getting codes from the classification, see <<_codes, codes>>"),
                                linkWithRel("codesAt")
                                        .description("Used for getting codes from the classification valid at a specific date, see <<_codesat, codesAt>>"),
                                linkWithRel("variant").description("Used for getting codes from a classification variant, see <<_variant, variant>>"),
                                linkWithRel("variantAt")
                                        .description("Used for getting codes from a variant of the classification valid at a specific date, "
                                                + "see <<_variantat, variantAt>>"),
                                linkWithRel("corresponds")
                                        .description("Used for getting mappings between two classifications, see <<_corresponds, corresponds>>"),
                                linkWithRel("correspondsAt")
                                        .description("Used for getting mappings between two classifications at a specific date, "
                                                + "see <<_correspondsat, correspondsAt>>"),
                                linkWithRel("changes").description("Used for getting changes in codes, see <<_changes, changes>>")),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Classification name"),
                                fieldWithPath("description").description("Description of classification"),
                                fieldWithPath("primaryLanguage").description("Primary language for classification"),
                                fieldWithPath("classificationType").description("Type of classification, Classification or Codelist"),
                                fieldWithPath("copyrighted").description("If true, classification is copyrighted"),
                                fieldWithPath("includeShortName").description("If true, indicates that classificationItems may have shortnames"),
                                fieldWithPath("includeNotes").description("If true, indicates that classificationItems may have notes"),
                                fieldWithPath("contactPerson").description("Contact person for classification"),
                                fieldWithPath("owningSection").description("Owning SSB section"),
                                fieldWithPath("statisticalUnits").description("Statistical units assigned to classification"),
                                fieldWithPath("lastModified").description("Last time classification has been modified"),
                                fieldWithPath("versions").description("Array of classification versions"),
                                fieldWithPath("_links").description("<<classification-links,Links>> to operations on classification"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void classificationOptionalParametersExample() throws Exception {
        when(classificationServiceMock.getClassificationSeries(anyObject())).thenReturn(
                createClassificationKommuneinndeling());
        // @formatter:off

        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING + "?language=nb&includeFuture=true")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(languageDescription(),includeFutureDescription(""))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void versionsExampleJson() throws Exception {
        ClassificationVersion version = createClassificationKommuneinndeling().getClassificationVersions().get(1);
        when(classificationServiceMock.getClassificationVersion(any(Long.class))).thenReturn(version);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/versions/" + CLASS_ID_KOMMUNEINNDELING).accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request")),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Version name"),
                                fieldWithPath("validFrom").description("Date the version is valid from"),
                                fieldWithPath("validTo").description("Date the version is valid to"),
                                fieldWithPath("lastModified").description("Time and date the version was last time modified at"),
                                fieldWithPath("published").description("List of languages the version is published in"),
                                fieldWithPath("introduction").description("Version description"),
                                fieldWithPath("legalBase").description("Legal base"),
                                fieldWithPath("publications").description("Source references"),
                                fieldWithPath("derivedFrom").description("Derived from"),
                                fieldWithPath("correspondenceTables").description("List of correspondence tables for the version"),
                                fieldWithPath("classificationVariants").description("List of classification variants for the version"),
                                fieldWithPath("changelogs").description("Log og changes"),
                                fieldWithPath("levels").description("List of levels"),
                                fieldWithPath("classificationItems").description("Array of classification items"),
                                fieldWithPath("_links").description("Links to operations on the version"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void versionsRequestParametersExample() throws Exception {
        ClassificationVersion version = createClassificationKommuneinndeling().getClassificationVersions().get(1);
        when(classificationServiceMock.getClassificationVersion(any(Long.class))).thenReturn(version);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/versions/" + CLASS_ID_KOMMUNEINNDELING + "?language=nb&includeFuture=true")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(languageDescription(),includeFutureDescription(""))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void versionsExampleCsv() throws Exception {
        ClassificationVersion version = createClassificationKommuneinndeling().getClassificationVersions().get(1);
        when(classificationServiceMock.getClassificationVersion(any(Long.class))).thenReturn(version);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/versions/" + CLASS_ID_KOMMUNEINNDELING)
                .header("Accept", "text/csv; charset=ISO-8859-1"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void codesExampleJson() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING + "/codes?from=2014-01-01&to=2015-01-01&csvSeparator=;")
                .header("Accept", MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void codesExampleCsv() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING + "/codes?from=2014-01-01&to=2015-01-01&csvSeparator=;")
                .header("Accept", "text/csv; charset=ISO-8859-1"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void codesOptionalParametersExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContextUri("/classifications/" + CLASS_ID_KOMMUNEINNDELING + "/codes?from=2014-01-01&to=2015-01-01&csvSeparator=;&csvFields=name,code"
                + "&selectLevel=1&selectCodes=01*&presentationNamePattern={code}-{name}&language=nb&includeFuture=true")
                .accept("text/csv"))
                .andDo(this.documentationHandler
                        .document(
                        requestParameters(
                                fromParameterDescription(),
                                toParameterDescription(),
                                csvSeparatorParameterDescription(),
                                csvFieldsParameterDescription(),
                                selectCodesParameterDescription(),
                                selectLevelParameterDescription(),
                                presentationNamePatternParameterDescription(),
                                languageDescription(),
                                includeFutureDescription("codes from the"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void codesAtExampleCsv() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", "2016-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING + "/codesAt?date=2015-01-01")
                .header("Accept", "text/csv; charset=ISO-8859-1"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void codesAtExampleJson() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", "2016-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING + "/codesAt?date=2015-01-01")
                .header("Accept", MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void codesAtOptionalParametersExample() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", "2016-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContextUri("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                + "/codesAt?date=2015-01-01&csvSeparator=;&csvFields=name,code&selectLevel=1&selectCodes=01*"
                + "&presentationNamePattern={code}-{name}&language=nb&includeFuture=true").accept("text/csv"))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                dateParameterDescription(),
                                csvSeparatorParameterDescription(),
                                csvFieldsParameterDescription(),
                                selectCodesParameterDescription(),
                                selectLevelParameterDescription(),
                                presentationNamePatternParameterDescription(),
                                languageDescription(),
                                includeFutureDescription("variants of the"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantExampleCsv() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        List<CodeDto> codes = createGreenhouseGasesCodes(dateRange);
        when(classificationServiceMock.findVariantClassificationCodes(any(), any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_GREENHOUSE_GASES
                        + "/variant?variantName=Klimagasser&from=2014-01-01&to=2015-01-01")
                        .header("Accept", "text/csv; charset=UTF-8"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantExampleJson() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        List<CodeDto> codes = createGreenhouseGasesCodes(dateRange);
        when(classificationServiceMock.findVariantClassificationCodes(any(), any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_GREENHOUSE_GASES
                        + "/variant?variantName=Klimagasser&from=2014-01-01&to=2015-01-01")
                        .header("Accept", MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantOptionalParametersExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findVariantClassificationCodes(any(), any(), any(), any(), any())).thenReturn(
                createFamilieInndelingCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContextUri("/classifications/" + CLASS_ID_GREENHOUSE_GASES
                        + "/variant?variantName=Klimagasser"
                        + "&from=2014-01-01&to=2015-01-01&csvSeparator=;&csvFields=name,code&selectLevel=1&selectCodes=01*"
                        + "&presentationNamePattern={code}-{name}&language=nb&includeFuture=true")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                variantNameParameterDescription(),
                                fromParameterDescription(),
                                toParameterDescription(),
                                csvSeparatorParameterDescription(),
                                csvFieldsParameterDescription(),
                                selectCodesParameterDescription(),
                                selectLevelParameterDescription(),
                                presentationNamePatternParameterDescription(),
                                languageDescription(),
                                includeFutureDescription("variants of the"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantAtExampleCsv() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", null);
        when(classificationServiceMock.findVariantClassificationCodes(any(), any(), any(), any(), any())).thenReturn(
                createGreenhouseGasesCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_GREENHOUSE_GASES
                        + "/variantAt?variantName=Klimagasser&date=2015-01-01")
                        .header("Accept", "text/csv; charset=ISO-8859-1"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantAtExampleJson() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", null);
        when(classificationServiceMock.findVariantClassificationCodes(any(), any(), any(), any(), any())).thenReturn(
                createGreenhouseGasesCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_GREENHOUSE_GASES
                        + "/variantAt?variantName=Klimagasser&date=2015-01-01")
                        .header("Accept", MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantAtOptionalParametersExample() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", null);
        when(classificationServiceMock.findVariantClassificationCodes(any(), any(), any(), any(), any()))
                .thenReturn(createGreenhouseGasesCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContextUri("/classifications/" + CLASS_ID_GREENHOUSE_GASES
                        + "/variantAt?variantName=Klimagasser"
                        + "&date=2015-01-01&csvSeparator=;&csvFields=name,code&selectLevel=1&selectCodes=01*"
                        + "&presentationNamePattern={code}-{name}&language=nb&includeFuture=true").accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                variantNameParameterDescription(),
                                dateParameterDescription(),
                                csvSeparatorParameterDescription(),
                                csvFieldsParameterDescription(),
                                selectCodesParameterDescription(),
                                selectLevelParameterDescription(),
                                presentationNamePatternParameterDescription(),
                                languageDescription(),
                                includeFutureDescription("variants of the"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantsExampleJson() throws Exception {
        ClassificationVariant variant = createClassificationFamiliegruppering(TestUtil.createUser())
                .getClassificationVersions().get(0).getClassificationVariants().get(0);
        when(classificationServiceMock.getClassificationVariant(any(Long.class))).thenReturn(variant);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/variants/" + 1111L).accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request")),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Variant name"),
                                fieldWithPath("contactPerson").description("Contact information to the SSB section representative"),
                                fieldWithPath("owningSection").description("SSB section number"),
                                fieldWithPath("lastModified").description("Time and date the variant was last time modified at"),
                                fieldWithPath("published").description("List of languages the version is published in"),
                                fieldWithPath("validFrom").description("Date the variant is valid from"),
                                fieldWithPath("validTo").description("Date the variant is valid to"),
                                fieldWithPath("introduction").description("Variant description"),
                                fieldWithPath("correspondenceTables").description("List of correspondence tables for the variant"),
                                fieldWithPath("changelogs").description("Log of changes"),
                                fieldWithPath("levels").description("List of levels"),
                                fieldWithPath("classificationItems").description("Array of classification items"),
                                fieldWithPath("_links").description("Links to operations on the variant"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantsRequestParametersExample() throws Exception {
        ClassificationVariant variant = createClassificationFamiliegruppering(TestUtil.createUser())
                .getClassificationVersions().get(0).getClassificationVariants().get(0);
        when(classificationServiceMock.getClassificationVariant(any(Long.class))).thenReturn(variant);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/variants/" + 1111L + "?language=nb")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(languageDescription())))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void variantsExampleCsv() throws Exception {
        ClassificationVariant variant = createClassificationFamiliegruppering(TestUtil.createUser())
                .getClassificationVersions().get(0).getClassificationVariants().get(0);
        when(classificationServiceMock.getClassificationVariant(any(Long.class))).thenReturn(variant);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/variants/" + 1111L)
                .header("Accept", "text/csv; charset=ISO-8859-1"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondsExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findCorrespondences(any(), any(), any(), any(), any())).thenReturn(
                createKommuneToBydelCorrespondences(dateRange));

        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/corresponds?targetClassificationId=" + CLASS_ID_BYDELSINNDELING
                        + "&from=2014-01-01&to=2015-01-01")
                        .header("Accept", "text/csv; charset=UTF-8"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondsOptionalParametersExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findCorrespondences(any(), any(), any(), any(), any())).thenReturn(
                createKommuneToBydelCorrespondences(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/corresponds?targetClassificationId=" + CLASS_ID_BYDELSINNDELING
                        + "&from=2014-01-01&to=2016-01-01&csvSeparator=;&csvFields=sourceCode,sourceName"
                        + "&language=nb&includeFuture=true").accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                targetClassificationIdParameterDescription(),
                                fromParameterDescription(),
                                toParameterDescription(),
                                csvFieldsParameterDescription(),
                                csvSeparatorParameterDescription(),
                                languageDescription(),
                                includeFutureDescription(""))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondsAtExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findCorrespondences(any(), any(), any(), any(), any())).thenReturn(
                createKommuneToBydelCorrespondences(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/correspondsAt?targetClassificationId=" + CLASS_ID_BYDELSINNDELING + "&date=2014-01-01")
                        .header("Accept", "text/csv; charset=UTF-8"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondsAtOptionalParametersExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findCorrespondences(any(), any(), any(), any(), any())).thenReturn(
                createKommuneToBydelCorrespondences(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/correspondsAt?targetClassificationId=" + CLASS_ID_BYDELSINNDELING + "&date=2016-01-01&csvSeparator=;&csvFields=sourceCode,sourceName"
                        + "&language=nb&includeFuture=true").accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                targetClassificationIdParameterDescription(),
                                dateParameterDescription(),
                                csvSeparatorParameterDescription(),
                                csvFieldsParameterDescription(),
                                languageDescription(),
                                includeFutureDescription(""))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondenceTablesExampleCsv() throws Exception {
        when(classificationServiceMock.getCorrespondenceTable(any(Long.class))).thenReturn(createCorrespondenceTable(1L));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/correspondencetables/" + 1L)
                .header("Accept", "text/csv; charset=ISO-8859-1"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondenceTablesExampleJson() throws Exception {
        when(classificationServiceMock.getCorrespondenceTable(any(Long.class))).thenReturn(createCorrespondenceTable(1L));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/correspondencetables/" + 1L).accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request"),
                                linkWithRel("source").description("Table's source classification version"),
                                linkWithRel("target").description("Table's target classification version")),
                        relaxedResponseFields(
                                fieldWithPath("name").description("Correspondence table's name"),
                                fieldWithPath("contactPerson").description("Contact information to the SSB section representative"),
                                fieldWithPath("owningSection").description("SSB section number"),
                                fieldWithPath("source").description("Name to the source classification version"),
                                fieldWithPath("sourceId").description("ID to the source classification version"),
                                fieldWithPath("target").description("Name to the target classification version"),
                                fieldWithPath("targetId").description("ID to the target classification version"),
                                fieldWithPath("changeTable").description("Changes done to the table"),
                                fieldWithPath("lastModified").description("Timestamp for the last modifications done to the table"),
                                fieldWithPath("published").description("A list of the languages the correspondence table is published in"),
                                fieldWithPath("sourceLevel").description("Level name and number for the source classification version"),
                                fieldWithPath("targetLevel").description("Level name and number for the target classification version"),
                                fieldWithPath("description").description("Description"),
                                fieldWithPath("changelogs").description("A list of the changes done to the correspondence table"),
                                fieldWithPath("correspondenceMaps").description("A list of the mappings the correspondence table includes"),
                                fieldWithPath("_links").description("<<correspondence-tables-links,links>> to operations on classificationFamily"))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void correspondenceTablesOptionalParametersExample() throws Exception {
        when(classificationServiceMock.getCorrespondenceTable(any(Long.class))).thenReturn(createCorrespondenceTable(1L));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/correspondencetables/" + 1L + "?language=nb")
                .accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        requestParameters(languageDescription())))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void changesExample() throws Exception {
        when(classificationServiceMock.getClassificationSeries(anyObject())).thenReturn(
                createClassificationKommuneinndeling());
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/changes?from=2014-01-01&to=2015-01-01").accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void changesOptionalParametersExample() throws Exception {
        when(classificationServiceMock.getClassificationSeries(anyObject())).thenReturn(
                createClassificationKommuneinndeling());
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/changes?from=2014-01-01&to=2016-01-01&csvSeparator=;&language=nb&includeFuture=true")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andDo(this.documentationHandler.document(
                        requestParameters(
                                fromParameterDescription(),
                                toParameterDescription(),
                                csvSeparatorParameterDescription(),
                                languageDescription(),
                                includeFutureDescription(""))))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void csvSeparatorExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(
                createKommuneInndelingCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2014-01-01&to=2015-01-01&csvSeparator=;")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }
    @Test
    public void csvFieldsCodesExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(
                createKommuneInndelingCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2014-01-01&to=2015-01-01&csvSeparator=;&csvFields=name,code")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }
    @Test
    public void csvFieldsCodesAtExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(
                createKommuneInndelingCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codesAt?date=2014-01-01&csvSeparator=;&csvFields=name,code")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }
    @Test
    public void selectLevelExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(
                createFamilieInndelingCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_FAMILIEGRUPPERING
                + "/codes?from=2014-01-01&to=2015-01-01&selectLevel=2").accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void presentationNamePatternExample() throws Exception {
        DateRange dateRange = DateRange.create("2014-01-01", "2015-01-01");
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(
                createKommuneInndelingCodes(dateRange));
        // @formatter:off
        this.mockMvc.perform(
                getWithContextUri("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2014-01-01&to=2015-01-01&presentationNamePattern={code}-{uppercase(name)}")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void changedSinceExample() throws Exception {
        List<ClassificationSeries> classifications = Lists.newArrayList(createClassificationKommuneinndeling(),
                createClassificationBydelsinndeling(), createClassificationFamiliegruppering(TestUtil.createUser()));

        when(classificationServiceMock.findAllPublic(anyBoolean(), any(Date.class), any(Pageable.class))).then(
                i -> createPage(i.getArgumentAt(2, Pageable.class), classifications));

        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications?changedSince=2015-03-01T01:30:00.000-0200")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void rangeExample() throws Exception {
        DateRange dateRange = DateRange.create("2013-01-01", "2014-01-01");
        List<CodeDto> codes = new ArrayList<>();
        codes.add(createCode(1, "0101", "Halden", dateRange));
        codes.add(createCode(1, "0104", "Moss", dateRange));
        codes.add(createCode(1, "1739", "Røyrvik", dateRange));
        codes.add(createCode(1, "1939", "Storfjord", dateRange));

        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                + "/codes?from=2013-01-01&to=2014-01-01").accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void rangeExtendedExample() throws Exception {
        DateRange dateRange = DateRange.create("2013-01-01", "2015-01-01");
        DateRange startRange = DateRange.create("2013-01-01", "2014-01-01");
        DateRange endRange = DateRange.create("2014-01-01", "2015-01-01");
        List<CodeDto> codes = new ArrayList<>();
        codes.add(createCode(1, "0101", "Halden", dateRange));
        codes.add(createCode(1, "0104", "Moss", dateRange));
        codes.add(createCode(1, "1739", "Røyrvik", startRange));
        codes.add(createCode(1, "1739", "Raarvihke Røyrvik", endRange));
        codes.add(createCode(1, "1939", "Storfjord", startRange));
        codes.add(createCode(1, "1939", "Omasvuotna Storfjord Omasvuonon", endRange));

        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2013-01-01&to=2015-01-01")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void selectCodesExampleCsv() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", "2016-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        codes.add(createCode(1, "0301", "Oslo", dateRange));
        codes.add(createCode(1, "0304", "Oslo", dateRange));
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2015-01-01&to=2016-01-01&selectCodes=0301-0305,01*")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void selectCodesExampleJson() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", "2016-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        codes.add(createCode(1, "0301", "Oslo", dateRange));
        codes.add(createCode(1, "0304", "Oslo", dateRange));
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2015-01-01&to=2016-01-01&selectCodes=0301-0305,01*")
                        .accept(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void languageExample() throws Exception {
        DateRange dateRange = DateRange.create("2015-01-01", "2016-01-01");
        List<CodeDto> codes = createKommuneInndelingCodes(dateRange);
        when(classificationServiceMock.findClassificationCodes(any(), any(), any(), any())).thenReturn(codes);
        // @formatter:off
        this.mockMvc.perform(
                getWithContext("/classifications/" + CLASS_ID_KOMMUNEINNDELING
                        + "/codes?from=2015-01-01&to=2016-01-01&language=nb")
                        .accept("text/csv"))
                .andDo(this.documentationHandler = document("{method-name}",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(/*prettyPrint()*/)))
                .andExpect(status().isOk());
        // @formatter:on
    }

    @Test
    public void pageExample() throws Exception {
        List<ClassificationSeries> classifications = Lists.newArrayList(createClassificationKommuneinndeling(),
                createClassificationBydelsinndeling(), createClassificationFamiliegruppering(TestUtil.createUser()));
        when(classificationServiceMock.findAllPublic(any(Boolean.class), any(Date.class), any(Pageable.class))).then(
                i -> createPage(i.getArgumentAt(2, Pageable.class), classifications));

        // @formatter:off
        this.mockMvc.perform(getWithContext("/classifications?size=2").accept(MediaType.APPLICATION_JSON))
                .andDo(this.documentationHandler.document(
                        links(
                                halLinks(),
                                linkWithRel("self").description("The current request"),
                                linkWithRel("search").description("Search"),
                                linkWithRel("first").description("First page"),
                                linkWithRel("next").description("Next page"),
                                linkWithRel("last").description("Last page")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("_embedded").ignored(),
                                fieldWithPath("_links").ignored(),
                                fieldWithPath("page.size").description("Size of a page (number of elements per page)"),
                                fieldWithPath("page.totalElements").description("Total number of elements in collection"),
                                fieldWithPath("page.totalPages").description("Total number of pages"),
                                fieldWithPath("page.number").description("Page number")
                        )))
                .andExpect(status().isOk());
        // @formatter:on
    }

    private ParameterDescriptor includeCodelistsDescription() {
        return parameterWithName("includeCodelists").description(
                "[Optional] include ssb codelists. Default is false.");
    }

    private ParameterDescriptor languageDescription() {
        return parameterWithName("language").description(
                "[Optional] specifies language of retrieved data. Default is nb (Norwegian Bokmål). For details see <<_language, language>>");
    }

    private ParameterDescriptor changedSinceDescription() {
        return parameterWithName("changedSince").description(
                "[Optional] specifies that only classifications that have been changed since changedSince shall be included in the response." +
                        " For details see  <<_changedsince, changedSince>>");
    }

    private ParameterDescriptor fromParameterDescription() {
        return parameterWithName("from").description(
                "[Mandatory] specifies beginning of range with format `<yyyy-MM-dd>`. For details see <<_range, range>>");
    }

    private ParameterDescriptor toParameterDescription() {
        return parameterWithName("to").description("[Optional] specifies end of range with format `<yyyy-MM-dd>`. "
                + "If not set means that to is indefinite. For details see <<_range, range>>");
    }

    private ParameterDescriptor dateParameterDescription() {
        return parameterWithName("date").description(
                "[Mandatory] specifies codes at a certain date with format `<yyyy-MM-dd>`.");
    }

    private ParameterDescriptor targetClassificationIdParameterDescription() {
        return parameterWithName("targetClassificationId").description(
                "[Mandatory] specifies id of target classification");
    }

    private ParameterDescriptor variantNameParameterDescription() {
        return parameterWithName("variantName").description("[Mandatory] specifies name of classification variant\n\n"
                + "NOTE: If you want to look up codes from variants across multiple versions do not include the year "
                + "(or year month) and do not include everything after that\n\n"
                + "Example for `Particulate matter 2009  - Variant of substances emitted to air 2009` would be `Particulate matter`");
    }

    private ParameterDescriptor csvSeparatorParameterDescription() {
        return parameterWithName("csvSeparator").description(
                "[Optional] specifies separator to be used for csv format. For details see <<_csvseparator, csvSeparator>>");
    }
    private ParameterDescriptor csvFieldsParameterDescription() {
        return parameterWithName("csvFields").description(
                "[Optional] specifies which fields should be included in the csv output. For details see <<_csvfields, csvfields>>");
    }

    private ParameterDescriptor selectCodesParameterDescription() {
        return parameterWithName("selectCodes").description(
                "[Optional] only return codes that match pattern given by selectCodes. For details see <<_selectcodes, selectCodes>>");
    }

    private ParameterDescriptor selectLevelParameterDescription() {
        return parameterWithName("selectLevel").description(
                "[Optional] only return codes with matching level. For details see <<_selectlevel, selectLevel>>");
    }

    private ParameterDescriptor presentationNamePatternParameterDescription() {
        return parameterWithName("presentationNamePattern").description(
                "[Optional] used to build a presentationName. For details see <<_presentationnamepattern, presentationNamePattern>>");
    }

    private ParameterDescriptor ssbSectionParameterDescription(String part) {
        return parameterWithName("ssbSection").description(
                "[Optional] only include classifications belonging to specified SSB section when " + part
                        + " classifications. Default is all SSB sections");
    }

    private ParameterDescriptor includeCodelistsParameterDescription(String part) {
        return parameterWithName("includeCodelists").description("[Optional] include codelists when " + part
                + " classifications. Default is false");
    }

    private ParameterDescriptor includeFutureDescription(String part) {
        return parameterWithName("includeFuture").description(String.format(
                "[Optional] include %s future versions if available. Default is false.", part));
    }

    private URI toUri(String url) {
        return UriComponentsBuilder.fromUriString(url).build().encode().toUri();
    }

    public MockHttpServletRequestBuilder getWithContext(String urlTemplate,
                                                        Object... urlVariables) {
        return MockMvcRequestBuilders.get(contextPath + RestConstants.API_VERSION_V1 + urlTemplate, urlVariables)
                .requestAttr(RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE, urlTemplate).contextPath(contextPath);
    }

    public  MockHttpServletRequestBuilder getWithContextUri(String url) {
        return MockMvcRequestBuilders.get(toUri(contextPath + RestConstants.API_VERSION_V1 + url)).contextPath(contextPath);
    }

    private Page<SolrSearchResult> createSearchPage(Pageable pageable) {
        ClassificationSeries classification = createClassificationKommuneinndeling();
        SolrSearchResult searchResult = new SolrSearchResult();
        searchResult.setItemid(classification.getId());
        searchResult.setTitle(classification.getName(Language.getDefault()));
        searchResult.setLanguage(Language.getDefault().getLanguageCode());
        searchResult.setScore(8.0);
        searchResult.setType(ClassificationType.CLASSIFICATION.getDisplayName(Language.EN));
        searchResult.setDescription("Kommuneinndelingen er en administrativ inndeling av Norge");
        searchResult.setFamily("familie");


        SolrResultPage page = new SolrResultPage(Lists.newArrayList(searchResult), pageable, 1, 1.0f);
        HighlightEntry<SolrSearchResult> highlightResult = new HighlightEntry<>(searchResult);
        highlightResult.addSnipplets("Description", Lists.newArrayList(
                "Kommuneinndelingen er en administrativ inndeling av Norge"));
        page.setHighlighted(Lists.newArrayList(highlightResult));
        return page;
    }

    private Page<ClassificationSeries> createPage(Pageable pageable, List<ClassificationSeries> classifications) {
        int totalSize = classifications.size();
        if (pageable.getPageSize() < totalSize) {
            classifications = classifications.subList(0, pageable.getPageSize());
        }
        return new PageImpl<>(classifications, pageable, totalSize);
    }

    private List<CodeDto> createKommuneInndelingCodes(DateRange dateRange) {
        List<CodeDto> codes = new ArrayList<>();
        codes.add(createCode(1, "0101", "Halden", dateRange));
        codes.add(createCode(1, "0104", "Moss", dateRange));
        codes.add(createCode(1, "0105", "Sarpsborg", dateRange));
        codes.add(createCode(1, "0106", "Fredrikstad", dateRange));
        return codes;
    }

    private List<CodeDto> createFamilieInndelingCodes(DateRange dateRange) {
        List<CodeDto> codes = new ArrayList<>();
        codes.add(createCode(1, "A", "Enpersonfamilie", dateRange));
        codes.add(createCode(2, "A_", "Enpersonfamilie", dateRange));
        codes.add(createCode(1, "B", "Ektepar", dateRange));
        codes.add(createCode(2, "BA", "Ektepar med barn (yngste barn 0-17 år)", dateRange));
        codes.add(createCode(2, "BB", "Ektepar uten barn 0-17 år", dateRange));
        return codes;
    }

    private List<CodeDto> createGreenhouseGasesCodes(DateRange dateRange) {
        List<CodeDto> codes = new ArrayList<>();
        codes.add(createCode(1, "01", "CO2 - karbondioksid", dateRange));
        codes.add(createCode(1, "02", "CH4 - metan", dateRange));
        codes.add(createCode(1, "03", "N2O - lystgass", dateRange));
        codes.add(createCode(1, "04", "Fluorgasser", dateRange));
        codes.add(createCode(2, "04.1", "HFK - hydrofluorkarboner", dateRange));
        return codes;
    }

    private CodeDto createCode(int levelNumber, String code, String officialName, DateRange dateRange) {
        return new CodeDto(TestUtil.createLevel(levelNumber), TestUtil.createClassificationItem(code, officialName),
                dateRange, Language.getDefault());
    }

    private List<CorrespondenceDto> createKommuneToBydelCorrespondences(DateRange dateRange) {
        List<CorrespondenceDto> correspondences = new ArrayList<>();
        correspondences.add(createCorrespondence("0301", "Oslo", "030101", "Gamle Oslo", dateRange));
        correspondences.add(createCorrespondence("0301", "Oslo", "030103", "Sagene", dateRange));
        correspondences.add(createCorrespondence("0301", "Oslo", "030105", "Frogner", dateRange));
        correspondences.add(createCorrespondence("1201", "Bergen", "120101", "Arna", dateRange));
        correspondences.add(createCorrespondence("1201", "Bergen", "120102", "Bergenhus", dateRange));

        return correspondences;
    }

    private CorrespondenceDto createCorrespondence(String sourceCode, String sourceName, String targetCode,
                                                   String targetName, DateRange dateRange) {
        ClassificationItem source = TestUtil.createClassificationItem(sourceCode, sourceName);
        ClassificationItem target = TestUtil.createClassificationItem(targetCode, targetName);
        return new CorrespondenceDto(source, target, dateRange, Language.getDefault());
    }

    private ClassificationSeries createClassificationKommuneinndeling() {
        ClassificationSeries classification = TestUtil.createClassificationWithId(CLASS_ID_KOMMUNEINNDELING,
                "Standard for kommuneinndeling",
                "Kommuneinndelingen er en administrativ inndeling av kommuner i Norge");

        ClassificationVersion version2014 = TestUtil.createClassificationVersion(DateRange.create("2014-01-01", null));
        version2014.setId(1L);
        Level level = TestUtil.createLevel(1);
        version2014.addLevel(level);
        version2014.addClassificationItem(TestUtil.createClassificationItem("0101", "Halden"), level.getLevelNumber(),
                null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("0104", "Moss"), level.getLevelNumber(),
                null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("0301", "Oslo"), level.getLevelNumber(),
                null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("1739", "Raarvihke Røyrvik"), level
                .getLevelNumber(), null);
        version2014.addClassificationItem(TestUtil.createClassificationItem("1939", "Omasvuotna Storfjord Omasvuonon"),
                level.getLevelNumber(), null);
        classification.addClassificationVersion(version2014);

        ClassificationVersion version2012 = TestUtil.createClassificationVersion(DateRange.create("2012-01-01",
                "2014-01-01"));
        version2012.setId(1L);
        Level level2012 = TestUtil.createLevel(1);
        version2012.addLevel(level2012);
        version2012.addClassificationItem(TestUtil.createClassificationItem("0101", "Halden"), level2012
                .getLevelNumber(), null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("0104", "Moss"), level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("0301", "Oslo"), level2012.getLevelNumber(),
                null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("1739", "Røyrvik"), level2012
                .getLevelNumber(), null);
        version2012.addClassificationItem(TestUtil.createClassificationItem("1939", "Storfjord"), level2012
                .getLevelNumber(), null);

        classification.addClassificationVersion(version2012);

        CorrespondenceTable correspondenceTable = TestUtil.createCorrespondenceTable(version2014, version2012);
        version2014.addCorrespondenceTable(correspondenceTable);
        correspondenceTable.addCorrespondenceMap(new CorrespondenceMap(version2014.findItem("1739"), version2012
                .findItem("1739")));
        correspondenceTable.addCorrespondenceMap(new CorrespondenceMap(version2014.findItem("1939"), version2012
                .findItem("1939")));

        return classification;
    }

    private ClassificationSeries createClassificationBydelsinndeling() {
        ClassificationSeries classification = TestUtil.createClassificationWithId(CLASS_ID_BYDELSINNDELING,
                "Standard for bydelsinndeling",
                "Bydel utgjør geografiske områder i en kommune");

        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2004-01-01", null));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(TestUtil.createClassificationItem("030101", "Gamle Oslo"), level.getLevelNumber(),
                null);
        version.addClassificationItem(TestUtil.createClassificationItem("030102", "Grünerløkka"), level
                .getLevelNumber(), null);
        version.addClassificationItem(TestUtil.createClassificationItem("030103", "Sagene"), level.getLevelNumber(),
                null);
        version.addClassificationItem(TestUtil.createClassificationItem("030104", "St. Hanshaugen"), level
                .getLevelNumber(), null);
        version.addClassificationItem(TestUtil.createClassificationItem("030105", "Frogner"), level.getLevelNumber(),
                null);
        classification.addClassificationVersion(version);
        return classification;
    }

    private ClassificationSeries createClassificationFamiliegruppering(User user) {
        ClassificationSeries classification = TestUtil.createClassificationWithId(CLASS_ID_FAMILIEGRUPPERING,
                "Standard for gruppering av familier",
                "Standarden beskriver de ulike familitypene som i dag brukes i SSBs familistatistikk");
        classification.setContactPerson(user);
        ClassificationVersion version = TestUtil.createClassificationVersion(DateRange.create("2006-01-01", null));
        Level level = TestUtil.createLevel(1);
        version.addLevel(level);
        version.addClassificationItem(TestUtil.createClassificationItem("1.1.1", "Enpersonfamilie, person under 30 år"),
                level.getLevelNumber(), null);
        ClassificationVariant variant = TestUtil.createClassificationVariant("Variant - Tilleggsinndeling for familier",
                user);
        variant.setId(1111L);
        variant.addClassificationItem(TestUtil.createClassificationItem("A", "Enpersonfamilie"), 1, null);
        variant.addClassificationItem(TestUtil.createClassificationItem("B", "Ektepar"), 1, null);
        variant.addClassificationItem(TestUtil.createClassificationItem("A_", "Enpersonfamilie"), 2, variant.findItem(
                "A"));
        variant.addClassificationItem(TestUtil.createClassificationItem("BA", "Ektepar med barn (yngste barn 0-17 år)"),
                2, variant.findItem("B"));
        variant.addClassificationItem(TestUtil.createClassificationItem("BB", "Ektepar uten barn 0-17 år"), 2, variant
                .findItem("B"));
        version.addClassificationVariant(variant);
        classification.addClassificationVersion(version);
        return classification;
    }

    private ClassificationFamily createClassificationFamily() {
        ClassificationFamily family = TestUtil.createClassificationFamily("Befolkning");
        family.setId(CLASS_FAMILY_BEFOLKNING);
        family.addClassificationSeries(createClassificationKommuneinndeling());
        return family;
    }

    private CorrespondenceTable createCorrespondenceTable(Long id) {
        CorrespondenceTable table = TestUtil.createCorrespondenceTable("Mapping Arbeidsgiveravgiftssoner - Kommuneinndeling",
                TestUtil.createClassificationVersionWithTable(CLASS_VARIANT_POLITIDISTRIKT, TestUtil.anyDateRange(), "Arbeidsgiveravgiftssoner"),
                TestUtil.createClassificationVersionWithTable(CLASS_VARIANT_KOMMUNEINNDELING, TestUtil.anyDateRange(), "Kommuneinndeling"));
        table.setId(id);
        table.addCorrespondenceMap(new CorrespondenceMap(
                TestUtil.createClassificationItem("01", "Oslo"),
                TestUtil.createClassificationItem("0301", "Oslo")));
        return table;
    }
}
