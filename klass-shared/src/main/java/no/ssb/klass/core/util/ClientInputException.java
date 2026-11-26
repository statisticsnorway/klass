package no.ssb.klass.core.util;

/**
 * A validation failure whose message is intended for clients, rendered as data rather than HTML.
 */
public final class ClientInputException extends IllegalArgumentException {
    public ClientInputException(String message) {
        super(message);
    }
}
