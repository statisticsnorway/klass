package no.ssb.klass.api.applicationtest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.CorrespondenceTable;
import no.ssb.klass.testutil.TestDataProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionTemplate;

import no.ssb.klass.core.model.ClassificationFamily;
import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.repository.ClassificationFamilyRepository;
import no.ssb.klass.core.repository.ClassificationSeriesRepository;
import no.ssb.klass.core.repository.CorrespondenceTableRepository;
import no.ssb.klass.core.repository.UserRepository;
import no.ssb.klass.core.service.ClassificationService;
import no.ssb.klass.core.util.TimeUtil;
import no.ssb.klass.core.util.TranslatablePersistenceConverter;
import no.ssb.klass.api.applicationtest.config.ApplicationTestConfig;
import no.ssb.klass.api.applicationtest.utils.ApplicationTestUtil;
import no.ssb.klass.api.util.RestConstants;
import no.ssb.klass.testutil.ConstantClockSource;
import no.ssb.klass.testutil.TestUtil;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.DatabaseProvider.ZONKY;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = { ApplicationTestConfig.class }, webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles({ ConfigurationProfiles.POSTGRES_EMBEDDED, ConfigurationProfiles.MOCK_SEARCH })
@AutoConfigureEmbeddedDatabase(provider = ZONKY, type= AutoConfigureEmbeddedDatabase.DatabaseType.POSTGRES)
@ComponentScan(basePackageClasses = TranslatablePersistenceConverter.class)
public abstract class AbstractRestApiApplicationTest {

    public static final String CONTENT_TYPE_CSV = "text/csv";

    public static final String REQUEST = RestConstants.API_VERSION_V1 + "/classifications";
    public static final String REQUEST_WITH_ID = REQUEST + "/{classificationId}";
    public static final String REQUEST_SEARCH = REQUEST + "/search";
    public static final String REQUEST_WITH_ID_AND_CODES = REQUEST + "/{classificationId}/codes";
    public static final String REQUEST_WITH_ID_AND_CODES_AT = REQUEST + "/{classificationId}/codesAt";
    public static final String REQUEST_WITH_ID_AND_VARIANT = REQUEST + "/{classificationId}/variant";
    public static final String REQUEST_WITH_ID_AND_VARIANT_AT = REQUEST + "/{classificationId}/variantAt";
    public static final String REQUEST_WITH_ID_AND_CORRESPONDS = REQUEST + "/{classificationId}/corresponds";
    public static final String REQUEST_WITH_ID_AND_CORRESPONDS_AT = REQUEST + "/{classificationId}/correspondsAt";

    public static final String REQUEST_CORRESPONDENCE_TABLES = RestConstants.API_VERSION_V1
            + "/correspondencetables/{correspondencetablesId}";

    public static final String REQUEST_SSB_SECTION = RestConstants.API_VERSION_V1 + "/ssbsections";

    public static final String REQUEST_CLASSIFICATION_FAMILY = RestConstants.API_VERSION_V1 + "/classificationfamilies";
    public static final String REQUEST_CLASSIFICATION_FAMILY_WITH_ID = REQUEST_CLASSIFICATION_FAMILY
            + "/{classificationfamilyId}";

    public static final String REQUEST_WITH_ID_AND_CHANGES = REQUEST + "/{classificationId}/changes";

    public static final String JSON_SEARCH_RESULTS = "_embedded.searchResults";
    public static final String JSON_SEARCH_RESULT1 = "_embedded.searchResults[0]";
    public static final String JSON_SEARCH_RESULT2 = "_embedded.searchResults[1]";

    public static final String XML_SEARCH_RESULTS = "PagedResources.contents.content";
    public static final String XML_SEARCH_RESULT1 = "PagedResources.contents.content[0]";
    public static final String XML_SEARCH_RESULT2 = "PagedResources.contents.content[1]";

    public static final String JSON_CLASSIFICATIONS = "_embedded.classifications";
    public static final String JSON_CLASSIFICATION1 = "_embedded.classifications[0]";
    public static final String JSON_CLASSIFICATION2 = "_embedded.classifications[1]";
    public static final String JSON_CLASSIFICATION3 = "_embedded.classifications[2]";

    public static final String XML_CLASSIFICATIONS = "PagedResources.contents.content";
    public static final String XML_CLASSIFICATION1 = "PagedResources.contents.content[0]";
    public static final String XML_CLASSIFICATION2 = "PagedResources.contents.content[1]";
    public static final String XML_CLASSIFICATION3 = "PagedResources.contents.content[2]";

