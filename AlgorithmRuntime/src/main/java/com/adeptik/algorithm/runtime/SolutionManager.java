package com.adeptik.algorithm.runtime;

import com.adeptik.algorithm.runtime.exceptions.RetryException;
import com.adeptik.algorithm.runtime.utils.OutputStreamHandler;

import java.io.IOException;

/**
 * Класс для манипулирования решением задачи
 */
@SuppressWarnings("unused")
public interface SolutionManager {

    /**
     * Сохранение решения задачи
     *
     * @param solutionStatus      Статус сохраняемого решения
     * @param outputStreamHandler Объект для осуществления сохранения решения задачи
     * @throws IOException    Сетевая ошибка
     * @throws RetryException Ошибка, исправление которой возможно, если повторно вызвать данный метод позднее
     */
    void post(SolutionStatus solutionStatus, OutputStreamHandler outputStreamHandler)
            throws IOException, RetryException;
}
