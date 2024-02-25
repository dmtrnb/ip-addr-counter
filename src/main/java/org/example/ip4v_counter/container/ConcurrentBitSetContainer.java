package org.example.ip4v_counter.container;

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

        int arrayIndex, index;
        if (bitIndex >= 0) {
            arrayIndex = 2 * (int) (bitIndex / SIZE_ONE_BIT_SET);
            index = (int) (bitIndex % SIZE_ONE_BIT_SET);
        } else {
            arrayIndex = 2 * (int) (~bitIndex / SIZE_ONE_BIT_SET) + 1;
            index = (int) (~bitIndex % SIZE_ONE_BIT_SET);
        }

        Lock lock = locks[arrayIndex];
        lock.lock();
        try {
            bitSets[arrayIndex].set(index);
        } finally {
            lock.unlock();
        }
    }
}
