package org.example.ip4v_counter.bitset_counter.container;

public interface BitSetContainer {

    void clear();
    void set(long bitIndex);
    long cardinality();
}
