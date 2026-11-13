package lt.viko.eif.imazolevska.rehearsalbooking.exception;


/**
 * Thrown when a requested room, band or booking does not exist.
 */
public class ResourceNotFoundException extends RuntimeException {
    /**
     * Creates the exception with a message for the user.
     *
     * @param message the error message
     */
    public ResourceNotFoundException(String message){
        super(message);
    }
}
