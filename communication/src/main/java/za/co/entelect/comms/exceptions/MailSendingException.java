package za.co.entelect.comms.exceptions;

/**
 * Thrown in the event that we are unable to send a mail over SMTP.
 */
public class MailSendingException extends MailException {

    public MailSendingException() {
        super();
    }

    public MailSendingException(final String message) {
        super(message);
    }

    public MailSendingException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
