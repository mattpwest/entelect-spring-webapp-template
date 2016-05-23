package za.co.entelect.services.providers.exceptions.tournament;

public class TournamentMatchGenerationFailedException extends Exception {
    public TournamentMatchGenerationFailedException(Throwable cause) {
        super("Tournament match generation failed.", cause);
    }
}
