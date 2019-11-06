package no.ssb.klass.api.controllers;

import static java.util.stream.Collectors.*;

import java.beans.PropertyEditorSupport;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import no.ssb.klass.core.util.AlphaNumericalComparator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.query.result.FacetAndHighlightPage;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.Resources;
import org.springframework.hateoas.mvc.ControllerLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.servlet.view.RedirectView;

import com.google.common.base.Strings;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.ClassificationVariant;
import no.ssb.klass.core.model.ClassificationVersion;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.repository.ClassificationFamilySummary;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.service.SearchService;
import no.ssb.klass.core.service.StatisticsService;
import no.ssb.klass.core.service.SubscriberService;
import no.ssb.klass.core.service.dto.CodeDto;
import no.ssb.klass.core.service.dto.CorrespondenceDto;
import no.ssb.klass.core.service.search.SolrSearchResult;
import no.ssb.klass.core.util.ClientException;
import no.ssb.klass.core.util.DateRange;
import no.ssb.klass.core.util.KlassResourceNotFoundException;
import no.ssb.klass.api.dto.CodeChangeList;
import no.ssb.klass.api.dto.CodeList;
import no.ssb.klass.api.dto.CorrespondenceItemList;
import no.ssb.klass.api.dto.KlassPagedResources;
import no.ssb.klass.api.dto.KlassResources;
import no.ssb.klass.api.dto.SubscribeResponse;
import no.ssb.klass.api.dto.hal.ClassificationFamilyResource;
import no.ssb.klass.api.dto.hal.ClassificationFamilySummaryResource;
import no.ssb.klass.api.dto.hal.ClassificationResource;
import no.ssb.klass.api.dto.hal.ClassificationSummaryResource;
import no.ssb.klass.api.dto.hal.ClassificationVariantResource;
import no.ssb.klass.api.dto.hal.ClassificationVersionResource;
import no.ssb.klass.api.dto.hal.CorrespondenceTableResource;
import no.ssb.klass.api.dto.hal.ResourceUtil;
import no.ssb.klass.api.dto.hal.SearchResultResource;
import no.ssb.klass.api.dto.hal.SsbSectionResource;
import no.ssb.klass.api.util.RestConstants;

@RestController
// NOTE: CrossOrigin config moved to KlassSecurityConfiguration
// due to conditional behavior where some requests didn't get CORS headers and cause cache problems
@RequestMapping(value = { RestConstants.API_VERSION_V1,"/api/klass"+RestConstants.API_VERSION_V1, "/rest/v1" })
public class ClassificationController {
    private static final Logger log = LoggerFactory.getLogger(ClassificationController.class);
    private final ClassificationService classificationService;
    private final SubscriberService subscriberService;
    private final SearchService searchService;
    private final StatisticsService statisticsService;

