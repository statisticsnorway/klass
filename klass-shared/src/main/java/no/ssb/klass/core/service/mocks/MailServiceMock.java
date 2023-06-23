package no.ssb.klass.core.service.mocks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import no.ssb.klass.core.config.ConfigurationProfiles;
import no.ssb.klass.core.service.MailService;

@Service
@Profile(ConfigurationProfiles.MOCK_MAILSERVER)
public class MailServiceMock implements MailService {
    private static final Logger log = LoggerFactory.getLogger(MailServiceMock.class);

    @Override
    public void sendMail(String to, String subject, String body) {
        log.info("Sending email to: " + to + "\n\nSubject:\n" + subject + "\n\nBody:\n" + body);
    }
}
