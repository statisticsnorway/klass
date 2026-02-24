package no.ssb.klass.designer.exceptions;

/**
 * The purpose of this class is to make parent for all exceptions that Klass needs to throw so we can filter them in
 * try-catch blocks.
 * 
 * @author Mads Lundemo, SSB.
 */
public abstract class KlassRuntimeException extends RuntimeException {

    public KlassRuntimeException() {
        super();
    }

    public KlassRuntimeException(String message) {
        super(message);
    }

    public KlassRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public KlassRuntimeException(Throwable cause) {
        super(cause);
    }

}
