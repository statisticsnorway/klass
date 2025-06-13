package no.ssb.klass.api.migration;

import java.util.Arrays;
import java.util.List;


public final class MigrationTestConstants {

    // paths
    public static final String DATA_SSB_HOST = "https://data.ssb.no";
    public static final String BASE_PATH = "/api/klass";
    public static final String CLASSIFICATIONS_PATH = "/classifications";
    public static final String CHANGES = "changes";
    public static final String CLASSIFICATION_FAMILIES = "classificationfamilies";
    public static final String VARIANT = "variant";
    public static final String VARIANT_AT = "variantAt";
    public static final String VARIANTS = "variants";
    public static final String CORRESPONDS = "corresponds";
    public static final String CORRESPONDS_AT = "correspondsAt";

    // Values
    public static final String TRUE= "true";
    public static final String NN= "nn";
    public static final String EN= "en";


    // Top level
    public static final String PAGE = "page";

    // Combinations
    public static final String PAGE_TOTAL_ELEMENTS = "page.totalElements";
    public static final String EMBEDDED_CLASSIFICATIONS = "_embedded.classifications";
    public static final String EMBEDDED_CLASSIFICATIONS_ID = "_embedded.classifications.id";
    public static final String EMBEDDED_SSB_SECTIONS= "_embedded.ssbSections";
    public static final String EMBEDDED_SSB_SECTIONS_NAME= "_embedded.ssbSections.name";
    public static final String EMBEDDED_CLASSIFICATION_FAMILIES = "_embedded.classificationFamilies";
    public static final String PUBLISHED_NB="published.nb";
    public static final String PUBLISHED_EN="published.en";
    public static final String PUBLISHED_NN="published.nn";
    public static final String CLASSIFICATION_VERSION= "classificationVersion";
    public static final String CLASSIFICATION_NAME =  "classification.name";
    public static final String CLASSIFICATION_ID = "classification.id";
    public static final String CLASSIFICATION_CLASSIFICATION_TYPE = "classification.classificationType";
    public static final String CLASSIFICATION_DESCRIPTION = "classification.description";
    public static final String CLASSIFICATION_LAST_MODIFIED = "classification.lastModified";
    public static final String CLASSIFICATION_PRIMARY_LANGUAGE = "classification.primaryLanguage";
    public static final String CLASSIFICATION_COPYRIGHTED = "classification.copyrighted";
    public static final String CLASSIFICATION_INCLUDE_SHORTNAME = "classification.includeShortName";
    public static final String CLASSIFICATION_INCLUDE_NOTES = "classification.includeNotes";
    public static final String CLASSIFICATION_CONTACT_PERSON = "classification.contactPerson";
    public static final String CLASSIFICATION_OWNING_SECTION = "classification.owningSection";

    // Fields
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CLASSIFICATION_TYPE = "classificationType";
    public static final String DESCRIPTION = "description";
    public static final String LAST_MODIFIED = "lastModified";
    public static final String PRIMARY_LANGUAGE = "primaryLanguage";
    public static final String COPYRIGHTED = "copyrighted";
    public static final String INCLUDE_SHORTNAME = "includeShortName";
    public static final String INCLUDE_NOTES = "includeNotes";
    public static final String CONTACT_PERSON = "contactPerson";
    public static final String OWNING_SECTION = "owningSection";
    public static final String VALID_FROM = "validFrom";
    public static final String VALID_TO = "validTo";
    public static final String INTRODUCTION= "introduction";
    public static final String LEGAL_BASE= "legalBase";
    public static final String DERIVED_FROM= "derivedFrom";
    public  static final String SOURCE = "source";
    public static final String SOURCE_ID = "sourceId";
    public static final String TARGET = "target";
    public static final String TARGET_ID ="targetId" ;
    public  static final String CHANGE_TABLE = "changeTable";
    public  static final String SOURCE_LEVEL = "sourceLevel";
    public  static final String TARGET_LEVEL = "targetLevel";
    public static final String TARGET_CLASSIFICATION_ID= "targetClassificationId";
    public static final String CODE= "code";
    public static final String PARENT_CODE= "parentCode";
    public static final String LEVEL = "level";
    public static final String SHORT_NAME = "shortName";
    public static final String NOTES = "notes";

    public static final String NUMBER_OF_CLASSIFICATIONS= "numberOfClassifications";

