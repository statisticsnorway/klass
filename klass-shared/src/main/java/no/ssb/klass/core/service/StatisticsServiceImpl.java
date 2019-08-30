package no.ssb.klass.core.service;

import no.ssb.klass.core.model.*;
import no.ssb.klass.core.repository.*;
import no.ssb.klass.core.service.dto.*;
import no.ssb.klass.core.service.enums.ReportMode;
import no.ssb.klass.core.service.enums.SubscriberMode;
import no.ssb.klass.core.service.enums.UseStatisticsMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

@Service
@Transactional(readOnly = true)
public class StatisticsServiceImpl implements StatisticsService {

    private final ClassificationSeriesRepository classificationRepository;
    private final ClassificationAccessRepository classificationAccessRepository;
    private final SearchWordsRepository searchWordsRepository;
    private final StatisticalUnitRepository statisticalUnitRepository;
    private final SubscriberRepository subscriberRepository;
    private final Boolean MISSED = Boolean.FALSE;

    @Autowired
    public StatisticsServiceImpl(ClassificationSeriesRepository classificationRepository,
            ClassificationAccessRepository statisticalClassificationAccessCounterRepository,
            SearchWordsRepository searchWordsRepository, StatisticalUnitRepository statisticalUnitRepository,
            SubscriberRepository subscriberRepository) {

        this.classificationRepository = classificationRepository;
        this.classificationAccessRepository = statisticalClassificationAccessCounterRepository;
        this.searchWordsRepository = searchWordsRepository;
        this.statisticalUnitRepository = statisticalUnitRepository;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public ContentUseStatisticDto generateContentUseStat(String section, ClassificationType classificationType) {
        int numberOfClassifications = classificationRepository.finNumberOfClassifications(classificationType, section);
        int numberOfPublishedClassifications =
                classificationRepository.findNumberOfPublishedClassifications(classificationType, section);

        int numberOfPublishedVersionsAnyLanguages =
                classificationRepository.findNumberOfPublishedVersionsAnyLanguages(classificationType, section);

        int numberOfPublishedVersionsAllLanguages =
                classificationRepository.findNumberOfPublishedVersionsAllLanguages(classificationType, section);

        return new ContentUseStatisticDto(numberOfClassifications, numberOfPublishedClassifications,
                numberOfClassifications - numberOfPublishedClassifications,
                numberOfPublishedVersionsAnyLanguages - numberOfPublishedVersionsAllLanguages);
    }

    @Override
    public List<ClassificationReportDto> getPublishedClassificationReport(String section, ClassificationType classificationType, ReportMode operation) {

        switch (operation) {
            case TOTAL:
                return classificationRepository.getClassificationReport(classificationType, section);
            case PUBLISHED:
                return classificationRepository.getPublishedClassificationReport(classificationType, section);
                
            default:
                throw new IllegalStateException("Gitt operasjon ikke implementert: " + operation);
        }
    }
    @Override
    public List<ClassificationVersionReportDto> getPublishedVersionReport(String section, ClassificationType classificationType, ReportMode operation) {

        switch (operation) {
            case ANY_LANGUAGE:
                return classificationRepository.getPublishedVersionsAnyLanguages(classificationType, section);

            case ALL_LANGUAGE:
                return classificationRepository.getPublishedVersionsAllLanguages(classificationType, section);
            default:
                throw new IllegalStateException("Gitt operasjon ikke implementert: " + operation);
        }
    }

    @Override
    public UsageStatisticsDto getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate) {
        checkNotNull(fromSearchDate);
        checkNotNull(toSearchDate);
        Instant fromDate = fromSearchDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toDate = toSearchDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        int usageSumC = classificationAccessRepository.getAccessSum(Date.from(fromDate), Date.from(toDate));
        int numberOfMisses = searchWordsRepository.getNumberOfMiss(Date.from(fromDate), Date.from(toDate));
        int numberOfSearchWords = searchWordsRepository.getNumberOfSearchWords(Date.from(fromDate), Date.from(toDate));
        return new UsageStatisticsDto(usageSumC, numberOfMisses, numberOfSearchWords);
    }

