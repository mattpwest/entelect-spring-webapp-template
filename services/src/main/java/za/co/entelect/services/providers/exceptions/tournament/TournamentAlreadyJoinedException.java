package za.co.entelect.services.providers.exceptions.tournament;

public class TournamentAlreadyJoinedException extends Exception {
    public TournamentAlreadyJoinedException() {
        super("You have already joined this tournament.");
    }
    
    public TournamentAlreadyJoinedException(String message) {
        super(message);
    }
}
