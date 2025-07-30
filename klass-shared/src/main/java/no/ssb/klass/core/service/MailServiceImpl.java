package no.ssb.klass.core.service;

import no.ssb.klass.mail.models.Email;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

public class MailServiceImpl implements MailService {


    private final RestTemplate klassMail;

    @Value("${klass.env.client.klass-mail.host}")
    private String klassMailHost;

    public MailServiceImpl(RestTemplateBuilder restTemplateBuilder) {
        this.klassMail = restTemplateBuilder.uriTemplateHandler(new DefaultUriBuilderFactory("http://" + klassMailHost)).build();
    }
    
    @Override
    public void sendMail(String to, String subject, String body) {
        klassMail.postForEntity("/mail", new Email(to, subject, body), Email.class);
    }
}
