package com.adeptik.algorithm.runtime;

/**
 * Контекст запуска алгоритма для решения определенной задачи
 */
@SuppressWarnings("unused")
public interface Context {

    /**
     * Объект для манипулирования входными данными задачи
     *
     * @return Объект {@link InputManager}
     */
    InputManager input();

    /**
     * Объект для манипулирования решением задачи
     *
     * @return Объект {@link SolutionManager}
     */
    SolutionManager solution();
}
