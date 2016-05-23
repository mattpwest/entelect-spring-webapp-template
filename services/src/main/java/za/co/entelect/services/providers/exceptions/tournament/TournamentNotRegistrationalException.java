package za.co.entelect.services.providers.exceptions.tournament;

public class TournamentNotRegistrationalException extends Exception {
    public TournamentNotRegistrationalException() {
        super("This is not a registrational tournament.");
    }
}
