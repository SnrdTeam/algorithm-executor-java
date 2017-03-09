package com.adeptik.algorithm.execution.contracts;

import java.net.URL;

/**
 * Хранилище решений задачи в веб-сервисе
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class HttpServiceSolutionStore extends SolutionStoreSettings {

    public static final String TYPE = "HttpServiceSolutionStore";

    public HttpServiceSolutionStore() {
        super(TYPE);
    }

    /**
     * Адрес сервера хранилища решений задачи
     */
    public URL serverUrl;

    /**
     * Заголовок запросов для авторизации на сервере
     */
    public String Authorization;
}
