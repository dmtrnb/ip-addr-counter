package org.example.ip4v_counter;

import org.example.ip4v_counter.converter.Ip4vAddrConverter;
import org.example.ip4v_counter.converter.Shorts2IntegerIp4VAddrConverter;
import org.example.ip4v_counter.converter.String2IntegerIp4VAddrConverter;
import org.example.ip4v_counter.converter.String2ShotrsIp4VAddrConverter;
import org.example.ip4v_counter.counter.BitSetIp4vAddrCounter;
import org.example.ip4v_counter.counter.Ip4vAddrCounter;
import org.example.ip4v_counter.counter.MultiThreadBitSetIp4vAddrCounter;
import org.example.ip4v_counter.validator.Ip4vAddrValidator;
import org.example.ip4v_counter.validator.Validator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import static org.example.ip4v_counter.counter.MultiThreadBitSetIp4vAddrCounter.THREADS;

public class Main {

    public static void main(String[] args) {
        Map<String, Ip4vAddrCounter> counters = new HashMap<>();
        counters.put("BitSetCounter", getBitSetCounter());
        counters.put("MultiThreadBitSetCounter", getMultiThreadBitSetCounter());

        counters.forEach((name, counter) -> {
            for (String filename: args) {
                long t0 = System.currentTimeMillis();
                long count = counter.count(filename);
                long t1 = System.currentTimeMillis();

                System.out.printf(
                        "%s:\nThere are %d unique ip addresses in the '%s' file. The calculation took about %.2f ms.\n",
                        name, count, filename, (t1 - t0) / 1000.
                );
            }
        });
    }

    private static Ip4vAddrCounter getBitSetCounter() {
        Logger logger = Logger.getGlobal();
        Validator<String> validator = new Ip4vAddrValidator();
        Ip4vAddrConverter<String, short[]> string2ShortsIp4vAddrConverter = new String2ShotrsIp4VAddrConverter();
        Ip4vAddrConverter<short[], Integer> shorts2IntegerIp4vAddrConverter = new Shorts2IntegerIp4VAddrConverter();
        Ip4vAddrConverter<String, Integer> string2IntegerIp4vAddrConverter = new String2IntegerIp4VAddrConverter(
                string2ShortsIp4vAddrConverter, shorts2IntegerIp4vAddrConverter
        );

        return new BitSetIp4vAddrCounter(validator, string2IntegerIp4vAddrConverter, logger);
    }

    private static Ip4vAddrCounter getMultiThreadBitSetCounter() {
        Logger logger = Logger.getGlobal();
        ExecutorService executorService = Executors.newFixedThreadPool(THREADS);
        return new MultiThreadBitSetIp4vAddrCounter(logger, executorService);
    }
}
