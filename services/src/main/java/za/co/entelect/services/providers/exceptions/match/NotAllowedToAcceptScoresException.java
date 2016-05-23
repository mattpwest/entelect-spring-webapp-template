package za.co.entelect.services.providers.exceptions.match;

public class NotAllowedToAcceptScoresException extends Exception {

    public NotAllowedToAcceptScoresException(String message) {
        super(message);
    }

    public NotAllowedToAcceptScoresException() {
        super("You have insufficient permissions to accept the score.");
    }
}
