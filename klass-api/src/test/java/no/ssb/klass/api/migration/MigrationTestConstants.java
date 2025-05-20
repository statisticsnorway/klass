package no.ssb.klass.api.migration;

import java.util.Arrays;
import java.util.List;


public final class MigrationTestConstants {

    public static final String BASE_PATH = "/api/klass";
    public static final String CLASSIFICATIONS_PATH = "/classifications";
    public static final String TOTAL_ELEMENTS = "page.totalElements";
    public static final String EMBEDDED_CLASSIFICATIONS = "_embedded.classifications";
    public static final String EMBEDDED_PAGE = "_embedded.page";
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

    public static final String FIRST= "first";
    public static final String FIRST_HREF= "first.href";
    public static final String LINKS_FIRST_HREF= "_links.first.href";
    public static final String SELF= "self";
    public static final String SELF_HREF= "self.href";
    public static final String HREF= "href";
    public static final String NEXT= "next";
    public static final String NEXT_HREF= "next.href";
    public static final String LINKS_NEXT_HREF= "_links.next.href";
    public static final String SEARCH= "search";
    public static final String SEARCH_HREF= "search.href";
    public static final String LINKS_SEARCH_HREF= "_links.search.href";
    public static final String LINKS_SEARCH_TEMPLATED= "_links.search.templated";
    public static final String LAST= "last";
    public static final String LAST_HREF= "last.href";
    public static final String LINKS_LAST_HREF= "_links.last.href";


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
            LINKS
    );

    public static List<String> pathNamesLinks = Arrays.asList(
            LINKS_FIRST_HREF,
            LINKS_SELF_HREF,
            LINKS_NEXT_HREF,
            LINKS_LAST_HREF,
            LINKS_SEARCH_HREF,
            LINKS_SEARCH_TEMPLATED

    );


}
