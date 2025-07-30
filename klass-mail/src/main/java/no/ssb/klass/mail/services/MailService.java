package no.ssb.klass.mail.services;

public interface MailService {
    void sendMail(String to, String subject, String body);
}
