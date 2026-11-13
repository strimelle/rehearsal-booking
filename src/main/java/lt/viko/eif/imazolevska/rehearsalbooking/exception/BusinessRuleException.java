package lt.viko.eif.imazolevska.rehearsalbooking.exception;


/**
 * Thrown when the given data breaks a business rule,
 * for example when a booking is too long or is in the past.
 */
public class BusinessRuleException extends RuntimeException{

    /**
     * Creates the exception with a message for the user.
     *
     * @param message the error message
     */
    public BusinessRuleException(String message){
        super(message);
    }
}
