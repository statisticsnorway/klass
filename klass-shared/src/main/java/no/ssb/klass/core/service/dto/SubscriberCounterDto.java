package no.ssb.klass.core.service.dto;

/**
 * Created by jro on 16.02.2017.
 */

public class SubscriberCounterDto implements StatisticalEntity {
        private String domain;
        private long count;


    public SubscriberCounterDto(String domain, long sub_count) {
            this.domain = domain;
            this.count = sub_count;
        }
    public String getName() {
        return domain;
    }

    public Long getCount() {
        return count;
    }

}
