package layug.exception;

import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Standardized error response structure for all API errors.
 * 
 * JSON Example:
 * {
 * "error": "User not found with id: 123",
 * "code": "RESOURCE_NOT_FOUND",
 * "status": 404,
 * "resource": "User",
 * "field": "id",
 * "timestamp": "2026-05-05T10:00:00",
 * "path": "/api/users/123"
 * }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private String error;
    private String code;
    private int status;
    private String resource;
    private String field;
    private String rejectedValue;
    private LocalDateTime timestamp;
    private String path;
    private Map<String, String> validationErrors;

    // Constructors
    public ErrorResponse(String error, String code, int status) {
        this.error = error;
        this.code = code;
        this.status = status;
    }

    public ErrorResponse(String error, String code, int status, String resource, String field) {
        this.error = error;
        this.code = code;
        this.status = status;
        this.resource = resource;
        this.field = field;
    }

    // Getters and Setters
    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getRejectedValue() {
        return rejectedValue;
    }

    public void setRejectedValue(String rejectedValue) {
        this.rejectedValue = rejectedValue;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, String> getValidationErrors() {
        return validationErrors;
    }

    public void setValidationErrors(Map<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }
}
