package com.adeptik.algorithm.runtime.exceptions;

/**
 * Ошибка выполнения операции, исправление которой возможно, если повторить операцию позднее
 */
public class RetryException extends Exception {

    public RetryException() {
    }

    public RetryException(String message) {
        super(message);
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryException(Throwable cause) {
        super(cause);
    }
}
