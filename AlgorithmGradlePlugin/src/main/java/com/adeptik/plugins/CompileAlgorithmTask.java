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

public class CompileAlgorithmTask extends Copy {

    public static final String DESTINATION_DIR_NAME = "algorithm";

    private final CopySpecInternal _runtime;
    private final CopySpecInternal _libs;

    private String _runtimeName;
    private String _entryPoint;

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

    @Input
    public String getRuntimeName() {
        return _runtimeName;
    }

    void setRuntimeName(String runtimeName) {
        this._runtimeName = runtimeName;
        _runtime.into(runtimeName);
    }

    @Input
    public String getEntryPoint() {
        return _entryPoint;
    }

    public void setEntryPoint(String entryPoint) {
        this._entryPoint = entryPoint;
    }

    @Internal
    public CopySpec getRuntime() {
        return _runtime.addChild();
    }

    @Internal
    public CopySpec getLibs() {
        return _libs.addChild();
    }
}
