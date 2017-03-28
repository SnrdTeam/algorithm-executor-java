package com.adeptik.algorithm.execution.contracts;

import java.io.File;

/**
 * Параметры запуска исполнения алгоритма для решения конкретной задачи
 */
@SuppressWarnings({"unused", "CanBeFinal"})
public class ExecutionSettings {

    /**
     * Папка с распакованным определением алгоритма
     */
    public File algorithmDir;

    /**
     * Папка с распакованным определением задачи
     */
    public File problemDir;

    /**
     * Параметра хранилища решений задачи
     */
    public SolutionStoreSettings solutionStore;
}
