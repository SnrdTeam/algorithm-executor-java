package com.adeptik.algorithm.execution.exceptions;

/**
 * Метод, описанный как точка входа в алгоритм, некорректен
 */
@SuppressWarnings("unused")
public class InvalidEntryPointException extends Exception {

    /**
     * Создание экземпляра класса {@link InvalidEntryPointException}
     *
     * @param message Сообщение об ошибке
     */
    public InvalidEntryPointException(String message) {
        super(message);
    }

    /**
     * Создание экземпляра класса {@link InvalidEntryPointException}
     *
     * @param message Сообщение об ошибке
     * @param cause   Исключение, являющееся причиной данной ошибки
     */
    public InvalidEntryPointException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Создание экземпляра класса {@link InvalidEntryPointException}
     *
     * @param cause Исключение, являющееся причиной данной ошибки
     */
    public InvalidEntryPointException(Throwable cause) {
        super(cause);
    }
}
