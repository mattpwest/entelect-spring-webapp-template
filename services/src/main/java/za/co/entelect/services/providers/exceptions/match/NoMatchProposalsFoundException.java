package za.co.entelect.services.providers.exceptions.match;

public class NoMatchProposalsFoundException extends Exception {
    public NoMatchProposalsFoundException() {
        super("There were no match proposals found for the match");
    }

    public NoMatchProposalsFoundException(String msg) {
        super(msg);
    }
}
