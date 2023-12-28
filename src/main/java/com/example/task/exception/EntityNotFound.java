package com.example.task.exception;

public class EntityNotFound extends DbException {

    public EntityNotFound() {
    }

    public EntityNotFound(String message) {
        super(message);
    }

    public EntityNotFound(String message, Throwable cause) {
        super(message, cause);
    }
}
