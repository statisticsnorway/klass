package no.ssb.klass.mail;

import no.ssb.klass.mail.models.Email;
import no.ssb.klass.mail.services.MailService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
public class TestMailService implements MailService {

    private static final Logger log = LoggerFactory.getLogger(TestMailService.class);

    @Override
    public void sendMail(Email email) {
        log.info("Sending email {}", email);
    }
}