    public static final String CONTACT_PERSON_NAME = "contactPerson.name";
    public static final String CONTACT_PERSON_EMAIL = "contactPerson.email";
    public static final String CONTACT_PERSON_PHONE = "contactPerson.phone";
    public static final String LINK_REL = "link.rel";
    public static final String LINK_HREF ="link.href" ;
    public static final String ENTITIES = "entities";
    public static final String CORRESPONDENCE_TABLE_NAME= "correspondenceTable.name";
    public static final String CORRESPONDENCE_TABLE_OWNING_SECTION =  "correspondenceTable.owningSection";


    // Field of list type
    public static final String CLASSIFICATIONS = "classifications";
    public static final String STATISTICAL_UNITS = "statisticalUnits";
    public static final String VERSIONS = "versions";
    public static final String PUBLISHED = "published";
    public static final String PUBLICATIONS= "publications";
    public static final String CORRESPONDENCE_TABLES= "correspondencetables";
    public static final String CORRESPONDENCE_ITEMS = "correspondenceItems";
    public static final String CHANGELOGS= "changelogs";
    public static final String CODE_CHANGES= "codeChanges";
    public static final String SSB_SECTIONS= "ssbsections";
    public static final String CORRESPONDENCE_MAPS = "correspondenceMaps";
    public static final String LEVELS= "levels";
    public static final String CLASSIFICATION_ITEMS= "classificationItems";
    public static final String CLASSIFICATIONS_VARIANTS= "classificationVariants";

    // Field list type xml
    public static final String CLASSIFICATION_VERSION_PUBLISHED_LANGUAGES= "classificationVersion.publishedLanguages.published";

    public static final String CLASSIFICATION_VERSION_LEVELS= "classificationVersion.levels.level";
    public static final String CLASSIFICATION_VERSION_CLASSIFICATION_ITEMS= "classificationVersion.classificationItems.classificationItem";

    public static final String ENTITIES_CONTENTS_CONTENT= "entities.contents.content";
    public static final String CODE_CHANGE_LIST_CODE_CHANGE_ITEM= "codeChangeList.codeChangeItem";
    public static final String CODE_LIST_CODE_ITEM= "codeList.codeItem";
    public static final String CORRESPONDENCE_ITEM_LIST_CORRESPONDENCE_ITEM= "correspondenceItemList.correspondenceItem";
    public static final String CLASSIFICATION_FAMILY = "classificationFamily";
    public static final String CLASSIFICATION_FAMILY_CLASSIFICATIONS_CLASSIFICATION = "classificationFamily.classifications.classification";
    public static final String PAGED_ENTITIES_CONTENTS_CONTENT = "pagedEntities.contents.content";
    public static final String CORRESPONDENCE_TABLE = "correspondenceTable";

    // Link fields
    public static final String HREF= "href";
    public static final String LINKS_SELF_HREF= "_links.self.href";
    public static final String LINKS_FIRST_HREF= "_links.first.href";
    public static final String LINKS_NEXT_HREF= "_links.next.href";
    public static final String LINKS_CODES_HREF= "_links.codes.href";
    public static final String LINKS_CODES_TEMPLATED= "_links.codes.templated";
    public static final String LINKS_CODES_AT_HREF= "_links.codesAt.href";
    public static final String LINKS_CODES_AT_TEMPLATED= "_links.codesAt.templated";
    public static final String LINKS_VARIANT_HREF= "_links.variant.href";
    public static final String LINKS_VARIANT_TEMPLATED= "_links.variant.templated";
    public static final String LINKS_VARIANT_AT_HREF= "_links.variantAt.href";
    public static final String LINKS_VARIANT_AT_TEMPLATED= "_links.variantAt.templated";
    public static final String LINKS_CORRESPONDS_HREF= "_links.corresponds.href";
    public static final String LINKS_CORRESPONDS_TEMPLATED= "_links.correspondsAt.templated";
    public static final String LINKS_CORRESPONDS_AT_HREF= "_links.correspondsAt.href";
    public static final String LINKS_CORRESPONDS_AT_TEMPLATED= "_links.correspondsAt.templated";
    public static final String LINKS_CHANGES_HREF= "_links.changes.href";
    public static final String LINKS_CHANGES_TEMPLATED= "_links.changes.templated";
    public static final String LINKS_SEARCH_HREF= "_links.search.href";
    public static final String LINKS_SEARCH_TEMPLATED= "_links.search.templated";
    public static final String LINKS_LAST_HREF= "_links.last.href";
    public static final String LINKS_SOURCE_HREF= "_links.source.href";
    public static final String LINKS_SOURCE_TEMPLATED= "_links.source.templated";
    public static final String LINKS_TARGET_TEMPLATED= "_links.target.templated";
    public static final String LINKS_TARGET_HREF= "_links.target.href";


