package org.example.ip4v_counter.bitset_counter.container;

import org.example.ip4v_counter.bitset_counter.converter.Ip4vAddrConverter;
import org.example.ip4v_counter.bitset_counter.converter.Shorts2IntegerIp4VAddrConverter;
import org.example.ip4v_counter.bitset_counter.converter.String2IntegerIp4VAddrConverter;
import org.example.ip4v_counter.bitset_counter.converter.String2ShotrsIp4VAddrConverter;
import org.example.ip4v_counter.bitset_counter.validator.Ip4vAddrValidator;
import org.example.ip4v_counter.bitset_counter.validator.Validator;

import java.util.logging.Logger;

public class DependencyContainer {

    private final Logger[] loggers;
    private final Validator<String>[] validators;
    private final Ip4vAddrConverter<String, Integer>[] converters;

    @SuppressWarnings("unchecked")
    public DependencyContainer(int count) {
        loggers = new Logger[count];
        validators = new Validator[count];
        converters = new Ip4vAddrConverter[count];

        for (int i = 0; i < count; i++) {
            loggers[i] = Logger.getGlobal();
            validators[i] = new Ip4vAddrValidator();
            Ip4vAddrConverter<String, short[]> string2ShortsIp4vAddrConverter = new String2ShotrsIp4VAddrConverter();
            Ip4vAddrConverter<short[], Integer> shorts2IntegerIp4vAddrConverter = new Shorts2IntegerIp4VAddrConverter();
            converters[i] = new String2IntegerIp4VAddrConverter(
                    string2ShortsIp4vAddrConverter, shorts2IntegerIp4vAddrConverter
            );
        }
    }

    public Logger getLogger(int index) {
        return loggers[index];
    }

    public Validator<String> getValidator(int index) {
        return validators[index];
    }

    public Ip4vAddrConverter<String, Integer> getConverter(int index) {
        return converters[index];
    }
}
