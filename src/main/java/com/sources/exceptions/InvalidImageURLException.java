package com.sources.exceptions;

public class InvalidImageURLException extends RuntimeException {
    public InvalidImageURLException(String message) {
        super(message);
    }
}