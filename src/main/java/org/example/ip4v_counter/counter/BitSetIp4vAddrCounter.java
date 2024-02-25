package org.example.ip4v_counter.counter;

import org.example.ip4v_counter.BitSetContainer;
import org.example.ip4v_counter.BitSetContainerImpl;
import org.example.ip4v_counter.converter.Ip4vAddrConverter;
import org.example.ip4v_counter.validator.Validator;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.logging.Logger;

public class BitSetIp4vAddrCounter implements Ip4vAddrCounter {

    private final Logger logger;
    private final BitSetContainer bitSetContainer;
    private final Validator<String> ip4vAddrValidator;
    private final Ip4vAddrConverter<String, Integer> ip4vAddrConverter;

    public static final long NBITS = 1L << 32;
    private static final String FILE_WAS_NOT_FOUND_MESSAGE = "File '%s' was not found";
    private static final String INVALID_IP_ADDR_MESSAGE = "Invalid ip address '%s' on %d line\n";

    public BitSetIp4vAddrCounter(
            Validator<String> ip4vAddrValidator, Ip4vAddrConverter<String, Integer> ip4vAddrConverter, Logger logger
    ) {
        this.bitSetContainer = new BitSetContainerImpl(NBITS);
        this.logger = logger;
        this.ip4vAddrValidator = ip4vAddrValidator;
        this.ip4vAddrConverter = ip4vAddrConverter;
    }

    public long count(String filename) {
        bitSetContainer.clear();

        try (LineNumberReader reader = new LineNumberReader(new FileReader(filename))) {
            String s;
            while ((s = reader.readLine()) != null) {
                if (!ip4vAddrValidator.validate(s)) {
                    logger.warning(INVALID_IP_ADDR_MESSAGE.formatted(s, reader.getLineNumber()));
                    continue;
                }

                int num = ip4vAddrConverter.convert(s);

                bitSetContainer.set(num);
            }
        } catch (FileNotFoundException e) {
            logger.severe(FILE_WAS_NOT_FOUND_MESSAGE.formatted(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return bitSetContainer.cardinality();
    }
}
