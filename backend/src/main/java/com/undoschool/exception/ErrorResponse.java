package com.undoschool.exception;

import java.time.OffsetDateTime;
import java.util.List;

public class ErrorResponse {
    private int statusCode;
    private String message;
    private List<String> errors;
    private OffsetDateTime timestamp;

    public ErrorResponse(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.timestamp = OffsetDateTime.now();
    }

    public ErrorResponse(int statusCode, String message, List<String> errors) {
        this.statusCode = statusCode;
        this.message = message;
        this.errors = errors;
        this.timestamp = OffsetDateTime.now();
    }

    public int getStatusCode() { return statusCode; }
    public String getMessage() { return message; }
    public List<String> getErrors() { return errors; }
    public OffsetDateTime getTimestamp() { return timestamp; }
}
