package lt.viko.eif.imazolevska.rehearsalbooking.exception;

/**
 * Thrown when an action conflicts with the current state of the system,
 * for example when a room name is already in use.
 */
public class ConflictException extends RuntimeException{

    /**
     * Creates the exception with a message for the user.
     *
     * @param message the error message
     */
    public ConflictException(String message){
        super(message);
    }
}
