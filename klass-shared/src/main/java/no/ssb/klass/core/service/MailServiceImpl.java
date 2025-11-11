package no.ssb.klass.core.service;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.model.Email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

@Service
@Profile("!" + ConfigurationProfiles.MOCK_MAILSERVER)
public class MailServiceImpl implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceImpl.class);

    private final RestTemplate klassMail;

    private final String mailPath = "/mail";

    public MailServiceImpl(
            @NonNull @Value("${klass.env.client.klass-mail.url}") String klassMailUrl,
            RestTemplateBuilder restTemplateBuilder) {
        Assert.notNull(klassMailUrl, "URL for klass-mail must not be null");
        klassMail =
                restTemplateBuilder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(klassMailUrl))
                        .build();
        log.info(
                "Created MailService for URL {}",
                klassMail.getUriTemplateHandler().expand(mailPath));
    }

    @Override
    public void sendMail(String to, String subject, String body) {
        ResponseEntity<Void> response =
                klassMail.exchange(
                        RequestEntity.post(mailPath).body(new Email(to, subject, body)),
                        Void.class);
        log.info("Sent mail {} to {}, result: {}", subject, to, response.getStatusCodeValue());
    }
}
