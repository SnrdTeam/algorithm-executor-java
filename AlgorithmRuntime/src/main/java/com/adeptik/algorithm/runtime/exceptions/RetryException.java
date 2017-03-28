package com.adeptik.algorithm.runtime.exceptions;

/**
 * Ошибка выполнения операции, исправление которой возможно, если повторить операцию позднее
 */
@SuppressWarnings("unused")
public class RetryException extends Exception {

    /**
     * Создание экземпляра класса {@link RetryException}
     */
    public RetryException() {
    }

    /**
     * Создание экземпляра класса {@link RetryException}
     *
     * @param message Сообщение об ошибке
     */
    public RetryException(String message) {
        super(message);
    }

    /**
     * Создание экземпляра класса {@link RetryException}
     *
     * @param message Сообщение об ошибке
     * @param cause   Исключение, являющееся причиной данной ошибки
     */
    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создание экземпляра класса {@link RetryException}
     *
     * @param cause Исключение, являющееся причиной данной ошибки
     */
    public RetryException(Throwable cause) {
        super(cause);
    }
}
