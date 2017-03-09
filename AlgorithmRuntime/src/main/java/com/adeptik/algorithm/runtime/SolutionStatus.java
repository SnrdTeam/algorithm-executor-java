package com.adeptik.algorithm.runtime;

/**
 * Статус решения задачи
 */
@SuppressWarnings("unused")
public enum SolutionStatus {

    /**
     * Решения является окончательным. Поиск решения завершен
     */
    Final,

    /**
     * Решение является промежуточным. Поиск решения продолжается, решение может быть обновлено на более лучшее
     */
    Intermediate
}
