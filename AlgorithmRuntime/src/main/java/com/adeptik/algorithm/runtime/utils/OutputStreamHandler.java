package com.adeptik.algorithm.runtime.utils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Класс для сохранения данных в поток
 */
public interface OutputStreamHandler {

    /**
     * Обработка сохранения данных в поток
     *
     * @param outputStream Поток, в который сохраняются данные
     * @throws IOException Ошибка записи в поток
     */
    void handle(OutputStream outputStream) throws IOException;
}
