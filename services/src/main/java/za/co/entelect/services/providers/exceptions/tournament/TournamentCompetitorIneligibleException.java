package za.co.entelect.services.providers.exceptions.tournament;

public class TournamentCompetitorIneligibleException extends Exception {
    public TournamentCompetitorIneligibleException(String reason) {
        super(String.format("Failed to join, due to: %s", reason));
    }
}
