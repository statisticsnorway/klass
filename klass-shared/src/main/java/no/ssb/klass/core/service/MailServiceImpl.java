package no.ssb.klass.core.service;

import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Session;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import no.ssb.klass.core.config.ConfigurationProfiles;

@Service
@Profile("!" + ConfigurationProfiles.MOCK_MAILSERVER)
public class MailServiceImpl implements MailService {
    private final JavaMailSender javaMailSender;

    @Autowired
    MailServiceImpl(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendMail(String to, String subject, String body) {
        try {
            Session session = Session.getDefaultInstance(System.getProperties());
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject(subject);
            message.setContent(body, "text/html; charset=utf-8");
            javaMailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
