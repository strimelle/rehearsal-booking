package lt.viko.eif.imazolevska.rehearsalbooking.exception;

/**
 * Thrown when an external service, such as Meteo.lt, cannot be reached
 * or returns an error.
 */
public class ExternalServiceException extends RuntimeException {

    /**
     * Creates the exception with a message for the user.
     *
     * @param message the error message
     */
    public ExternalServiceException(String message) {
        super(message);
    }
}
