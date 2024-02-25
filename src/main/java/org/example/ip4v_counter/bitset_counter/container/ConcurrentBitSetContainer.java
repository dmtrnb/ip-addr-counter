package org.example.ip4v_counter.bitset_counter.container;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentBitSetContainer extends SimpleBitSetContainer implements BitSetContainer {
    private final Lock[] locks;

    public ConcurrentBitSetContainer(long nbits) {
        super(nbits);

        int size = (int) divideRoundUp(nbits, SIZE_ONE_BIT_SET);

        this.locks = new Lock[size];
        for (int i = 0; i < size; i++) {
            this.locks[i] = new ReentrantLock();
        }
    }

    @Override
    public void set(long bitIndex) {
        if (bitIndex < minIndex || bitIndex >= maxIndex) {
            throw new IndexOutOfBoundsException("The bitIndex is outside the range of acceptable values: " + bitIndex);
        }

        boolean isNegative = bitIndex < 0;
        if (isNegative) {
            bitIndex = ~bitIndex;
        }
        int index = (int) (bitIndex % SIZE_ONE_BIT_SET);
        int arrayIndex = 2 * (int) (bitIndex / SIZE_ONE_BIT_SET) + (isNegative ? 1 : 0);

        Lock lock = locks[arrayIndex];
        lock.lock();
        try {
            bitSets[arrayIndex].set(index);
        } finally {
            lock.unlock();
        }
    }
}