    @Override
    public void addUseForClassification(ClassificationSeries classificationSeries) {
        ClassificationAccessCounter loggUse = new ClassificationAccessCounter(classificationSeries);
        classificationAccessRepository.save(loggUse);
    }

    @Override
    public void addSearchWord(String searchWord, boolean hit) {
        SearchWords searchWords = new SearchWords(searchWord, hit);
        searchWordsRepository.save(searchWords);
    }

    @Override
    public Page<StatisticalEntity> getUsageStatistics(LocalDate fromSearchDate, LocalDate toSearchDate,
                                                 UseStatisticsMode operation, Pageable pageable) {
        
        Instant fromDate = fromSearchDate.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant toDate = toSearchDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        
        switch (operation) {
        case TOTAL_CLASSIFIC:
            return classificationAccessRepository.getClassificationsCount(Date.from(fromDate), Date.from(toDate), pageable);
        case NUMBEROF_SEARCH_RETURNED_NULL:
            return searchWordsRepository.getSearchWords(MISSED, Date.from(fromDate), Date.from(toDate), pageable);
        case TOTAL_SEARCH_WORDS:
            return searchWordsRepository.getSearchWords(Date.from(fromDate), Date.from(toDate), pageable);
        default:
            return null;
        }
    }

    @Override
    public Page<StatisticalEntity> getStaticalUnitsOverView(Pageable pageable) {
        return statisticalUnitRepository.getStaticalUnitsOverView(pageable);
    }

    @Override
    public List<ClassificationReportDto> getAllClassificationSeriesForStaticalUnit(
            StatisticalUnit statisticalUnit) {
        return  statisticalUnitRepository.getAllClassificationSeriesForStaticalUnit(statisticalUnit);
    }

    @Override
    public SubscriberStatisticsDto getSubscriberStatistics() {
        int numberOfSubscribers = subscriberRepository.findNumberOfSubscribers();
        int numberOfInternalSubscribers = subscriberRepository.findNumberOfInternalSubscribers();
        int numberOfExternalSubscribers = subscriberRepository.findNumberOfExternalSubscribers();
        return new SubscriberStatisticsDto(numberOfSubscribers, numberOfInternalSubscribers,
                numberOfExternalSubscribers);
    }

    @Override
    public Page<StatisticalEntity> getSubscriberReport(SubscriberMode operation, Pageable p) {
        switch (operation) {
        case TOTAL:
            return subscriberRepository.getSubscriberReportAll(p);
        case INTERNAL:
            return subscriberRepository.getSubscriberReportInternal(p);
        case EXTERNAL:
            return subscriberRepository.getSubscriberReportExternal(p);
        default:
            throw new IllegalStateException("Gitt operasjon ikke implementert: " + operation);
        }
    }

    @Override
    public Page<StatisticalEntity> getSubscriberDomainReport(SubscriberMode operation, Pageable p) {
        switch (operation) {
        case TOTAL:
            return subscriberRepository.getSubscriberDomainReportAll(p);
        case INTERNAL:
            return subscriberRepository.getSubscriberDomainReportInternal(p);
        case EXTERNAL:
            return subscriberRepository.getSubscriberDomainReportExternal(p);
        default:
            throw new IllegalStateException("Gitt operasjon ikke implementert: " + operation);

        }

    }

    @Override
    public Page<StatisticalEntity> getSubscriberClassificationReport(SubscriberMode operation, Pageable p) {
        switch (operation) {
        case TOTAL:
            return subscriberRepository.getSubscriberClassReportAll(p);
        case INTERNAL:
            return subscriberRepository.getSubscriberClassReportInternal(p);
        case EXTERNAL:
            return subscriberRepository.getSubscriberClassReportExternal(p);
        default:
            throw new IllegalStateException("Gitt operasjon ikke implementert: " + operation);
        }
    }
}
