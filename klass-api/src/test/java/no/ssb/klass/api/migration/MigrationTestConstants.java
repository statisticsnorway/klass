package no.ssb.klass.api.migration;

import java.util.Arrays;
import java.util.List;


public final class MigrationTestConstants {

    public static final String BASE_PATH = "/api/klass";
    public static final String CLASSIFICATIONS_PATH = "/classifications";

    public static final String EMBEDDED_CLASSIFICATIONS = "_embedded.classifications";
    public static final String EMBEDDED_PAGE = "_embedded.page";
    public static final String PAGE = "page";

    public static final String SOURCE_API_CHECK = "Source API is not ready";
    public static final String TARGET_API_CHECK  = "Target API is not ready";

    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String CLASSIFICATION_TYPE = "classificationType";
    public static final String LINKS = "_links";
    public static final String DESCRIPTION = "description";
    public static final String LAST_MODIFIED = "lastModified";
    public static final String PRIMARY_LANGUAGE = "primaryLanguage";
    public static final String COPYRIGHTED = "copyrighted";
    public static final String INCLUDE_SHORTNAME = "includeShortName";
    public static final String INCLUDE_NOTES = "includeNotes";
    public static final String CONTACT_PERSON = "contactPerson";
    public static final String CONTACT_PERSON_NAME = "contactPerson.name";
    public static final String CONTACT_PERSON_EMAIL = "contactPerson.email";
    public static final String CONTACT_PERSON_PHONE = "contactPerson.phone";
    public static final String OWNING_SECTION = "owningSection";
    public static final String STATISTICAL_UNITS = "statisticalUnits";
    public static final String VERSIONS = "versions";
    public static final String VALID_FROM = "validFrom";
    public static final String VALID_TO = "validTo";
    public static final String PUBLISHED = "published";

    public static final String LINKS_SELF_HREF= "_links.self.href";
    public static final String LINKS_FIRST_HREF= "_links.first.href";
    public static final String HREF= "href";
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

    public static final String NB= "nb";
    public static final String NN= "nn";
    public static final String EN= "en";

    public static final String LANGUAGE_PARAM= "language";

    public static final String INCLUDE_FUTURE_TRUE_PARAM= "?includeFuture=true";

    public static final String SEARCH= "search?";
    public static final String CODES= "codes";
    public static final String CODES_AT= "codesAt";
    public static final String DATE= "date";
    public static final String RANGE_FROM= "from";
    public static final String RANGE_TO= "to";


    public static List<String> pathNamesClassification = Arrays.asList(
            ID,
            NAME,
            CLASSIFICATION_TYPE,
            DESCRIPTION,
            LAST_MODIFIED,
            PRIMARY_LANGUAGE,
            COPYRIGHTED,
            INCLUDE_SHORTNAME,
            INCLUDE_NOTES,
            CONTACT_PERSON,
            OWNING_SECTION,
            CONTACT_PERSON_EMAIL,
            CONTACT_PERSON_NAME,
            CONTACT_PERSON_PHONE,
            STATISTICAL_UNITS
    );

    public static List<String> pathNamesClassificationsPage = Arrays.asList(
            NAME,
            ID,
            CLASSIFICATION_TYPE,
            LAST_MODIFIED,
            LINKS_SELF_HREF
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

    public static List<String> pathNamesClassificationsLinks = Arrays.asList(
            LINKS_FIRST_HREF,
            LINKS_SELF_HREF,
            LINKS_NEXT_HREF,
            LINKS_LAST_HREF,
            LINKS_SEARCH_HREF,
            LINKS_SEARCH_TEMPLATED

    );

    public static List<String> pathNamesClassificationLinks = Arrays.asList(
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

}
