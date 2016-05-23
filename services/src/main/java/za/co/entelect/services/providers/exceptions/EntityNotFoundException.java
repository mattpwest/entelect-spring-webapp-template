package za.co.entelect.services.providers.exceptions;

public class EntityNotFoundException extends Exception {

    public EntityNotFoundException() {
        super();
    }

    public EntityNotFoundException(final String message) {
        super(message);
    }

    public EntityNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public EntityNotFoundException(Object entityType, int id) {
        super(String.format("%s with ID %d not found.", entityType.getClass().getSimpleName(), id));
    }
}