    // Params
    public static final String LANGUAGE ="language";
    public static final String INCLUDE_FUTURE= "includeFuture";
    public static final String CODES= "codes";
    public static final String SELECT_CODES= "selectCodes";
    public static final String SELECT_LEVEL= "selectLevel";
    public static final String CODES_AT= "codesAt";
    public static final String DATE= "date";
    public static final String FROM = "from";
    public static final String TO = "to";
    public static final String INCLUDE_CODE_LISTS= "includeCodeLists";
    public static final String CHANGED_SINCE= "changedSince";
    public static final String VARIANT_NAME= "variantName";
    public static final String SSB_SECTION= "ssbSection";
    public static final String CSV_SEPARATOR= "csvSeparator";
    public static final String CSV_FIELDS = "csvFields";
    public static final String PRESENTATION_NAME_PATTERN= "presentationNamePattern";

    // header
    public static final String ACCEPT= "Accept";
    public static final String APPLICATION_XML = "application/xml";
    public static final String TEXT_CSV= "text/csv";

    // Messages
    public static final String FAIL_MESSAGE= "Mismatch at path '%s':\n  Source: %s\n  Target: %s";
    public static final String LOG_MESSAGE_STATUS_CODE= "Status code: ";
    public static final String API_EMPTY_RESPONSE_MESSAGE ="Source api returned no content";


    // Lists
    public static List<String> pathNamesClassification = Arrays.asList(
            NAME,
            ID,
            CLASSIFICATION_TYPE,
            DESCRIPTION,
            LAST_MODIFIED,
            PRIMARY_LANGUAGE,
            COPYRIGHTED,
            INCLUDE_SHORTNAME,
            INCLUDE_NOTES,
            CONTACT_PERSON,
            OWNING_SECTION,
            LINKS_SELF_HREF,
            LINKS_CODES_HREF,
            LINKS_CODES_TEMPLATED,
            LINKS_CODES_AT_HREF,
            LINKS_CODES_AT_TEMPLATED,
            LINKS_VARIANT_HREF,
            LINKS_VARIANT_TEMPLATED,
            LINKS_VARIANT_AT_HREF,
            LINKS_VARIANT_AT_TEMPLATED,
            LINKS_CORRESPONDS_HREF,
            LINKS_CORRESPONDS_TEMPLATED,
            LINKS_CORRESPONDS_AT_HREF,
            LINKS_CORRESPONDS_AT_TEMPLATED,
            LINKS_CHANGES_HREF,
            LINKS_CHANGES_TEMPLATED
    );

    public static List<String> pathNamesClassificationXml = Arrays.asList(
            CLASSIFICATION_NAME,
            CLASSIFICATION_ID,
            CLASSIFICATION_CLASSIFICATION_TYPE,
            CLASSIFICATION_DESCRIPTION,
            CLASSIFICATION_LAST_MODIFIED,
            CLASSIFICATION_PRIMARY_LANGUAGE,
            CLASSIFICATION_COPYRIGHTED,
            CLASSIFICATION_INCLUDE_SHORTNAME,
            CLASSIFICATION_INCLUDE_NOTES,
            CLASSIFICATION_CONTACT_PERSON,
            CLASSIFICATION_OWNING_SECTION
    );

