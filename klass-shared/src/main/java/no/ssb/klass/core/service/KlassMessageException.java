package no.ssb.klass.core.service;

@SuppressWarnings("serial")
public class KlassMessageException extends Exception {
    public KlassMessageException(String errorMessage) {
        super(errorMessage);
    }
}
