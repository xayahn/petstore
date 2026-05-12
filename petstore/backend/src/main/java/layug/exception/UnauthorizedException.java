package layug.exception;

/**
 * Exception thrown when an unauthorized action is attempted.
 * Maps to HTTP 401 Unauthorized status code.
 */
public class UnauthorizedException extends RuntimeException {

    public UnauthorizedException(String message) {
        super(message);
    }

    public UnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
