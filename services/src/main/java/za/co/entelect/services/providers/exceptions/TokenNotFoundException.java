package za.co.entelect.services.providers.exceptions;

public class TokenNotFoundException extends Exception {

    public TokenNotFoundException() {
        super("Token not found.");
    }
}
