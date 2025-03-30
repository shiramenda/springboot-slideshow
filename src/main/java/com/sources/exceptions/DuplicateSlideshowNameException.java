package com.sources.exceptions;

public class DuplicateSlideshowNameException extends RuntimeException {
    public DuplicateSlideshowNameException(String message) {
        super(message);
    }
}
