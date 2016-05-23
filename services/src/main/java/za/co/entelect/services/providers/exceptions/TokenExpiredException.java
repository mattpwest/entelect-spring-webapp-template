package za.co.entelect.services.providers.exceptions;

public class TokenExpiredException extends Exception {

    public TokenExpiredException() {
        super("Token expired.");
    }
}