    public static List<String> pathNamesClassificationVariants = Arrays.asList(
            NAME,
            ID,
            CONTACT_PERSON,
            OWNING_SECTION,
            LAST_MODIFIED,
            PUBLISHED_NB,
            PUBLISHED_EN,
            PUBLISHED_NN,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesClassificationVariantsXml = Arrays.asList(
            NAME,
            ID,
            VALID_FROM,
            LAST_MODIFIED,
            INTRODUCTION,
            CONTACT_PERSON_NAME,
            CONTACT_PERSON_EMAIL,
            CONTACT_PERSON_PHONE,
            OWNING_SECTION,
            PUBLICATIONS,
            DERIVED_FROM,
            LEGAL_BASE,
            LINK_REL,
            LINK_HREF
    );

    public static List<String> pathNamesClassificationsPage = Arrays.asList(
            NAME,
            ID,
            CLASSIFICATION_TYPE,
            LAST_MODIFIED,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesClassificationsPageXml = Arrays.asList(
            NAME,
            ID,
            CLASSIFICATION_TYPE,
            LAST_MODIFIED,
            LINK_REL,
            LINK_HREF
    );

    public static List<String> pathNamesVersion = Arrays.asList(
            NAME,
            ID,
            VALID_FROM,
            VALID_TO,
            LAST_MODIFIED,
            PUBLISHED,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesClassificationFamilies = Arrays.asList(
            NAME,
            ID,
            NUMBER_OF_CLASSIFICATIONS,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesClassificationFamilyById = Arrays.asList(
            NAME,
            ID,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesClassificationFamilyByIdXml = Arrays.asList(
            NAME,
            ID,
            LINK_REL,
            LINK_HREF
    );

    public static List<String> pathNamesVersionsById = Arrays.asList(
            NAME,
            ID,
            VALID_FROM,
            VALID_TO,
            LAST_MODIFIED,
            INTRODUCTION,
            LEGAL_BASE,
            PUBLICATIONS,
            DERIVED_FROM,
            CONTACT_PERSON,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesVersionsClassificationItems = Arrays.asList(
            CODE,
            PARENT_CODE,
            LEVEL,
            NAME,
            SHORT_NAME,
            NOTES
    );

    public static List<String> pathNamesCorrespondenceTableVersions = Arrays.asList(
            NAME,
            ID,
            CONTACT_PERSON,
            OWNING_SECTION,
            SOURCE,
            SOURCE_ID,
            TARGET,
            TARGET_ID,
            CHANGE_TABLE,
            LAST_MODIFIED,
            SOURCE_LEVEL,
            TARGET_LEVEL,
            DESCRIPTION,
            PUBLISHED_NB,
            PUBLISHED_NN,
            PUBLISHED_EN,
            LINKS_SELF_HREF,
            LINKS_SOURCE_HREF,
            LINKS_SOURCE_TEMPLATED,
            LINKS_TARGET_HREF,
            LINKS_TARGET_TEMPLATED
    );

    public static List<String> pathNamesCorrespondenceTable = Arrays.asList(
            NAME,
            ID,
            CONTACT_PERSON,
            OWNING_SECTION,
            SOURCE,
            SOURCE_ID,
            TARGET,
            TARGET_ID,
            CHANGE_TABLE,
            LAST_MODIFIED,
            SOURCE_LEVEL,
            TARGET_LEVEL,
            DESCRIPTION,
            LINKS_SELF_HREF,
            LINKS_SOURCE_HREF,
            LINKS_SOURCE_TEMPLATED,
            LINKS_TARGET_HREF,
            LINKS_TARGET_TEMPLATED
    );

    public static List<String> pathNamesVariantById = Arrays.asList(
            NAME,
            ID,
            CONTACT_PERSON,
            OWNING_SECTION,
            LAST_MODIFIED,
            VALID_FROM,
            VALID_TO,
            INTRODUCTION,
            LINKS_SELF_HREF
    );

    public static List<String> pathNamesClassificationFamiliesXml = Arrays.asList(
            NAME,
            ID,
            NUMBER_OF_CLASSIFICATIONS,
            LINK_REL,
            LINK_HREF
    );

    public static List<String> pathNamesClassificationsLinks = Arrays.asList(
            LINKS_FIRST_HREF,
            LINKS_SELF_HREF,
            LINKS_NEXT_HREF,
            LINKS_LAST_HREF,
            LINKS_SEARCH_HREF,
            LINKS_SEARCH_TEMPLATED

    );

    public static List<String> pathNamesXmlLinks = Arrays.asList(
            LINK_REL,
            LINK_HREF

    );

    public static List<String> pathNamesCorrespondenceTableXml = Arrays.asList(
            CORRESPONDENCE_TABLE_NAME,
            CORRESPONDENCE_TABLE_OWNING_SECTION

    );

}
