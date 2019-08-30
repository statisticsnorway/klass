package no.ssb.klass.core.repository;

import java.util.Date;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import no.ssb.klass.core.model.ClassificationAccessCounter;
import no.ssb.klass.core.service.dto.StatisticalEntity;

@Repository
public interface ClassificationAccessRepository extends JpaRepository<ClassificationAccessCounter, Long> {
    @Query(value = "select count(*) from ClassificationAccessCounter c "
            + "where c.timeStamp between :fromDate and :toDate")
    int getAccessSum(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate);

    @Query(value = "select new no.ssb.klass.core.service.dto.AccessCounterDto("
            + "case cs.primaryLanguage when 0 then cs.nameNo when 1 then cs.nameNn when 2 then cs.nameEn end as name, count(cs.id)) "
            + "from ClassificationAccessCounter cac join "
            + "cac.classificationSeries cs where cac.timeStamp between :fromDate and :toDate group by cs.id "
            + "order by count(cs.id) desc, name")
    Page<StatisticalEntity> getClassificationsCount(@Param("fromDate") Date fromDate, @Param("toDate") Date toDate,
            Pageable p);

}
