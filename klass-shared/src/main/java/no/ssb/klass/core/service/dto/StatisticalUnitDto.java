package no.ssb.klass.core.service.dto;

import no.ssb.klass.core.model.Language;
import no.ssb.klass.core.model.StatisticalUnit;

public class StatisticalUnitDto implements StatisticalEntity {
    private final StatisticalUnit statisticalUnit;
    private final Long count;

    public StatisticalUnitDto(StatisticalUnit statisticalUnit, Long count) {
        this.statisticalUnit = statisticalUnit;
        this.count = count;
    }

    public String getName() {
        return statisticalUnit.getName(Language.getDefault());
    }

    public Long getCount() {
        return count;
    }

    public StatisticalUnit getStatisticalUnit() {
        return statisticalUnit;
    }
}
