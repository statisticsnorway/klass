package no.ssb.klass.core.exception;

/** For use when sending emails fails. */
public class KlassEmailException extends Exception {
    public KlassEmailException(String errorMessage, Exception e) {
        super(errorMessage, e);
    }
}
