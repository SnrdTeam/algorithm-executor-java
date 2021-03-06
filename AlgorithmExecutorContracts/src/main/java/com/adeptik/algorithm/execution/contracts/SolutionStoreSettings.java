package com.adeptik.algorithm.execution.contracts;

/**
 * Параметры хранилища решений задачи
 */
@SuppressWarnings("unused")
public abstract class SolutionStoreSettings {

    private final String _type;

    /**
     * Создание экземпляра класса {@link SolutionStoreSettings}
     *
     * @param type Имя типа хранилища решений задачи
     */
    SolutionStoreSettings(String type) {

        _type = type;
    }

    /**
     * Возвращает тип хранилища решений задачи
     *
     * @return строка, представляющая собой тип хранилища решений задачи
     */
    public final String getType() {

        return _type;
    }
}
