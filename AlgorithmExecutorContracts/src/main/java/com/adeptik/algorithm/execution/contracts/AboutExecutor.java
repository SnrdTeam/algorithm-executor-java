package com.adeptik.algorithm.execution.contracts;

import java.util.Collection;

/**
 * Информация об исполнителе
 */
@SuppressWarnings({"WeakerAccess", "unused"})
public class AboutExecutor {

    /**
     * Имя исполнителя
     */
    public final String name;

    /**
     * Версия исполнителя
     */
    public final String version;

    /**
     * Список поддерживаемых сред исполнения.
     * Именно так должны называться папки в подпаке runtimes определения алгоритма
     */
    public final Collection<String> supportedRuntimes;

    public AboutExecutor(String name, String version, Collection<String> supportedRuntimes) {

        if (name == null)
            throw new NullPointerException("name cannot be null");
        if (version == null)
            throw new NullPointerException("version cannot be null");
        if (supportedRuntimes == null)
            throw new NullPointerException("supportedRuntimes cannot be null");

        this.supportedRuntimes = supportedRuntimes;
        this.name = name;
        this.version = version;
    }
}
