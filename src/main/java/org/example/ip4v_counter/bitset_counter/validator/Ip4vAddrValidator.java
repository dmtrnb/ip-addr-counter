package org.example.ip4v_counter.bitset_counter.validator;

import java.util.regex.Pattern;

public class Ip4vAddrValidator implements Validator<String> {

    private static final Pattern PATTERN = Pattern.compile(
            "^((1?\\d\\d?|2[0-4]\\d|25[0-5])\\.){3}(1?\\d\\d?|2[0-4]\\d|25[0-5])$");

    @Override
    public boolean validate(String s) {
        return PATTERN.matcher(s).matches();
    }
}
