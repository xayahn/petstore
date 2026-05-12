package layug.exception;

/**
 * Exception thrown when input validation fails.
 * Maps to HTTP 400 Bad Request status code.
 */
public class ValidationException extends RuntimeException {

    private String field;
    private String rejectedValue;

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String field, String rejectedValue, String message) {
        super(message);
        this.field = field;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }
}
