package no.ssb.klass.core.repository;

import java.util.List;
import no.ssb.klass.core.model.StatisticalUnit;
import no.ssb.klass.core.service.dto.ClassificationReportDto;
import no.ssb.klass.core.service.dto.StatisticalEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author Mads Lundemo, SSB.
 */
@Repository
public interface StatisticalUnitRepository extends JpaRepository<StatisticalUnit, Long> {
  @Query(
      value =
          "select new no.ssb.klass.core.service.dto.StatisticalUnitDto(su, count(cs)) "
              + "from ClassificationSeries cs join cs.statisticalUnits su where cs.deleted = false group by su.name order by su.name")
  Page<StatisticalEntity> getStaticalUnitsOverView(Pageable p);

  @Query(
      value =
          "select new no.ssb.klass.core.service.dto.ClassificationReportDto(cs, user) "
              + "from ClassificationSeries cs join cs.statisticalUnits su join cs.contactPerson user "
              + "where su=:statisticalUnit and cs.deleted = false")
  List<ClassificationReportDto> getAllClassificationSeriesForStaticalUnit(
      @Param("statisticalUnit") StatisticalUnit statisticalUnit);
}
