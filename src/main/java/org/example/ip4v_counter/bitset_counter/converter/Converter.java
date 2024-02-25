package org.example.ip4v_counter.bitset_counter.converter;

public interface Converter<T, R> {

    R convert(T t);
}
