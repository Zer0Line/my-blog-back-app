package com.training.blog.exception;

public class MissingAttachedFileException extends RuntimeException {

    public MissingAttachedFileException() {
        super("No file was attached");
    }
}
