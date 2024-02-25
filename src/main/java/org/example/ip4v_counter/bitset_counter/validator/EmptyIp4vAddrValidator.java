package org.example.ip4v_counter.bitset_counter.validator;

public class EmptyIp4vAddrValidator implements Validator<String> {
    @Override
    public boolean validate(String s) {
        return true;
    }
}
