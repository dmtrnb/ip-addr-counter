package org.example.ip4v_counter.bitset_counter.runner;

import org.example.ip4v_counter.bitset_counter.container.BitSetContainer;
import org.example.ip4v_counter.bitset_counter.container.DependencyContainer;
import org.example.ip4v_counter.bitset_counter.converter.Ip4vAddrConverter;
import org.example.ip4v_counter.bitset_counter.validator.Validator;

import java.util.concurrent.CountDownLatch;
import java.util.logging.Logger;

import static org.example.ip4v_counter.bitset_counter.MultiThreadBitSetIp4vAddrCounter.THREADS;

public class ArrayStringIp4vAddrRunner implements Runnable {

    private static final String INVALID_IP_ADDR_MESSAGE = "Invalid ip address '%s' on %d line\n";

    private final int from;
    private final int to;
    private final String[] strings;
    private final BitSetContainer bitSetContainer;
    private final DependencyContainer dependencyContainer;

    private Logger logger;
    private Validator<String> validator;
    private Ip4vAddrConverter<String, Integer> converter;

    public ArrayStringIp4vAddrRunner(String[] strings, int from, int to, BitSetContainer bitSetContainer,
                                     DependencyContainer dependencyContainer, CountDownLatch countDownLatch
    ) {
        this.from = from;
        this.to = to;
        this.strings = strings;
        this.bitSetContainer = bitSetContainer;
        this.countDownLatch = countDownLatch;
        this.dependencyContainer = dependencyContainer;
    }

    private final CountDownLatch countDownLatch;

    @Override
    public void run() {
        setDependencies();

        for (int i = from; i < to; i++) {
            String s = strings[i];
            if (!validator.validate(s)) {
                logger.warning(INVALID_IP_ADDR_MESSAGE.formatted(s, 0));
                return;
            }

            int num = converter.convert(s);

            bitSetContainer.set(num);
        }

        countDownLatch.countDown();
    }

    private void setDependencies() {
        int threadId = (int) (Thread.currentThread().getId() % THREADS);
        this.logger = dependencyContainer.getLogger(threadId);
        this.validator = dependencyContainer.getValidator(threadId);
        this.converter = dependencyContainer.getConverter(threadId);
    }
}
