package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import java.time.LocalDateTime;

/**
 * Error data that the API returns when something goes wrong.
 * All errors have the same format (NFR-5).
 */
public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;

    /**
     * Creates the error response.
     *
     * @param timestamp when the error happened
     * @param status    the HTTP status code, for example 404
     * @param error     the name of the status, for example "Not Found"
     * @param message   the explanation for the user
     * @param path      the URL of the request
     */
    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }
}
