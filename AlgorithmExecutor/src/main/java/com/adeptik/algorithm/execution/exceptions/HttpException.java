package com.adeptik.algorithm.execution.exceptions;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Ошибка при выполнениии http-запроса
 */
@SuppressWarnings("unused")
public class HttpException extends IOException {

    private final int _code;
    private final String _reason;
    private final Map<String, List<String>> _headers;
    private final String _body;

    /**
     * Создание экземпляра класса {@link HttpException}
     *
     * @param code    Код ответа
     * @param reason  reason phrase
     * @param headers Заголовки ответа
     * @param body    Тело ответа
     */
    public HttpException(int code, String reason, Map<String, List<String>> headers, String body) {

        _code = code;
        _reason = reason;
        _headers = headers;
        _body = body;
    }

    /**
     * Код ответа
     *
     * @return код ответа
     */
    public int responseCode() {
        return _code;
    }

    /**
     * reason phrase
     *
     * @return reason phrase
     */
    public String reason() {
        return _reason;
    }

    /**
     * Заголовки ответа
     *
     * @return заголовки ответа
     */
    public Map<String, List<String>> headers() {
        return _headers;
    }

    /**
     * Тело ответа
     *
     * @return тело ответа
     */
    public String body() {
        return _body;
    }
}
