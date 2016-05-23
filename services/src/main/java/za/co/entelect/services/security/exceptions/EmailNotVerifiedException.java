package za.co.entelect.services.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class EmailNotVerifiedException extends AuthenticationException {

    public EmailNotVerifiedException() {
        super("Your email has not been verified.");
    }
}
