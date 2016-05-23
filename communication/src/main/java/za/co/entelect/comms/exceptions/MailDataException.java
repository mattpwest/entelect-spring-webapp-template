package za.co.entelect.comms.exceptions;

/**
 * Thrown in the event that we are unable to store an email due to a problem mapping its data object to JSON.
 */
public class MailDataException extends MailException {

    public MailDataException() {
        super();
    }

    public MailDataException(final String message) {
        super(message);
    }

    public MailDataException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
