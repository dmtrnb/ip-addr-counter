package org.example.ip4v_counter.bitset_counter;

import org.example.ip4v_counter.bitset_counter.container.ConcurrentBitSetContainer;
import org.example.ip4v_counter.bitset_counter.runner.ArrayStringIp4vAddrRunner;
import org.example.ip4v_counter.bitset_counter.container.BitSetContainer;
import org.example.ip4v_counter.bitset_counter.container.DependencyContainer;
import org.example.ip4v_counter.counter.Ip4vAddrCounter;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Logger;

public class MultiThreadBitSetIp4vAddrCounter implements Ip4vAddrCounter {

    public static final int THREADS = 8;
    private static final int SIZE_ARRAY = 16384;
    private static final long NBITS = 1L << 32;
    private static final String FILE_WAS_NOT_FOUND_MESSAGE = "File '%s' was not found";

    private final Logger logger;
    private final DependencyContainer dependencyContainer;
    private final BitSetContainer bitSetContainer;
    private final ExecutorService executorService;

    public MultiThreadBitSetIp4vAddrCounter(Logger logger, ExecutorService executorService) {
        this.logger = logger;
        this.executorService = executorService;
        this.bitSetContainer = new ConcurrentBitSetContainer(NBITS);
        this.dependencyContainer = new DependencyContainer(THREADS);
    }

    public long count(String filename) {
        bitSetContainer.clear();

        try (LineNumberReader reader = new LineNumberReader(new FileReader(filename))) {
            String s;
            int count = 0;
            String[] array = new String[SIZE_ARRAY];

            while ((s = reader.readLine()) != null) {
                array[count] = s;
                count++;
                if (count == SIZE_ARRAY) {
                    count = 0;
                    work(array, SIZE_ARRAY, THREADS);
                }
            }

            work(Arrays.copyOf(array, count), count, 1);

        } catch (FileNotFoundException e) {
            logger.severe(FILE_WAS_NOT_FOUND_MESSAGE.formatted(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        return bitSetContainer.cardinality();
    }

    private void work(String[] strings, int size, int threads) {
        int from = 0;
        int chunk = size / threads;
        int to = chunk;
        CountDownLatch countDownLatch = new CountDownLatch(threads);

        for (int i = 0; i < threads; i++) {
            executorService.execute(new ArrayStringIp4vAddrRunner(
                    strings, from, to, bitSetContainer, dependencyContainer, countDownLatch
            ));
            from = to;
            to += chunk;
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
