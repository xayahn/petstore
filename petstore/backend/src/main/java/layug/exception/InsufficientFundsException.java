package layug.exception;

/**
 * Exception thrown when an account has insufficient funds for an operation.
 * Maps to HTTP 402 Payment Required status code.
 */
public class InsufficientFundsException extends RuntimeException {

    private java.math.BigDecimal required;
    private java.math.BigDecimal available;

    public InsufficientFundsException(String message) {
        super(message);
    }

    public InsufficientFundsException(java.math.BigDecimal required, java.math.BigDecimal available) {
        super(String.format("Insufficient funds. Required: %s, Available: %s", required, available));
        this.required = required;
        this.available = available;
    }

    public java.math.BigDecimal getRequired() {
        return required;
    }

    public java.math.BigDecimal getAvailable() {
        return available;
    }
}
