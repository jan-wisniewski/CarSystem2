package com.app.persistence.model.exception;

public class JsonConversionException extends RuntimeException {
    public JsonConversionException(String message) {
        super(message);
    }
}
