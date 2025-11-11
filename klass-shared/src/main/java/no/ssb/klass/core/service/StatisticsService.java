package no.ssb.klass.core.service;

import no.ssb.klass.core.model.ClassificationSeries;
import no.ssb.klass.core.model.ClassificationType;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.service.dto.*;
import no.ssb.klass.core.service.enums.ReportMode;
import no.ssb.klass.core.service.enums.SubscriberMode;
import no.ssb.klass.core.service.enums.UseStatisticsMode;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface StatisticsService {
    ContentUseStatisticDto generateContentUseStat(
            String section, ClassificationType classificationType);

    List<ClassificationReportDto> getPublishedClassificationReport(
            String section, ClassificationType classificationType, ReportMode operation);

    List<ClassificationVersionReportDto> getPublishedVersionReport(
            String section, ClassificationType classificationType, ReportMode operation);

    UsageStatisticsDto getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate);

    void addSearchWord(String searchWord, boolean hit);

    void addUseForClassification(ClassificationSeries classificationSeries);

    Page<StatisticalEntity> getUsageStatistics(
            LocalDate fromSearchDate,
            LocalDate toSearchDate,
            UseStatisticsMode operation,
            Pageable pageable);

    Page<StatisticalEntity> getStaticalUnitsOverView(Pageable pageable);

    List<ClassificationReportDto> getAllClassificationSeriesForStaticalUnit(
            StatisticalUnit statisticalUnit);

    SubscriberStatisticsDto getSubscriberStatistics();

    Page<StatisticalEntity> getSubscriberReport(SubscriberMode operation, Pageable p);

    Page<StatisticalEntity> getSubscriberDomainReport(SubscriberMode operation, Pageable p);

    Page<StatisticalEntity> getSubscriberClassificationReport(SubscriberMode operation, Pageable p);
}
