package no.ssb.klass.core.service;

public interface MailService {
    void sendMail(String to, String subject, String body);
}