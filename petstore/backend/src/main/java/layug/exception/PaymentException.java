package layug.exception;

/**
 * Exception thrown when a payment operation fails.
 * Maps to HTTP 402 Payment Required or 400/500 depending on context.
 */
public class PaymentException extends RuntimeException {

    private String errorCode;
    private String paymentMethod;

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public PaymentException(String message, String errorCode, String paymentMethod) {
        super(message);
        this.errorCode = errorCode;
        this.paymentMethod = paymentMethod;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }
}
