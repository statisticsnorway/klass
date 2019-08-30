package no.ssb.klass.core.service.dto;

public class SubscriberStatisticsDto {
    public final int numberOfSubscribers, numberOfInternalSubscribers, numberOfExternalSubscribers;

    public SubscriberStatisticsDto(int numberOfSubscribers, int numberOfInternalSubscribers,
                                   int numberOfExternalSubscribers) {
        this.numberOfSubscribers = numberOfSubscribers;
        this.numberOfInternalSubscribers = numberOfInternalSubscribers;
        this.numberOfExternalSubscribers = numberOfExternalSubscribers;
    }
}
