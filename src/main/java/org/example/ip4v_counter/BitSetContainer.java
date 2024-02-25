package org.example.ip4v_counter;

public interface BitSetContainer {

    void clear();
    void set(long bitIndex);
    long cardinality();
}
