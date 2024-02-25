package org.example.ip4v_counter.container;

import java.util.Arrays;
import java.util.BitSet;

public class SimpleBitSetContainer implements BitSetContainer {

    protected static final long SIZE_ONE_BIT_SET = Integer.MAX_VALUE + 1L;

    protected final BitSet[] bitSets;
    protected final long minIndex;
    protected final long maxIndex;

    public SimpleBitSetContainer(long nbits) {
        int size = (int) divideRoundUp(nbits, SIZE_ONE_BIT_SET);

        bitSets = new BitSet[size];
        for (int i = 0; i < size; i++) {
            bitSets[i] = new BitSet();
        }

        minIndex = -(size / 2) * SIZE_ONE_BIT_SET;
        maxIndex = divideRoundUp(size, 2) * SIZE_ONE_BIT_SET;
    }

    @Override
    public void clear() {
        Arrays.stream(bitSets).forEach(BitSet::clear);
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

        bitSets[arrayIndex].set(index);
    }

    @Override
    public long cardinality() {
        return Arrays.stream(bitSets)
                .mapToLong(BitSet::cardinality)
                .sum();
    }

    protected long divideRoundUp(long num, long divisor) {
        long mod = num % divisor;
        return num / divisor + (mod != 0 ? 1 : 0);

    }
}
