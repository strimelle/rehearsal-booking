package lt.viko.eif.imazolevska.rehearsalbooking.exception;

import jakarta.servlet.http.HttpServletRequest;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.ErrorResponse;
import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Turns exceptions into JSON error responses with the right HTTP status code.
 * Works for all controllers.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Creates an error response with the current time and the request URL.
     */
    private ErrorResponse createErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        LocalDateTime now = LocalDateTime.now();
        int statusCode = status.value();
        String error = status.getReasonPhrase();
        String path = request.getRequestURI();

        ErrorResponse errorResponse = new ErrorResponse(now, statusCode, error, message, path);
        return errorResponse;
    }

    /**
     * Handles "not found" errors: HTTP 404.
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException exception,
                                                        HttpServletRequest request) {
        ErrorResponse body = createErrorResponse(HttpStatus.NOT_FOUND, exception.getMessage(), request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Handles broken business rules about the data: HTTP 400.
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRule(BusinessRuleException exception,
                                                            HttpServletRequest request) {
        ErrorResponse body = createErrorResponse(HttpStatus.BAD_REQUEST, exception.getMessage(), request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles conflicts with the current state: HTTP 409.
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException exception,
                                                        HttpServletRequest request) {
        ErrorResponse body = createErrorResponse(HttpStatus.CONFLICT, exception.getMessage(), request);
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Handles validation errors from @Valid: HTTP 400.
     * The message lists every invalid field and the reason.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException exception,
                                                          HttpServletRequest request) {
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        String message = "";
        for (FieldError fieldError : fieldErrors) {
            String fieldMessage = fieldError.getField() + ": " + fieldError.getDefaultMessage();
            if (message.isEmpty()) {
                message = fieldMessage;
            } else {
                message = message + "; " + fieldMessage;
            }
        }

        ErrorResponse body = createErrorResponse(HttpStatus.BAD_REQUEST, message, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Handles a broken JSON body or a wrong date format: HTTP 400.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException exception,
                                                              HttpServletRequest request) {
        String message = "Request body is missing or has a wrong format";
        ErrorResponse body = createErrorResponse(HttpStatus.BAD_REQUEST, message, request);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