    @Autowired
    public ClassificationController(ClassificationService classificationService,
            SubscriberService subscriberService,
            SearchService searchService, StatisticsService statisticsService) {
        this.classificationService = classificationService;
        this.subscriberService = subscriberService;
        this.searchService = searchService;
        this.statisticsService = statisticsService;
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
    public Resources<ClassificationFamilySummaryResource> classificationFamilies(
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

        return new KlassResources<>(summaryResources, new Link(getCurrentRequest(), Link.REL_SELF));
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
    public Resources<SsbSectionResource> ssbsections() {
        List<SsbSectionResource> ssbSectionResources = classificationService
                .findResponsibleSectionsWithPublishedVersions().stream()
                .sorted().map(SsbSectionResource::new).collect(toList());
        return new KlassResources<>(ssbSectionResources, new Link(getCurrentRequest(), Link.REL_SELF));
    }

    @RequestMapping(value = "/classifications", method = RequestMethod.GET)
    public KlassPagedResources<ClassificationSummaryResource> classifications(
            // @formatter:off
                @RequestParam(value = "includeCodelists", defaultValue = "false") boolean includeCodelists,
                @RequestParam(value = "changedSince", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Date changedSince,
                Pageable pageable, PagedResourcesAssembler<ClassificationSeries> assembler) {
            // @formatter:on

        Page<ClassificationSeries> classifications = classificationService.findAllPublic(
                includeCodelists, changedSince, pageable);

        Link self = new Link(getCurrentRequest(), Link.REL_SELF);
        PagedResources<ClassificationSummaryResource> response = assembler.toResource(classifications,
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
        Link self = new Link(getCurrentRequest(), Link.REL_SELF);
        ssbSection = extractSsbSection(ssbSection);

        FacetAndHighlightPage<SolrSearchResult> page =
                searchService.publicSearch(query, pageable, ssbSection, includeCodelists);
        PagedResources<SearchResultResource> response = assembler.toResource(page,
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
                          @RequestParam(value = "selectLevel", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        return codesInternal(id, new DateRangeHolder(from, to), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);
    }

    @RequestMapping(value = "/classifications/{id}/codesAt", method = RequestMethod.GET)
    public CodeList codesAt(@PathVariable Long id,
            // @formatter:off
                          @RequestParam(value = "date") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate date,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "selectLevel", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        return codesInternal(id, new DateRangeHolder(date), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);
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
        return codeChanges;
    }

    @RequestMapping(value = "/classifications/{id}/variant", method = RequestMethod.GET)
    public CodeList variant(@PathVariable Long id,
            // @formatter:off
                          @RequestParam(value = "variantName") String variantName,
                          @RequestParam(value = "from") @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate from,
                          @RequestParam(value = "to", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate to,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "level", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        return variantInternal(id, variantName, new DateRangeHolder(from, to), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);
    }

    @RequestMapping(value = "/classifications/{id}/variantAt", method = RequestMethod.GET)
    public CodeList variantAt(@PathVariable Long id,
            // @formatter:off
                          @RequestParam(value = "variantName") String variantName,
                          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate date,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "level", required = false) String selectLevel,
                          @RequestParam(value = "selectCodes", required = false) String selectCodes,
                          @RequestParam(value = "presentationNamePattern", required = false) String presentationNamePattern,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture
                          ) {
            // @formatter:on
        return variantInternal(id, variantName, new DateRangeHolder(date), csvSeparator, selectLevel, selectCodes,
                presentationNamePattern, language, includeFuture);
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
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture) {
            // @formatter:on
        return correspondsInternal(id, targetClassificationId, new DateRangeHolder(from, to), csvSeparator, language, includeFuture);
    }

    @RequestMapping(value = "/classifications/{id}/correspondsAt", method = RequestMethod.GET)
    public CorrespondenceItemList correspondsAt(@PathVariable Long id,
            // @formatter:off
                          @RequestParam(value = "targetClassificationId") Long targetClassificationId,
                          @RequestParam(value = "date", required = false) @DateTimeFormat(pattern = RestConstants.DATE_FORMAT) LocalDate date,
                          @RequestParam(value = "csvSeparator", defaultValue = ",") String csvSeparator,
                          @RequestParam(value = "language", defaultValue = "nb") Language language,
                          @RequestParam(value = "includeFuture", defaultValue = "false") Boolean includeFuture) {
            // @formatter:on
        return correspondsInternal(id, targetClassificationId, new DateRangeHolder(date), csvSeparator, language, includeFuture);
    }

    private CorrespondenceItemList correspondsInternal(Long id, Long targetClassificationId,
            DateRangeHolder dateRangeHolder, String csvSeparator, Language language, Boolean includeFuture) {
        List<CorrespondenceDto> correspondences = classificationService.findCorrespondences(id, targetClassificationId,
                dateRangeHolder.dateRange, language, includeFuture);
        return new CorrespondenceItemList(csvSeparator, dateRangeHolder.withRange, includeFuture).convert(correspondences)
                .removeOutside(dateRangeHolder.dateRange).limit(dateRangeHolder.dateRange).compress().sort();
    }

    @Transactional
    @RequestMapping(value = "/classifications/{classificationId}/trackChanges", method = RequestMethod.POST)
    public ResponseEntity<SubscribeResponse> trackChanges(@PathVariable Long classificationId, @RequestParam(
            value = "email") String email) {
        ClassificationSeries classification = null;
        try {
            classification = classificationService.getClassificationSeries(classificationId);
            if (subscriberService.containsTracking(email, classification)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(SubscribeResponse.EXISTS);
            } else {
                URL endSubscriptionUrl = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class)
                        .removeTracking(classificationId, email)).toUri().toURL();

                String token = subscriberService.trackChanges(email, classification, endSubscriptionUrl);

                URL verifySubscriptionUrl = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class)
                        .verifyTracking(email, token)).toUri().toURL();

                subscriberService.sendVerificationMail(email, verifySubscriptionUrl, classification);
            }
        } catch (ClientException e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SubscribeResponse.UNKNOWN_ERROR);
        } catch (MalformedURLException e) {
            log.error(e.getMessage(), e);
            // rollback to avoid people signing up without getting verification mail
            // TODO: we should probably make a way to resend verification email instead.
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SubscribeResponse.EMAIL_PROBLEM);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(SubscribeResponse.UNKNOWN_ERROR);
        }
        // return ResponseEntity.ok("An email is sent for verification.");
        return ResponseEntity.ok(SubscribeResponse.CREATED);
    }

    @RequestMapping(value = "/classifications/{classificationId}/removeTracking", method = RequestMethod.GET)
    public ResponseEntity<String> removeTracking(@PathVariable Long classificationId, @RequestParam(
            value = "email") String email) {
        try {
            ClassificationSeries classification = classificationService.getClassificationSeries(classificationId);
            subscriberService.removeTracking(email, classification);
        } catch (ClientException e) {
            throw new RestClientException(e.getMessage());
        }
        return ResponseEntity.ok("Subscription is deleted.");
    }

    @RequestMapping(value = "/classifications/verifyTracking/{email}/{token}", method = RequestMethod.GET)
    public ResponseEntity<String> verifyTracking(@PathVariable String email, @PathVariable String token) {
        try {
            subscriberService.verifyTracking(email, token);
        } catch (ClientException e) {
            throw new RestClientException(e.getMessage());
        }
        return ResponseEntity.ok("Subscription is verified.");
    }

    private void addSearchLink(PagedResources<ClassificationSummaryResource> response) {
        ControllerLinkBuilder linkBuilder = ControllerLinkBuilder.linkTo(ControllerLinkBuilder.methodOn(ClassificationController.class).search("query", null, true,
                null, null));
        response.add(new Link(ResourceUtil.createUriTemplate(linkBuilder, "query", "includeCodelists"), "search"));
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
