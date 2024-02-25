package org.example.ip4v_counter.validator;

public interface Validator<T> {

    boolean validate(T t);
}
