package org.example.ip4v_counter.bitset_counter.converter;

public class Shorts2IntegerIp4VAddrConverter implements Ip4vAddrConverter<short[], Integer> {
    @Override
    public Integer convert(short[] shorts) {
        return shorts[0] << 24 | shorts[1] << 16 | shorts[2] << 8 | shorts[3];
    }
}
