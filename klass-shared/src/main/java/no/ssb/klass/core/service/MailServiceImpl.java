package no.ssb.klass.core.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import no.ssb.klass.core.config.ConfigurationProfiles;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Profile("!" + ConfigurationProfiles.MOCK_MAILSERVER)
public class MailServiceImpl implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final RestTemplate klassMail;

    private final String mailPath = "/mail";
    private final String klassMailUrl;

    public MailServiceImpl(
            @Value("${klass.env.client.klass-mail.url}")
            String klassMailUrl,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.klassMailUrl = klassMailUrl;
        Assert.notNull(klassMailUrl, "URL for klass-mail must not be null");
        klassMail = restTemplateBuilder.build();
        log.info("Created MailService for URL {}", klassMail.getUriTemplateHandler().expand(mailPath));
    }

    @Override
    public void sendMail(String to, String subject, String body) {
        ResponseEntity<Void> response = klassMail.exchange(
                RequestEntity.post(
                        UriComponentsBuilder.fromUriString(klassMailUrl).path(mailPath).build().toUri()
                ).body(
                        new Email(to, subject, body)
                ),
                Void.class
        );
        log.info("Sent mail {} to {}, result: {}", subject, to, response.getStatusCodeValue());
    }

    private static class Email {
        private final String string;
        private final String subject;
        private final String body;

        public Email(String to, String subject, String body) {
            string = to;
            this.subject = subject;
            this.body = body;
        }

        public String getString() {
            return string;
        }

        public String getSubject() {
            return subject;
        }

        public String getBody() {
            return body;
        }
    }
}
