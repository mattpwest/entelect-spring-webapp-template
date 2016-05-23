package za.co.entelect.services.providers.exceptions.league;

public class LeagueAlreadyJoinedException extends Exception {
    public LeagueAlreadyJoinedException() {
        super("You have already joined this league.");
    }
    public LeagueAlreadyJoinedException(String message) {
        super(message);
    }
}
