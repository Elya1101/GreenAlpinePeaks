package com.example.greenalpinepeaks.exception;

public class ServiceExecutionException extends RuntimeException {

    public ServiceExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}