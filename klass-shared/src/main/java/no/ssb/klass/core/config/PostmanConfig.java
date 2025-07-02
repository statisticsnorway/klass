package no.ssb.klass.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.lang.NonNull;
import org.springframework.validation.annotation.Validated;

@Validated
@Profile("!" + ConfigurationProfiles.MOCK_MAILSERVER)
@ConfigurationProperties(prefix = "messaging.postman")
public class PostmanConfig {

    private String publisherAppName;

    private String pubsubTopicIncoming;

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

}
