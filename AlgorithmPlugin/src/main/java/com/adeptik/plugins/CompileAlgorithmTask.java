package com.adeptik.plugins;

import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.file.CopySpec;
import org.gradle.api.internal.file.collections.FileTreeAdapter;
import org.gradle.api.internal.file.collections.MapFileTree;
import org.gradle.api.internal.file.copy.CopySpecInternal;
import org.gradle.api.tasks.Copy;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.Internal;

import java.io.*;
import java.util.concurrent.Callable;

/**
 * Задача Gradle для сборки Определения алгоритма
 */
@SuppressWarnings({"WeakerAccess", "unused", "Convert2Lambda"})
public class CompileAlgorithmTask extends Copy {

    public static final String DESTINATION_DIR_NAME = "algorithm";

    private final CopySpecInternal _runtime;
    private final CopySpecInternal _libs;

    private String _runtimeName;
    private String _entryPoint;

    /**
     * Создание экземпляра класса {@link CompileAlgorithmTask}
     */
    public CompileAlgorithmTask() {

        this.setDestinationDir(getProject().file(new File(getProject().getBuildDir(), DESTINATION_DIR_NAME)));

        CopySpecInternal runtimes = (CopySpecInternal) getRootSpec().addFirst().into("runtimes");
        runtimes.addChild().from(new Callable<FileTreeAdapter>() {

            public FileTreeAdapter call() throws Exception {
                MapFileTree runtimesSource = new MapFileTree(getTemporaryDirFactory(), getFileSystem());

                if (_runtimeName == null)
                    throw new InvalidUserDataException("runtimeName cannot be null");
                if (_runtimeName.isEmpty())
                    throw new InvalidUserDataException("runtimeName cannot be empty");

                runtimesSource.add(_runtimeName + "/entrypoint", new Action<OutputStream>() {

                    public void execute(OutputStream outputStream) {

                        if (_entryPoint == null)
                            throw new InvalidUserDataException("entryPoint cannot be null");
                        if (_entryPoint.isEmpty())
                            throw new InvalidUserDataException("entryPoint cannot be empty");

                        Writer writer = new OutputStreamWriter(outputStream);
                        try {
                            writer.write(_entryPoint);
                            writer.flush();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                return new FileTreeAdapter(runtimesSource);
            }
        });

        _runtime = ((CopySpecInternal) runtimes.addChild().into(_runtimeName));
        _libs = ((CopySpecInternal) _runtime.addChild().into("libs"));
    }

    /**
     * Возвоащает имя Среды исполнения
     *
     * @return Имя Среды исполнения
     */
    @Input
    public String getRuntimeName() {
        return _runtimeName;
    }

    /**
     * Устанавливает имя Среды исполнения
     *
     * @param runtimeName Имя Среды исполнения
     */
    void setRuntimeName(String runtimeName) {
        this._runtimeName = runtimeName;
        _runtime.into(runtimeName);
    }

    /**
     * Возврщает точку входа в Алгоритм
     *
     * @return Точка входа в Алгоритм - полное имя метода
     */
    @Input
    public String getEntryPoint() {
        return _entryPoint;
    }

    /**
     * Устанавливает точку входа в Алгоритм
     *
     * @param entryPoint Точка входа в Алгоритм - полное имя метода
     */
    public void setEntryPoint(String entryPoint) {
        this._entryPoint = entryPoint;
    }

    /**
     * Возвращает содержимое папки Среды исполнения в Определении алгоритма
     *
     * @return Содержимое папки Среды исполнения в Определении алгоритма
     */
    @Internal
    public CopySpec getRuntime() {
        return _runtime.addChild();
    }

    /**
     * Возвращает содержимое папки libs Среды исполнения в Определении алгоритма
     *
     * @return Содержимое папки libs Среды исполнения в Определении алгоритма
     */
    @Internal
    public CopySpec getLibs() {
        return _libs.addChild();
    }
}
