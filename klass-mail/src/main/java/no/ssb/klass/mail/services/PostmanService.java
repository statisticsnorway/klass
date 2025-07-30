package no.ssb.klass.mail.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.core.publisher.PubSubPublisherTemplate;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import no.ssb.klass.mail.config.PostmanConfig;
import no.ssb.klass.mail.models.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostmanService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(PostmanService.class);

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final PostmanConfig postmanConfig;
    private final PubSubPublisherTemplate pubSubPublisher;

    @Autowired
    public PostmanService(
            PostmanConfig postmanConfig,
            PubSubPublisherTemplate pubSubPublisher) {
        this.postmanConfig = postmanConfig;
        this.pubSubPublisher = pubSubPublisher;

        log.info("Using Postman - publishing emails for app {} and topic {}",
                postmanConfig.getPublisherAppName(),
                postmanConfig.getPubsubTopicIncoming());
    }

    @Override
    public void sendMail(Email email) {
        log.debug("Postman sending mail to {} with subject {}", email.to(), email.subject());
        EmailRequest emailRequest = new EmailRequest(email, postmanConfig.getFromDisplayName());
        String topic = postmanConfig.getPubsubTopicIncoming();
        PubsubMessage message = pubsubMessageOf(new MessageRequest(emailRequest));
        pubSubPublisher.publish(topic, message);
        log.debug("Postman published {} to topic {}", message.getMessageId(), topic);
    }

    /**
     * Create a pubsub message from the given message request.
     */
    private PubsubMessage pubsubMessageOf(MessageRequest messageRequest) {
        TopicEntry topicEntry = new TopicEntry(messageRequest);
        return PubsubMessage.newBuilder().putAttributes("PUBLISHER_APP_NAME", postmanConfig.getPublisherAppName())
                .setData(topicEntry.toByteString())
                .build();
    }


    public static class EmailRequest {
        String message;
        String subject;
        String receiverEmailAddress;
        FromType fromType;
        String fromDisplayName;
        Boolean includeLogo;

        public EmailRequest(Email email, String fromDisplayName) {
            this.message = email.body();
            this.subject = email.subject();
            this.receiverEmailAddress = email.to();
            this.fromType = FromType.NO_REPLY;
            this.fromDisplayName = fromDisplayName;
            this.includeLogo = true;
        }

        public String getMessage() {
            return message;
        }

        public String getSubject() {
            return subject;
        }

        public String getReceiverEmailAddress() {
            return receiverEmailAddress;
        }

        public FromType getFromType() {
            return fromType;
        }

        public String getFromDisplayName() {
            return fromDisplayName;
        }

        public Boolean getIncludeLogo() {
            return includeLogo;
        }
    }

    public enum FromType {
        NO_REPLY, REPLY
    }

    public enum MessageChannel {
        EMAIL, SMS
    }

    public static class MessageRequest {

        private final String id;

        private final MessageChannel messageChannel = MessageChannel.EMAIL;

        private final EmailRequest emailRequest;

        public MessageRequest(EmailRequest emailRequest) {
            this.id = String.format("klass-subscriber-%s-%s", emailRequest.receiverEmailAddress, emailRequest.message.hashCode());
            this.emailRequest = emailRequest;
        }

        public String getId() {
            return id;
        }

        public MessageChannel getMessageChannel() {
            return messageChannel;
        }

        public EmailRequest getEmailRequest() {
            return emailRequest;
        }
    }


    public static class TopicEntry {
        MessageRequest data;

        public TopicEntry(MessageRequest messageRequest) {
            data = messageRequest;
        }

        public ByteString toByteString() {
            try {
                return ByteString.copyFrom(OBJECT_MAPPER.writeValueAsBytes(this));
            } catch (JsonProcessingException e) {
                throw new RuntimeException("Error mapping " + this.getClass().getSimpleName() + " object to JSON", e);
            }
        }

        public MessageRequest getData() {
            return data;
        }
    }

}
