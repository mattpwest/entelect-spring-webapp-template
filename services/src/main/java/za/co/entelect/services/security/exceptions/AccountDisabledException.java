package za.co.entelect.services.security.exceptions;

import org.springframework.security.core.AuthenticationException;

public class AccountDisabledException extends AuthenticationException {

    public AccountDisabledException() {
        super("Your account has been disabled by an administrator.");
    }
}
