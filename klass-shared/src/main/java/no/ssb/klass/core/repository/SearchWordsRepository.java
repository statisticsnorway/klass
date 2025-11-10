package no.ssb.klass.core.repository;

import no.ssb.klass.core.model.SearchWords;
import no.ssb.klass.core.service.dto.StatisticalEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;

public interface SearchWordsRepository extends JpaRepository<SearchWords, Long> {
    @Query(
            value =
                    "select count(distinct sw.searchString) from SearchWords sw "
                            + "where sw.timeStamp between :fromDate and :toDate")
    int getNumberOfSearchWords(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(
            value =
                    "select count(*) from SearchWords sw "
                            + "where sw.hit = false and sw.timeStamp between :fromDate and :toDate")
    int getNumberOfMiss(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(
            value =
                    "select new no.ssb.klass.core.service.dto.AccessCounterDto(sw.searchString, count(sw.searchString)) "
                            + "from SearchWords sw "
                            + "where sw.hit = :hit and sw.timeStamp between :fromDate and :toDate group by sw.searchString "
                            + "order by count(sw.searchString) desc, sw.searchString")
    Page<StatisticalEntity> getSearchWords(
            @Param("hit") Boolean hit,
            @Param("fromDate") Date fromDate,
            @Param("toDate") Date toDate,
            Pageable p);

    @Query(
            value =
                    "select new no.ssb.klass.core.service.dto.AccessCounterDto(sw.searchString, count(sw.searchString)) "
                            + "from SearchWords sw "
                            + "where sw.timeStamp between :fromDate and :toDate group by sw.searchString "
                            + "order by count(sw.searchString) desc, sw.searchString")
    Page<StatisticalEntity> getSearchWords(
            @Param("fromDate") Date fromDate, @Param("toDate") Date toDate, Pageable p);
}