    public static final String JSON_PAGE = "page";
    public static final String XML_PAGE = "PagedResources.page";

    public static final String JSON_LINKS = "_links";
    public static final String XML_LINKS = "PagedResources.links";
    public static final String XML_ROOT = "PagedResources";

    public static final String JSON_CODES = "codes";
    public static final String XML_CODES = "codeList.codeItem";

    public static final String XML_CORRESPONDENCES = "correspondenceItemList.correspondenceItem";
    public static final String JSON_CORRESPONDENCES = "correspondenceItems";

    public static final String XML_CORRESPONDENCETABLE = "correspondenceTable";
    public static final String XML_CORRESPONDENCETABLE_MAP = XML_CORRESPONDENCETABLE
            + ".correspondenceMaps.correspondenceMap";

    public static final int PAGE_SIZE = 20;

    public static final String CHANGED_SINCE_NEW_DATE = "2015-10-31T03:00:00.000-0200";
    public static final String CHANGED_SINCE_OLD_DATE = "2000-10-30T01:00:00.000-0200";

    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    protected ClassificationSeries kommuneinndeling;
    protected ClassificationSeries bydelsinndeling;
    protected ClassificationSeries familieGrupperingCodelist;
    protected ClassificationFamily classificationFamily;
    protected CorrespondenceTable correspondenceTable;

    @Autowired
    protected ApplicationTestUtil applicationTestUtil;

    @Autowired
    protected ClassificationService classificationService;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected ClassificationFamilyRepository classificationFamilyRepository;
    @Autowired
    protected ClassificationSeriesRepository seriesRepository;
    @Autowired
    protected CorrespondenceTableRepository correspondenceTableRepository;
    @Autowired
    protected TransactionTemplate template;
    @Value("${local.server.port}")
    protected int port;

    @BeforeEach
    public void prepareTestData() {
        applicationTestUtil.clearDatabase();
        applicationTestUtil.clearSearch();
        // if (!testDataInitialized) {
        User user = userRepository.save(TestUtil.createUser());
        TimeUtil.setClockSource(new ConstantClockSource(parseDate(CHANGED_SINCE_OLD_DATE)));
        classificationFamily = classificationFamilyRepository.save(TestUtil
                .createClassificationFamily("Befolkning"));
        kommuneinndeling = TestDataProvider.createClassificationKommuneinndeling();
        kommuneinndeling.setContactPerson(user);
        classificationFamily.addClassificationSeries(kommuneinndeling);
        TimeUtil.setClockSource(new ConstantClockSource(parseDate(CHANGED_SINCE_NEW_DATE)));
        bydelsinndeling = TestDataProvider.createClassificationBydelsinndeling();
        bydelsinndeling.setContactPerson(user);
        classificationFamily.addClassificationSeries(bydelsinndeling);

        familieGrupperingCodelist = TestDataProvider.createFamiliegrupperingCodelist(user);
        classificationFamily.addClassificationSeries(familieGrupperingCodelist);
        classificationService.saveAndIndexClassification(familieGrupperingCodelist);

        kommuneinndeling = classificationService.saveAndIndexClassification(kommuneinndeling);
        bydelsinndeling = classificationService.saveAndIndexClassification(bydelsinndeling);

        correspondenceTable = TestDataProvider.createAndAddCorrespondenceTable(kommuneinndeling,
                bydelsinndeling);
        correspondenceTableRepository.save(correspondenceTable);
        correspondenceTableRepository
                .save(TestDataProvider.createAndAddCorrespondenceTableTableFutureVersion(kommuneinndeling,
                        bydelsinndeling));
        correspondenceTableRepository.save(TestDataProvider.createAndAddChangeCorrespondenceTable(
                kommuneinndeling));
        correspondenceTableRepository.save(TestDataProvider.createAndAddChangeCorrespondenceTableFutureVersion(
                kommuneinndeling));

        TimeUtil.setClockSource(new ConstantClockSource(parseDate(CHANGED_SINCE_OLD_DATE)));
        classificationService.saveAndIndexClassification(kommuneinndeling);
        TimeUtil.revertClockSource();

        TransactionStatus transaction = template.getTransactionManager().getTransaction(null);
        seriesRepository.updateClassificationLastModified(bydelsinndeling.getId(), bydelsinndeling
                .getLastModified());
        template.getTransactionManager().commit(transaction);
        // testDataInitialized = true;

        // }
    }

    protected Date parseDate(String date) {
        try {
            return dateFormat.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException("unable to parse date", e);
        }
    }

}
