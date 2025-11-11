package no.ssb.klass.core.service.dto;

public class AccessCounterDto implements StatisticalEntity {
    private final String entityName;
    private final Long count;

    public AccessCounterDto(String entityName, Long count) {
        this.entityName = entityName;
        this.count = count;
    }

    public String getName() {
        return entityName;
    }

    public Long getCount() {
        return count;
    }
}
