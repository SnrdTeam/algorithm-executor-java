package com.adeptik.algorithm.runtime;

import java.io.IOException;
import java.io.InputStream;

/**
 * Класс для манипулирования входными данными задачи
 */
@SuppressWarnings("unused")
public interface InputManager {

    /**
     * Открывает для чтения входные данные из определения задачи по имени ресурса
     *
     * @param name Имя ресурса входных данных
     * @return Поток для чтения содержимого ресурса входных данных
     * @throws IOException Ошиибка при открытии ресурса с указанным именем
     */
    InputStream openInput(String name)
            throws IOException;
}
