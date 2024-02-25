package org.example.ip4v_counter.converter;

public interface Converter<T, R> {

    R convert(T t);
}
