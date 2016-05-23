package za.co.entelect.services.providers.exceptions;

public class EmailInUseException extends Exception {

    public EmailInUseException() {
        super();
    }

    public EmailInUseException(final String message) {
        super(message);
    }

    public EmailInUseException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
