package no.ssb.klass.core.util;

/** An expected client-facing failure; its message must not contain private diagnostics. */
@SuppressWarnings("serial")
public class ClientException extends RuntimeException {
    public ClientException(String message) {
        super(message);
    }
}
