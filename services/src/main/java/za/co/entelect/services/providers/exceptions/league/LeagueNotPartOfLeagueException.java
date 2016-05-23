package za.co.entelect.services.providers.exceptions.league;

public class LeagueNotPartOfLeagueException extends Exception {
    public LeagueNotPartOfLeagueException() {
        super("You are not part of this league.");
    }

    public LeagueNotPartOfLeagueException(String msg) {
        super(msg);
    }
}
