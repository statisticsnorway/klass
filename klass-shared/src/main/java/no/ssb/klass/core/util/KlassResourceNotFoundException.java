package no.ssb.klass.core.util;

/** A missing domain resource with a client-facing message, rendered as data rather than HTML. */
public class KlassResourceNotFoundException extends RuntimeException {
    public KlassResourceNotFoundException(String message) {
        super(message);
    }
}
