package za.co.entelect.comms.exceptions;

/**
 * General mail exception. Root of the MailService exception hierarchy.
 */
public class MailException extends Exception {

    public MailException() {
        super();
    }

    public MailException(final String message) {
        super(message);
    }

    public MailException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
