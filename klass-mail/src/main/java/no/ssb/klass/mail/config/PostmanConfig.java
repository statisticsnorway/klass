package no.ssb.klass.mail.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "messaging.postman")
public class PostmanConfig {

    private String publisherAppName;

    private String pubsubTopicIncoming;

    private String fromDisplayName;

    public @NonNull String getPublisherAppName() {
        return publisherAppName;
    }

    public void setPublisherAppName(String publisherAppName) {
        this.publisherAppName = publisherAppName;
    }

    public @NonNull String getPubsubTopicIncoming() {
        return pubsubTopicIncoming;
    }

    public void setPubsubTopicIncoming(String pubsubTopicIncoming) {
        this.pubsubTopicIncoming = pubsubTopicIncoming;
    }

    public @NonNull String getFromDisplayName() {
        return fromDisplayName;
    }

    public void setFromDisplayName(String fromDisplayName) {
        this.fromDisplayName = fromDisplayName;
    }
}
