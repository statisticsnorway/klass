package no.ssb.klass.mail.controllers;

import no.ssb.klass.mail.models.Email;
import no.ssb.klass.mail.services.MailService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class MailController {

    private final MailService mailService;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping("/mail")
    public void sendMail(@RequestBody Email email) {
        mailService.sendMail(email);
    }
}
