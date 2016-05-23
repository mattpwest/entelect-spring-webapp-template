package za.co.entelect.web.exceptions;

import lombok.Getter;

public class EntityNotFoundException extends RuntimeException {

    @Getter
    private String redirectUri = "/";

    public EntityNotFoundException() {
        super("Entity not found.");
    }

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String entityType, Integer id) {
        super(String.format("%s with ID %d not found.", entityType, id));
    }

    public EntityNotFoundException(String entityType, Integer id, String redirectUri) {
        this(entityType, id);

        this.redirectUri = redirectUri;
    }
}
