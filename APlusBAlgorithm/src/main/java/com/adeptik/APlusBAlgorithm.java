package com.adeptik;

import com.adeptik.algorithm.runtime.Context;
import com.adeptik.algorithm.runtime.SolutionStatus;
import com.adeptik.algorithm.runtime.exceptions.RetryException;
import com.adeptik.algorithm.runtime.utils.OutputStreamHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Scanner;

/**
 * Класс, содержащий точку входа Алгоритма "a + b"
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class APlusBAlgorithm {

    private static final long RETRY_DELAY_MILLIS = 1000;

    /**
     * Точка входа Алгоритма "a + b"
     *
     * @param context Контекст запуска алгоритма для решения определенной задачи
     */
    public static void run(Context context) {

        System.out.println("Running algorithm \"a + b\"");

        try {

            try (InputStream inputStream = context.input().openInput("input.txt")) {
                try (Scanner inputScanner = new Scanner(inputStream)) {
                    final double result = inputScanner.nextDouble() + inputScanner.nextDouble();
                    System.out.println("a + b = " + result);
                    while (true) {
                        try {
                            System.out.println("Sending request to the solution store");
                            context.solution().post(SolutionStatus.Final, new OutputStreamHandler() {

                                @Override
                                public void handle(OutputStream outputStream) throws IOException {

                                    OutputStreamWriter writer = new OutputStreamWriter(outputStream);
                                    writer.write(Double.toString(result));
                                    writer.flush();
                                }
                            });
                            break;
                        } catch (RetryException e) {
                            System.out.println("Sending request failed. Will retry after " + RETRY_DELAY_MILLIS + " ms");
                            Thread.sleep(RETRY_DELAY_MILLIS);
                        }
                    }
                    System.out.println("Problem solved.");
                }
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
