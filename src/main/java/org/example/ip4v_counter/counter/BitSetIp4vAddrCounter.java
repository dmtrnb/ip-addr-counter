package org.example.ip4v_counter.counter;

import org.example.ip4v_counter.converter.Ip4vAddrConverter;
import org.example.ip4v_counter.validator.Validator;

import java.io.*;
import java.util.BitSet;
import java.util.logging.Logger;

public class BitSetIp4vAddrCounter implements Ip4vAddrCounter {

    private final BitSet positive;
    private final BitSet negative;
    private final Logger logger;
    private final Validator<String> ip4vAddrValidator;
    private final Ip4vAddrConverter<String, Integer> ip4vAddrConverter;

    private static final int COUNT_OF_BITS = 2 << (32 - 6 - 1);
    private static final String FILE_WAS_NOT_FOUND_MESSAGE = "File '%s' was not found";
    private static final String INVALID_IP_ADDR_MESSAGE = "Invalid ip address '%s' on %d line\n";

    public BitSetIp4vAddrCounter(
            Validator<String> ip4vAddrValidator, Ip4vAddrConverter<String, Integer> ip4vAddrConverter, Logger logger
    ) {
        this.positive = new BitSet(COUNT_OF_BITS);
        this.negative = new BitSet(COUNT_OF_BITS);
        this.logger = logger;
        this.ip4vAddrValidator = ip4vAddrValidator;
        this.ip4vAddrConverter = ip4vAddrConverter;
    }

    public long count(String filename) {
        positive.clear();
        negative.clear();

        try (LineNumberReader reader = new LineNumberReader(new FileReader(filename))) {
            String s;
            while ((s = reader.readLine()) != null) {
                if (!ip4vAddrValidator.validate(s)) {
                    logger.warning(INVALID_IP_ADDR_MESSAGE.formatted(s, reader.getLineNumber()));
                    continue;
                }

                int num = ip4vAddrConverter.convert(s);

                if (num < 0) {
                    negative.set(~num);
                } else {
                    positive.set(num);
                }
            }
        } catch (FileNotFoundException e) {
            logger.severe(FILE_WAS_NOT_FOUND_MESSAGE.formatted(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return positive.cardinality() + negative.cardinality();
    }
}
