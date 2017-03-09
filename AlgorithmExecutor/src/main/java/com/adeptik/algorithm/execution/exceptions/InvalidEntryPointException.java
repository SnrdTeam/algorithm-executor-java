package com.adeptik.algorithm.execution.exceptions;

/**
 * Метод, описанный как точка входа в алгоритм, некорректен
 */
public class InvalidEntryPointException extends Exception {

    public InvalidEntryPointException(String message) {
        super(message);
    }

    public InvalidEntryPointException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidEntryPointException(Throwable cause) {
        super(cause);
    }
}
