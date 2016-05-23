package za.co.entelect.services.providers.exceptions;

import org.springframework.web.client.RestClientException;

public class RecaptchaServiceException extends RuntimeException {

    public RecaptchaServiceException(String message, RestClientException exception) {
        super(message, exception);
    }
}
