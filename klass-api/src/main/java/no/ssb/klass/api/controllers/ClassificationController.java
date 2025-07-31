package no.ssb.klass.api.controllers;

import com.google.common.base.Strings;
import no.ssb.klass.api.controllers.validators.CsvFieldsValidator;
import no.ssb.klass.api.dto.*;
import no.ssb.klass.api.dto.hal.*;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.core.exception.KlassEmailException;
import no.ssb.klass.core.model.*;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.SubscriberService;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.service.search.SolrSearchResult;
import no.ssb.klass.core.util.AlphaNumericalComparator;
import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.*;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import java.beans.PropertyEditorSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RestController
// NOTE: CrossOrigin config moved to KlassSecurityConfiguration
// due to conditional behavior where some requests didn't get CORS headers and cause cache problems
@RequestMapping(value = {RestConstants.PREFIX_AND_API_VERSION_V1, RestConstants.API_VERSION_V1, "/rest/v1"},
        produces = {MediaTypes.HAL_JSON_VALUE, "application/*", "text/csv"})
public class ClassificationController {
    private static final Logger log = LoggerFactory.getLogger(ClassificationController.class);
    private final ClassificationService classificationService;
    private final SubscriberService subscriberService;
    private final SearchService searchService;
    private final StatisticsService statisticsService;
    private final CsvFieldsValidator csvFieldsValidator;

    @Value("${spring.data.rest.base-path:}")
    private String basePath;

