package no.ssb.klass.core.model;

public record Email(
        String to,
        String subject,
        String body
) {
}
