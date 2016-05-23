package za.co.entelect.services.providers.exceptions.tournament;

public class TournamentFullException extends Exception {
    public TournamentFullException() {
        super("The tournament is full.");
    }
}
