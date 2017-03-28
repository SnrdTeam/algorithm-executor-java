package com.adeptik.algorithm.execution;

import com.adeptik.algorithm.execution.contracts.ExecutionSettings;
import com.adeptik.algorithm.execution.contracts.FileSolutionStore;
import com.adeptik.algorithm.execution.contracts.HttpServiceSolutionStore;
import com.adeptik.algorithm.execution.exceptions.HttpException;
import com.adeptik.algorithm.runtime.Context;
import com.adeptik.algorithm.runtime.InputManager;
import com.adeptik.algorithm.runtime.SolutionManager;
import com.adeptik.algorithm.runtime.SolutionStatus;
import com.adeptik.algorithm.runtime.exceptions.RetryException;
import com.adeptik.algorithm.runtime.utils.OutputStreamHandler;
import okhttp3.*;
import okio.BufferedSink;

import java.io.*;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Реализация контекста запуска Алгоритма для решения определенной Задачи
 */
class ExecutionContext implements Context {

    private final InputManager _inputManager;
    private final SolutionManager _solutionManager;

    /**
     * Создание экземпляра класса {@link ExecutionContext}
     *
     * @param executionSettings Параметры запуск исполнения алгоритма для решения конкретной задачи
     * @throws FileNotFoundException Файл или папка не найдены
     */
    ExecutionContext(ExecutionSettings executionSettings)
            throws FileNotFoundException {

        _inputManager = new InputManagerImpl(executionSettings.problemDir);

        if (executionSettings.solutionStore == null)
            throw new NullPointerException("Solution Store Server is not specified");
        if (executionSettings.solutionStore instanceof FileSolutionStore) {
            File solutionsDir = ((FileSolutionStore) executionSettings.solutionStore).solutionsDir;
            _solutionManager = new SolutionManagerFileImpl(solutionsDir);
        } else if (executionSettings.solutionStore instanceof HttpServiceSolutionStore) {
            URL serverUrl = ((HttpServiceSolutionStore) executionSettings.solutionStore).serverUrl;
            String authorization = ((HttpServiceSolutionStore) executionSettings.solutionStore).Authorization;
            _solutionManager = new SolutionManagerHttpImpl(serverUrl, authorization);
        } else
            _solutionManager = null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public InputManager input() {
        return _inputManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SolutionManager solution() {
        return _solutionManager;
    }


    /**
     * Реализация класса для манипулирования входными данными задачи
     */
    private static class InputManagerImpl implements InputManager {

        private final File _problemDir;

        InputManagerImpl(File problemDir) {

            if (problemDir == null)
                throw new NullPointerException("problemDir cannot be null");
            if (!problemDir.exists() || !problemDir.isDirectory())
                throw new InvalidParameterException("problemDir folder not found");

            _problemDir = problemDir;
        }

        @Override
        public InputStream openInput(String name) throws IOException {

            if (name == null)
                throw new NullPointerException("name");
            if (name.isEmpty())
                throw new InvalidParameterException("name cannot be an empty string");

            File resourceFile = new File(_problemDir, name);
            if (!resourceFile.exists() || !resourceFile.isFile()) {
                throw new FileNotFoundException("resource not found");
            }
            return new FileInputStream(resourceFile);
        }
    }

    /**
     * Реализация хранения решения задачи в виде HTTP-сервиса
     */
    private static class SolutionManagerHttpImpl implements SolutionManager {

        private final URL _solutionStoreUrl;
        private final String _authorization;

        /**
         * Создание экземпляра класса {@link SolutionManagerHttpImpl}
         *
         * @param solutionStoreUrl Адрес сервиса хранения решения задачи
         * @param authorization    Значение заголовка HTTP-запроса Authorization
         */
        SolutionManagerHttpImpl(URL solutionStoreUrl, String authorization) {

            _solutionStoreUrl = solutionStoreUrl;
            _authorization = authorization;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void post(SolutionStatus solutionStatus, final OutputStreamHandler outputStreamHandler)
                throws IOException, RetryException {

            MultipartBody body = new MultipartBody.Builder()
                    .addFormDataPart("SolutionStatus", translateSolutionStatus(solutionStatus))
                    .addFormDataPart("Solution", "solution", new RequestBody() {

                        @Override
                        public MediaType contentType() {
                            return MediaType.parse("application/octet-stream");
                        }

                        @Override
                        public void writeTo(BufferedSink sink) throws IOException {

                            outputStreamHandler.handle(sink.outputStream());
                        }
                    })
                    .setType(MediaType.parse("multipart/form-data"))
                    .build();

            Request request = new Request.Builder()
                    .url(new URL(_solutionStoreUrl, "/api/problem/solution"))
                    .header("Authorization", _authorization)
                    .put(body)
                    .build();

            OkHttpClient httpClient = new OkHttpClient.Builder()
                    .build();

            try {
                try (Response response = httpClient.newCall(request).execute()) {

                    if (!response.isSuccessful()) {

                        throw new HttpException(
                                response.code(),
                                response.message(),
                                response.headers().toMultimap(),
                                response.body().string());
                    }
                }
            } catch (HttpException e) {

                switch (e.responseCode()) {
                    case 400: // Bad Request
                    case 403: // Forbidden
                    case 410: // Gone
                        throw e;
                    default:
                        throw new RetryException(e);
                }
            } catch (IOException e) {

                throw new RetryException(e);
            }
        }

        private static String translateSolutionStatus(SolutionStatus solutionStatus) {

            switch (solutionStatus) {

                case Final:
                    return "Final";
                case Intermediate:
                    return "Intermediate";
                default:
                    throw new InvalidParameterException("Unknown solution status " + solutionStatus);
            }
        }
    }

    /**
     * Реализация файлового хранения решения задачи
     */
    private static class SolutionManagerFileImpl implements SolutionManager {

        private final File _solutionsDir;

        /**
         * Создание экземпляра класса {@link SolutionManagerFileImpl}
         *
         * @param solutionsDir Папка для сохранения решений задач
         * @throws FileNotFoundException Папка для сохранения решений задач не найдена
         */
        SolutionManagerFileImpl(File solutionsDir)
                throws FileNotFoundException {

            if (!solutionsDir.exists())
                throw new FileNotFoundException("solutionsDir not found");
            _solutionsDir = solutionsDir;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void post(SolutionStatus solutionStatus, OutputStreamHandler outputStreamHandler)
                throws IOException {

            String format = solutionStatus + "_" + new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss")
                    .format(Calendar.getInstance().getTime());
            File solutionFile;
            int number = 0;
            do {
                solutionFile = new File(_solutionsDir, format + (number > 0 ? "." + number : "") + ".solution");
                ++number;
            } while (solutionFile.exists());

            try (FileOutputStream fileOutputStream = new FileOutputStream(solutionFile)) {

                outputStreamHandler.handle(fileOutputStream);
            }
        }
    }
}
