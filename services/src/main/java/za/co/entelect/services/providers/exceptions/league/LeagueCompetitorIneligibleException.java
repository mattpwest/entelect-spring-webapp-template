package za.co.entelect.services.providers.exceptions.league;

public class LeagueCompetitorIneligibleException extends Exception {
    public LeagueCompetitorIneligibleException(String reason) {
        super(String.format("Failed to join, due to: %s", reason));
    }
}
