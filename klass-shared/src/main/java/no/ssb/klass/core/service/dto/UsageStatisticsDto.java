package no.ssb.klass.core.service.dto;

public class UsageStatisticsDto {

    public final int numberOfClassifications;
    public final int numberOfSearchReturnedNull;
    public final int totalSearchWords;

    public UsageStatisticsDto(
            int numberOfClassifications, int numberOfSearchReturnedNull, int totalSearchWords) {
        this.numberOfClassifications = numberOfClassifications;
        this.numberOfSearchReturnedNull = numberOfSearchReturnedNull;
        this.totalSearchWords = totalSearchWords;
    }
}
