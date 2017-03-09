package com.adeptik.algorithm.execution.contracts;

import java.io.File;

/**
 * Хранилище решений задачи в папке
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public final class FileSolutionStore extends SolutionStoreSettings {

    public static final String TYPE = "FileSolutionStore";

    public FileSolutionStore() {
        super(TYPE);
    }

    /**
     * Папка, в которую необходимо сохранять решения задачи
     */
    public File solutionsDir;
}
