package com.adeptik;

import com.adeptik.algorithm.runtime.Context;
import com.adeptik.algorithm.runtime.exceptions.RetryException;

import java.io.IOException;

/**
 * Класс, содержащий точку входа алгоритма "Бесконечный цикл"
 */
public class InfiniteLoopAlgorithm {

    /**
     * Точка входа Алгоритма "Бесконечный цикл"
     *
     * @param context Контекст запуска алгоритма для решения определенной задачи
     */
    public static void run(Context context) throws IOException, RetryException {

        int counter = 0;

        //noinspection InfiniteLoopStatement
        while (true) {

            // Делаем какие-то вычисления

            boolean odd = false;
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                odd |= i % 2 != 0;
            }

            System.out.println(++counter + " cycle completed");

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
