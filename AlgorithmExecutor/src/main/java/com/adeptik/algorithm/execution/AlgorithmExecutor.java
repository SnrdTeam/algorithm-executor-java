package com.adeptik.algorithm.execution;

import com.adeptik.BuildConfig;
import com.adeptik.algorithm.execution.contracts.*;
import com.adeptik.algorithm.execution.exceptions.InvalidEntryPointException;
import com.adeptik.algorithm.runtime.Context;
import com.adeptik.extensions.PrintStreamExtensions;
import com.google.common.base.Charsets;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.io.Files;
import com.google.gson.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Objects;

@SuppressWarnings("Guava")
public class AlgorithmExecutor {

    private static final int JAVA_MIN_VERSION = 6;
    private static final FluentIterable<String> SUPPORTED_RUNTIMES;

    static {

        ArrayList<String> runtimes = new ArrayList<>();
        for (int jdkVersion = BuildConfig.JDK_VERSION; jdkVersion >= JAVA_MIN_VERSION; jdkVersion--) {
            runtimes.add("jre" + jdkVersion + "-jar");
        }
        SUPPORTED_RUNTIMES = FluentIterable.from(runtimes);
    }

    public static void main(String[] args) {

        if (args.length == 1 && Objects.equals(args[0], "--help")) {

            printHelp();
        } else if (args.length == 1 && Objects.equals(args[0], "--about")) {

            about();
        } else if (args.length == 2 && Objects.equals(args[0], "--check")) {

            check(new File(args[1]));
        } else if (args.length == 2 && Objects.equals(args[0], "--start")) {

            try {

                start(new File(args[1]));
            } catch (Exception e) {

                System.out.print("Error occurred during execution: ");
                PrintStreamExtensions.print(System.out, e);
                System.out.println();
                System.exit(21);
            }
        } else {

            System.out.println("Invalid arguments");
            printHelp();
            System.exit(11);
        }
    }

