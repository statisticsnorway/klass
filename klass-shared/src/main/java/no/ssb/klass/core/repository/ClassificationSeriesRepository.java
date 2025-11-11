package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.MigratedFrom;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.model.User;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.core.service.dto.ClassificationVersionReportDto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Repository
public interface ClassificationSeriesRepository
        extends JpaRepository<ClassificationSeries, Long>,
                JpaSpecificationExecutor<ClassificationSeries> {

    String CHECKPARAMETERS =
            " and (:classificationType is null or :classificationType = classification.classificationType) and "
                    + "(:section is null or :section = user.section)";

    @Query(
            value =
                    "select distinct classification.contactPerson.section from ClassificationSeries classification")
    Set<String> findAllResponsibleSections();

    @Query(
            value =
                    "select distinct classification.contactPerson.section from ClassificationSeries as classification "
                            + "     join classification.classificationVersions as version where version.classification.id = classification.id "
                            + "     and (version.published.published_en = true "
                            + "             or version.published.published_no = true "
                            + "         or version.published.published_nn = true) "
                            + "        and version.deleted = false "
                            + "        and classification.deleted = false "
                            + "        and classification.copyrighted = false")
    Set<String> findResponsibleSectionsWithPublishedVersions();

    @Query(
            value =
                    "select count(*) from ClassificationSeries classification join classification.contactPerson user where "
                            + "classification.deleted = false"
                            + CHECKPARAMETERS)
    int finNumberOfClassifications(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select count(distinct classification.id) from ClassificationVersion version join version.classification classification join "
                            + "classification.contactPerson user where classification.deleted = false and version.deleted = false "
                            + "and (version.published.published_no = true or "
                            + "version.published.published_nn = true or version.published.published_en = true)"
                            + CHECKPARAMETERS)
    int findNumberOfPublishedClassifications(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select count(version.id) from ClassificationVersion version join version.classification classification join "
                            + "classification.contactPerson user where classification.deleted = false and version.deleted = false "
                            + "and (version.published.published_no = true "
                            + "or version.published.published_nn = true or version.published.published_en = true)"
                            + CHECKPARAMETERS)
    int findNumberOfPublishedVersionsAnyLanguages(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select count(version.id) from ClassificationVersion version join version.classification classification join "
                            + "classification.contactPerson user where classification.deleted = false and version.deleted = false "
                            + "and version.published.published_no = true "
                            + "and version.published.published_nn = true and version.published.published_en = true"
                            + CHECKPARAMETERS)
    int findNumberOfPublishedVersionsAllLanguages(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select new no.ssb.klass.core.service.dto.ClassificationReportDto(classification, user) "
                            + "from ClassificationSeries classification join classification.contactPerson user where "
                            + "classification.deleted = false"
                            + CHECKPARAMETERS
                            + " order by classification.id")
    List<ClassificationReportDto> getClassificationReport(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select new no.ssb.klass.core.service.dto.ClassificationReportDto(classification, user) "
                            + "from ClassificationVersion version join version.classification classification join "
                            + "classification.contactPerson user where classification.deleted = false and (version.published.published_no = true or "
                            + "version.published.published_nn = true or version.published.published_en = true)"
                            + CHECKPARAMETERS
                            + " order by classification.id")
    List<ClassificationReportDto> getPublishedClassificationReport(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select new no.ssb.klass.core.service.dto.ClassificationVersionReportDto(version, classification, user) "
                            + "from ClassificationVersion version join version.classification classification join "
                            + "classification.contactPerson user where classification.deleted = false and version.deleted = false "
                            + "and (version.published.published_no = true "
                            + "or version.published.published_nn = true or version.published.published_en = true)"
                            + CHECKPARAMETERS
                            + " order by version.id")
    List<ClassificationVersionReportDto> getPublishedVersionsAnyLanguages(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select new no.ssb.klass.core.service.dto.ClassificationVersionReportDto(version, classification, user) "
                            + "from ClassificationVersion version "
                            + "  join version.classification classification "
                            + "  join classification.contactPerson user "
                            + "where classification.deleted = false and version.deleted = false "
                            + "  and version.published.published_no = true "
                            + "  and version.published.published_nn = true and version.published.published_en = true "
                            + CHECKPARAMETERS
                            + "  order by version.id")
    List<ClassificationVersionReportDto> getPublishedVersionsAllLanguages(
            @Param("classificationType") ClassificationType classificationType,
            @Param("section") String section);

    @Query(
            value =
                    "select count(id) from ClassificationSeries where deleted = false and :user = contactPerson")
    int getNumberOfClassificationForUser(@Param("user") User user);

    @Modifying
    @Query(
            "update ClassificationSeries set contactPerson = :newUser where contactPerson = :oldUser and deleted = false")
    void updateUser(@Param("oldUser") User oldUser, @Param("newUser") User newUser);

    ClassificationSeries findByNameNoIgnoreCase(@Param("name") String name);

    ClassificationSeries findByNameNnIgnoreCase(@Param("name") String name);

    ClassificationSeries findByNameEnIgnoreCase(@Param("name") String name);

    long countByMigratedFrom(MigratedFrom migratedFrom);

    @Query("select id from ClassificationSeries")
    List<Long> findAllClassificationIds();

    List<ClassificationSeries> findByContactPerson(User contactPerson);

    @Modifying
    @Query("update ClassificationSeries set lastModified = :timeStamp where id = :classificationId")
    void updateClassificationLastModified(
            @Param("classificationId") Long classificationId, @Param("timeStamp") Date timeStamp);

    @Query("select distinct statisticalUnits from ClassificationSeries where deleted = false")
    List<StatisticalUnit> findAllClassificationStatisticalUnits();
}
