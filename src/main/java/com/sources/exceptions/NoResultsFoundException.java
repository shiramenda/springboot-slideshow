package com.sources.exceptions;

public class NoResultsFoundException extends RuntimeException {
    public NoResultsFoundException(String message) {
        super(message);
    }
}