    private static void check(File algorithmDir) {

        AlgorithmCheckResult result = new AlgorithmCheckResult();
        try {
            prepareAlgorithm(algorithmDir);
            result.validAlgorithm = true;
        } catch (Exception e) {
            result.validAlgorithm = false;
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        gson.toJson(result, System.out);
    }

    private static void printHelp() {
        System.out.println();
        System.out.println("Usage: --about");
        System.out.println("Usage: --check <algorithm folder>");
        System.out.println("Usage: --start <execution settings file>");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --help    prints this help");
        System.out.println("  --about   prints information about this executor in json");
        System.out.println("  --check   checks algorithm execution possibility in <algorithm folder>");
        System.out.println("  --start   starts algorithm execution with settings stored in <execution settings file>");
    }

    private static void about() {

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        gson.toJson(
                new AboutExecutor(
                        "jre-jar executor",
                        BuildConfig.VERSION,
                        SUPPORTED_RUNTIMES.toList()),
                System.out);
    }

    private static void start(File startSettingsFile)
            throws Exception {

        if (startSettingsFile == null)
            throw new NullPointerException("startSettingsFile cannot be null");
        if (!startSettingsFile.exists())
            throw new FileNotFoundException("startSettingsFile not found");

        ExecutionSettings executionSettings;
        try (FileReader reader = new FileReader(startSettingsFile)) {

            Gson gson = new GsonBuilder()
                    .registerTypeAdapter(File.class, new FileTypeDeserializer())
                    .registerTypeAdapter(SolutionStoreSettings.class, new SolutionStoreSettingsTypeDeserializer())
                    .create();
            executionSettings = gson.fromJson(reader, ExecutionSettings.class);
        }
        execute(executionSettings);
    }

    @SuppressWarnings({"Convert2Lambda", "Guava"})
    private static void execute(ExecutionSettings executionSettings)
            throws Exception {

        if (executionSettings == null)
            throw new NullPointerException("executionSettings cannot be null");

        File problemDir = executionSettings.problemDir;
        if (problemDir == null)
            throw new NullPointerException("problemDir cannot be null");
        if (!problemDir.exists() || !problemDir.isDirectory())
            throw new IllegalArgumentException("Problem folder is invalid");

        prepareAlgorithm(executionSettings.algorithmDir)
                .invoke(null, new ExecutionContext(executionSettings));
    }

    private static Method prepareAlgorithm(File algorithmDir)
            throws Exception {

        if (algorithmDir == null)
            throw new NullPointerException("algorithmDir cannot be null");
        if (!algorithmDir.exists() || !algorithmDir.isDirectory())
            throw new IllegalArgumentException("Algorithm folder is invalid");

        File runtimeDir = null;
        for (String runtime : SUPPORTED_RUNTIMES) {
            runtimeDir = new File(algorithmDir, "runtimes/" + runtime + "/");
            if (runtimeDir.exists() && runtimeDir.isDirectory())
                break;
            runtimeDir = null;
        }
        if (runtimeDir == null)
            throw new IOException("There is no any supported runtime");

        File libsDir = new File(runtimeDir, "libs");
        if (!libsDir.exists() || !libsDir.isDirectory())
            throw new IOException("libs directory is invalid");

        File[] jars = libsDir.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jars == null || jars.length == 0)
            throw new Exception("there is no any jar file");

        File entryPointFile = new File(runtimeDir, "entrypoint");
        EntryPoint entryPoint = new EntryPoint(entryPointFile, Charsets.UTF_8);

        ClassLoader classLoader = URLClassLoader.newInstance(FluentIterable
                .from(jars)
                .transform(file -> {
                    try {
                        assert file != null;
                        return file.toURI().toURL();
                    } catch (MalformedURLException e) {
                        throw new RuntimeException(e);
                    }
                })
                .toArray(URL.class));
        Class<?> entryPointClass = classLoader.loadClass(entryPoint.ClassName);

        try {
            Method entryPointMethod = entryPointClass.getDeclaredMethod(entryPoint.MethodName, Context.class);
            if (!Modifier.isStatic(entryPointMethod.getModifiers()))
                throw new IllegalAccessException("entrypoint method is not static");
            return entryPointMethod;
        } catch (Exception e) {
            throw new InvalidEntryPointException("Entry Point method is invalid", e);
        }
    }

    private static class EntryPoint {

        final String ClassName;

        final String MethodName;

        private EntryPoint(File entryPointFile, Charset charset) throws IOException {

            if (!entryPointFile.exists() || !entryPointFile.isFile())
                throw new IOException("entrypoint file not found");

            String methodFullName = Iterables.getFirst(
                    Files.readLines(entryPointFile, charset), null);
            assert methodFullName != null;
            int lastDotIndex = methodFullName.lastIndexOf('.');
            if (lastDotIndex < 0)
                throw new IllegalStateException("Entry point is invalid");
            ClassName = methodFullName.substring(0, lastDotIndex);
            MethodName = methodFullName.substring(lastDotIndex + 1);
            if (ClassName.isEmpty() || MethodName.isEmpty())
                throw new IllegalStateException("Entry point is invalid");
        }
    }

    private static class FileTypeDeserializer implements JsonDeserializer<File> {

        @Override
        public File deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            String filePath = context.deserialize(json, String.class);
            return new File(filePath);
        }
    }

    private static class SolutionStoreSettingsTypeDeserializer implements JsonDeserializer<SolutionStoreSettings> {

        @Override
        public SolutionStoreSettings deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {

            JsonElement typeProperty = json.getAsJsonObject().get("_type");
            if (typeProperty == null)
                throw new JsonParseException("required property \"_type\" not found");
            String type = typeProperty.getAsString();
            switch (type) {

                case FileSolutionStore.TYPE:
                    return context.deserialize(json, FileSolutionStore.class);
                case HttpServiceSolutionStore.TYPE:
                    return context.deserialize(json, HttpServiceSolutionStore.class);
                default:
                    throw new JsonParseException(new UnsupportedOperationException("Unknown type " + type));
            }
        }
    }
}
