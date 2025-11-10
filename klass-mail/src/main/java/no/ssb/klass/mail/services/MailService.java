package no.ssb.klass.mail.services;

import no.ssb.klass.mail.models.Email;

public interface MailService {
  void sendMail(Email email);
}
