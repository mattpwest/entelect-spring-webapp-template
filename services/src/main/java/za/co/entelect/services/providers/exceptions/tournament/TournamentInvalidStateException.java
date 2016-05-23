package za.co.entelect.services.providers.exceptions.tournament;

public class TournamentInvalidStateException extends Exception {

    public TournamentInvalidStateException() {
        super("Tournament is not in the correct state to allow that operation.");
    }

    public TournamentInvalidStateException(String message) {
        super(message);
    }

    public TournamentInvalidStateException(String message, Throwable cause) {
        super(message, cause);
    }
}