    @Autowired
    public ClassificationController(ClassificationService classificationService,
                                    SubscriberService subscriberService,
                                    SearchService searchService, StatisticsService statisticsService,
                                    CsvFieldsValidator csvFieldsValidator) {
        this.classificationService = classificationService;
        this.subscriberService = subscriberService;
        this.searchService = searchService;
        this.statisticsService = statisticsService;
        this.csvFieldsValidator = csvFieldsValidator;
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String resourceNotFoundExceptionHandler(KlassResourceNotFoundException exception) {
        log.info(exception.getMessage() + ". For request: " + getCurrentRequest());
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String badRequestExceptionHandler(RestClientException exception) {
        log.info(exception.getMessage() + ". For request: " + getCurrentRequest());
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String argumentTypeMismatchExceptionHandler(MethodArgumentTypeMismatchException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String argumentTypeMismatchExceptionHandler(IllegalArgumentException exception) {
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String serverErrorExceptionHandler(Exception exception) {
        log.warn(exception.getMessage() + ". For request: " + getCurrentRequest(), exception);
        return exception.getMessage();
    }

    // redirect root to docs for convenience
    @RequestMapping("/")
    public RedirectView localRedirect() {
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("api-guide.html");
        return redirectView;
    }

    @RequestMapping(value = "/classificationfamilies", method = RequestMethod.GET)
    public CollectionModel<ClassificationFamilySummaryResource> classificationFamilies(
            // @formatter:off
                @RequestParam(value = "ssbSection", required = false) String ssbSection,
                @RequestParam(value = "includeCodelists", defaultValue = "false") boolean includeCodelists,
                @RequestParam(value = "language", defaultValue = "nb") Language language) {
            // @formatter:on
        ClassificationType classificationType = extractClassificationType(includeCodelists);
        ssbSection = extractSsbSection(ssbSection);
        List<ClassificationFamilySummary> summaries =
                classificationService.findPublicClassificationFamilySummaries(
                        ssbSection, classificationType);
        List<ClassificationFamilySummaryResource> summaryResources = summaries.stream()
                .map(summary -> new ClassificationFamilySummaryResource(summary, language))
                .collect(toList());

        return new KlassResources<>(summaryResources, Link.of(getCurrentRequest(), IanaLinkRelations.SELF));
    }

    @RequestMapping(value = "/classificationfamilies/{id}", method = RequestMethod.GET)
    public ClassificationFamilyResource classificationFamily(
            // @formatter:off
                @PathVariable Long id,
                @RequestParam(value = "ssbSection", required = false) String ssbSection,
                @RequestParam(value = "includeCodelists", defaultValue = "false") Boolean includeCodelists,
                @RequestParam(value = "language", defaultValue = "nb") Language language) {
            // @formatter:on
        ClassificationType classificationType = extractClassificationType(includeCodelists);
        ssbSection = extractSsbSection(ssbSection);
        ClassificationFamily classificationFamily = classificationService.getClassificationFamily(id);
        return new ClassificationFamilyResource(classificationFamily, language, ssbSection, classificationType);
    }

    @RequestMapping(value = "/ssbsections", method = RequestMethod.GET)
    public CollectionModel<SsbSectionResource> ssbsections() {
        List<SsbSectionResource> ssbSectionResources = classificationService
                .findResponsibleSectionsWithPublishedVersions().stream()
                .sorted().map(SsbSectionResource::new).collect(toList());
        return new KlassResources<>(ssbSectionResources, Link.of(getCurrentRequest(), IanaLinkRelations.SELF));
    }

    @RequestMapping(value = "/classifications", method = RequestMethod.GET)
    public KlassPagedResources<ClassificationSummaryResource> classifications(
            // @formatter:off
                @RequestParam(value = "includeCodelists", defaultValue = "false") boolean includeCodelists,
                @RequestParam(value = "changedSince", required = false)
                @DateTimeFormat(iso = ISO.DATE_TIME, fallbackPatterns = "yyyy-MM-dd'T'HH:mm:ss.ssZ") Date changedSince,
                Pageable pageable, PagedResourcesAssembler<ClassificationSeries> assembler) {
            // @formatter:on

        Page<ClassificationSeries> classifications = classificationService.findAllPublic(
                includeCodelists, changedSince, pageable);

        Link self = Link.of(getCurrentRequest(), IanaLinkRelations.SELF);
        PagedModel<ClassificationSummaryResource> response = assembler.toModel(classifications,
                ClassificationSummaryResource::new, self);
        addSearchLink(response);
        return new KlassPagedResources<>(response);
    }

    @RequestMapping(value = "/classifications/search", method = RequestMethod.GET)
    public KlassPagedResources<SearchResultResource> search(
            // @formatter:off
                @RequestParam(value = "query") String query,
                @RequestParam(value = "ssbSection", required = false) String ssbSection,
                @RequestParam(value = "includeCodelists", defaultValue = "false") boolean includeCodelists,
                Pageable pageable, PagedResourcesAssembler<SolrSearchResult> assembler) {
            // @formatter:on
        Link self = Link.of(getCurrentRequest(), IanaLinkRelations.SELF);
        ssbSection = extractSsbSection(ssbSection);

        FacetAndHighlightPage<SolrSearchResult> page =
                searchService.publicSearch(query, pageable, ssbSection, includeCodelists);
        PagedModel<SearchResultResource> response = assembler.toModel(page,
                searchResult -> new SearchResultResource(searchResult, page.getHighlights(searchResult)),
                self);

        boolean hit = response.getContent().size() != 0;
        statisticsService.addSearchWord(query, hit);
        return new KlassPagedResources<>(response);
    }

    @RequestMapping(value = "/classifications/{id}", method = RequestMethod.GET)
    public ClassificationResource classification(@PathVariable Long id,
                                                 @RequestParam(value = "language", defaultValue = "nb") Language language,
                                                 @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture) {
        ClassificationSeries classification = classificationService.getClassificationSeries(id);
        statisticsService.addUseForClassification(classification);
        return new ClassificationResource(classification, language, includeFuture);
    }

    @RequestMapping(value = "/versions/{id}", method = RequestMethod.GET)
    public ClassificationVersionResource versions(@PathVariable Long id,
                                                  @RequestParam(value = "language", defaultValue = "nb") Language language,
                                                  @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture) {
        ClassificationVersion version = classificationService.getClassificationVersion(id);
        List<CorrespondenceTable> corrTableVersionIsTarget = classificationService
                .findPublicCorrespondenceTablesWithTarget(version).stream()
                .sorted(AlphaNumericalComparator.comparing(CorrespondenceTable::getNameInPrimaryLanguage, true))
                .collect(toList());
        return new ClassificationVersionResource(version, language, corrTableVersionIsTarget, includeFuture);
    }

    @RequestMapping(value = "/correspondencetables/{id}", method = RequestMethod.GET)
    public CorrespondenceTableResource correspondenceTables(@PathVariable Long id, @RequestParam(value = "language",
            defaultValue = "nb") Language language) {
        CorrespondenceTable table = classificationService.getCorrespondenceTable(id);
        return new CorrespondenceTableResource(table, language);
    }

    @RequestMapping(value = "/variants/{id}", method = RequestMethod.GET)
    public ClassificationVariantResource variants(@PathVariable Long id, @RequestParam(value = "language",
            defaultValue = "nb") Language language) {
        ClassificationVariant variant = classificationService.getClassificationVariant(id);
        return new ClassificationVariantResource(variant, language);
    }

    @RequestMapping(value = "/classifications/{id}/codes", method = RequestMethod.GET)
    public CodeList codes(@PathVariable Long id,
                          // @formatter:off
                          @RequestParam(value = "from") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate from,
                          @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate to,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "selectLevel", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        CodeList codeList = codesInternal(id, new DateRangeHolder(from, to), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsCodeItem(csvFieldsList);
            codeList.setCsvFields(csvFieldsList);
        }
        return codeList;
    }

    @RequestMapping(value = "/classifications/{id}/codesAt", method = RequestMethod.GET)
    public CodeList codesAt(@PathVariable Long id,
                            // @formatter:off
                          @RequestParam(value = "date") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate date,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "selectLevel", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on

        CodeList codeList = codesInternal(id, new DateRangeHolder(date), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsCodeItem(csvFieldsList);
            codeList.setCsvFields(csvFieldsList);
        }
        return codeList;
    }

    private CodeList codesInternal(Long id, DateRangeHolder dateRangeHolder, String csvSeparator, String selectLevel,
                                   String selectCodes, String presentationNamePattern, Language language, Boolean includeFuture) {
        List<CodeDto> codes = classificationService.findClassificationCodes(id, dateRangeHolder.dateRange, language, includeFuture);
        CodeList codeList = new CodeList(csvSeparator, dateRangeHolder.withRange, dateRangeHolder.dateRange, includeFuture).convert(codes);
        return codeList.filterValidity(dateRangeHolder.dateRange)
                .limit(dateRangeHolder.dateRange)
                .compress()
                .filterOnLevel(selectLevel)
                .filterOnCodes(selectCodes)
                .presentationNames(presentationNamePattern)
                .sort();
    }

    @RequestMapping(value = "/classifications/{id}/changes", method = RequestMethod.GET)
    public CodeChangeList changes(@PathVariable Long id,
                                  // @formatter:off
                          @RequestParam(value = "from") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate from,
                          @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate to,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture) {
            // @formatter:on
        DateRange dateRange = DateRange.create(from, to);
        ClassificationSeries classification = classificationService.getClassificationSeries(id);
        List<CorrespondenceTable> changeTables = classification.getChangeTables(dateRange, includeFuture).stream()
                .filter(correspondenceTable -> correspondenceTable.isPublished(language)).collect(toList());
        CodeChangeList codeChanges = new CodeChangeList(csvSeparator);
        for (CorrespondenceTable changeTable : changeTables) {
            codeChanges = codeChanges.merge(codeChanges.convert(changeTable, language));
        }

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsChangeItemSchema(csvFieldsList);
            codeChanges.setCsvFields(csvFieldsList);
        }

        return codeChanges;
    }

    @RequestMapping(value = "/classifications/{id}/variant", method = RequestMethod.GET)
    public CodeList variant(@PathVariable Long id,
                            // @formatter:off
                          @RequestParam(value = "variantName") String variantName,
                          @RequestParam(value = "from") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate from,
                          @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate to,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "level", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        CodeList codeList = variantInternal(id, variantName, new DateRangeHolder(from, to), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsCodeItem(csvFieldsList);
            codeList.setCsvFields(csvFieldsList);
        }

        return codeList;

    }

    @RequestMapping(value = "/classifications/{id}/variantAt", method = RequestMethod.GET)
    public CodeList variantAt(@PathVariable Long id,
                              // @formatter:off
                          @RequestParam(value = "variantName") String variantName,
                          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate date,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "level", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        CodeList codeList = variantInternal(id, variantName, new DateRangeHolder(date), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsCodeItem(csvFieldsList);
            codeList.setCsvFields(csvFieldsList);
        }

        return codeList;
    }


    private CodeList variantInternal(Long id, String variantName, DateRangeHolder dateRangeHolder, String csvSeparator,
                                     String selectLevel, String selectCodes, String presentationNamePattern, Language language, Boolean includeFuture) {
        List<CodeDto> codes = classificationService.findVariantClassificationCodes(id, variantName, language,
                dateRangeHolder.dateRange, includeFuture);
        return new CodeList(csvSeparator, dateRangeHolder.withRange, dateRangeHolder.dateRange, includeFuture).convert(codes).limit(dateRangeHolder.dateRange)
                .compress().filterOnLevel(selectLevel).filterOnCodes(selectCodes).presentationNames(
                        presentationNamePattern).sort();
    }

    @RequestMapping(value = "/classifications/{id}/corresponds", method = RequestMethod.GET)
    public CorrespondenceItemList corresponds(@PathVariable Long id,
                                              // @formatter:off
                          @RequestParam(value = "targetClassificationId") Long targetClassificationId,
                          @RequestParam(value = "from") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate from,
                          @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate to,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture) {
            // @formatter:on
        CorrespondenceItemList correspondenceList = correspondsInternal(id, targetClassificationId,
                new DateRangeHolder(from, to), csvSeparator, language, includeFuture, false);

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsCorrespondenceItem(csvFieldsList);
            correspondenceList.setCsvFields(csvFieldsList);
        }

        return correspondenceList;
    }

    @RequestMapping(value = "/classifications/{id}/correspondsAt", method = RequestMethod.GET)
    public CorrespondenceItemList correspondsAt(@PathVariable Long id,
                                                // @formatter:off
                          @RequestParam(value = "targetClassificationId") Long targetClassificationId,
                          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate date,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "csvFields", defaultValue = "") String csvFields,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture,
                          @RequestParam(value = "inverted", defaultValue = "false") Boolean inverted) {
            // @formatter:on
        CorrespondenceItemList correspondenceList = correspondsInternal(id, targetClassificationId,
                new DateRangeHolder(date), csvSeparator, language, includeFuture, inverted);

        if (!csvFields.isEmpty()) {
            List<String> csvFieldsList = getCsvFieldsList(csvFields);
            csvFieldsValidator.validateFieldsCorrespondenceItem(csvFieldsList);
            correspondenceList.setCsvFields(csvFieldsList);
        }

        return correspondenceList;
    }

    private CorrespondenceItemList correspondsInternal(Long id, Long targetClassificationId,
                                                       DateRangeHolder dateRangeHolder, String csvSeparator, Language language,
                                                       Boolean includeFuture, Boolean inverted) {
        List<CorrespondenceDto> correspondences = classificationService.findCorrespondences(id, targetClassificationId,
                dateRangeHolder.dateRange, language, includeFuture, inverted);
        return new CorrespondenceItemList(csvSeparator, dateRangeHolder.withRange, includeFuture)
                .convert(correspondences)
                .removeOutside(dateRangeHolder.dateRange)
                .limit(dateRangeHolder.dateRange)
                .group()
                .sort();
    }

    @Transactional(rollbackFor = {KlassEmailException.class, MalformedURLException.class})
    @RequestMapping(value = "/classifications/{classificationId}/trackChanges", method = RequestMethod.POST)
    public ResponseEntity<SubscribeResponse> trackChanges(
            @PathVariable Long classificationId,
            @RequestParam(value = "email") String email
    ) {
        ClassificationSeries classification = classificationService.getClassificationSeries(classificationId);
        if (subscriberService.containsTracking(email, classification)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SubscribeResponse.EXISTS);
        }
        try {
            URL endSubscriptionUrl = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClassificationController.class)
                    .removeTracking(classificationId, email)).toUri().toURL();

            String token = subscriberService.trackChanges(email, classification, endSubscriptionUrl);

            URL verifySubscriptionUrl = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClassificationController.class)
                    .verifyTracking(email, token)).toUri().toURL();

            subscriberService.sendVerificationMail(email, verifySubscriptionUrl, classification);
        } catch (KlassEmailException | MalformedURLException e) {
            log.error(e.getMessage(), e);
            // rollback to avoid people signing up without getting verification mail
            // TODO: we should probably make a way to resend verification email instead.
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SubscribeResponse.EMAIL_PROBLEM);
        }
        return ResponseEntity.ok(SubscribeResponse.CREATED);
    }

    @RequestMapping(value = "/classifications/{classificationId}/removeTracking", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> removeTracking(@PathVariable Long classificationId, @RequestParam(
            value = "email") String email) {
        try {
            ClassificationSeries classification = classificationService.getClassificationSeries(classificationId);
            subscriberService.removeTracking(email, classification);
        } catch (ClientException e) {
            throw new RestClientException(e.getMessage());
        }
        return ResponseEntity.ok("""
                <!DOCTYPE html>
                <html>
                    <head><title>Klass subscription</title></head>
                    <body>
                        <header>
                            <h2>Klass subscription</h2>
                        </header>
                        <p>Subscription is deleted.</p>
                    </body>
                </html>
                """);
    }

    @RequestMapping(value = "/classifications/verifyTracking/{email}/{token}", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> verifyTracking(@PathVariable String email, @PathVariable String token) {
        try {
            subscriberService.verifyTracking(email, token);
        } catch (ClientException e) {
            throw new RestClientException(e.getMessage());
        }
        return ResponseEntity.ok("""
                <!DOCTYPE html>
                <html>
                    <head><title>Klass subscription</title></head>
                    <body>
                        <header>
                            <h2>Klass subscription</h2>
                        </header>
                        <p>Subscription is verified.</p>
                    </body>
                </html>
                """);
    }

    private void addSearchLink(PagedModel<ClassificationSummaryResource> response) {
        WebMvcLinkBuilder linkBuilder = WebMvcLinkBuilder.linkTo(WebMvcLinkBuilder.methodOn(ClassificationController.class).search("query", null, true,
                null, null));
        response.add(Link.of(ResourceUtil.createUriTemplate(linkBuilder, "query", "includeCodelists"), "search"));
    }


    private String getCurrentRequest() {
        return ServletUriComponentsBuilder.fromCurrentRequest().build().toString();
    }

    private ClassificationType extractClassificationType(boolean includeCodelists) {
        return includeCodelists ? null : ClassificationType.CLASSIFICATION;
    }

    private String extractSsbSection(String ssbSection) {
        return Strings.isNullOrEmpty(ssbSection) ? null : ssbSection;
    }

    private List<String> getCsvFieldsList(String csvFields) {
        return Arrays.asList(csvFields.split(","));
    }


    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Language.class, new CaseInsensitiveConverter<>(Language.class));
    }

    private static class DateRangeHolder {
        private final DateRange dateRange;
        private final boolean withRange;

        DateRangeHolder(LocalDate from, LocalDate to) {
            this.dateRange = DateRange.create(from, to);
            this.withRange = true;
        }

        DateRangeHolder(LocalDate date) {
            this.dateRange = DateRange.create(date, date.plusDays(1));
            this.withRange = false;
        }

        @Override
        public String toString() {
            return "DateRangeHolder{" +
                    "dateRange=" + dateRange +
                    ", withRange=" + withRange +
                    '}';
        }
    }

    private static class CaseInsensitiveConverter<T extends Enum<T>> extends PropertyEditorSupport {

        private final Class<T> typeParameterClass;

        CaseInsensitiveConverter(Class<T> typeParameterClass) {
            super();
            this.typeParameterClass = typeParameterClass;
        }

        @Override
        public void setAsText(final String text) throws IllegalArgumentException {
            String upper = text.toUpperCase(); // or something more robust
            T value = T.valueOf(typeParameterClass, upper);
            setValue(value);
        }

    }
}
