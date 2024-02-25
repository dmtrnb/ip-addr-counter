package org.example.ip4v_counter.bitset_counter.converter;

public class String2IntegerIp4VAddrConverter implements Ip4vAddrConverter<String, Integer> {

    private final Ip4vAddrConverter<String, short[]> string2ShortsIp4VAddrConverter;
    private final Ip4vAddrConverter<short[], Integer> shorts2IntegerIp4VAddrConverter;

    public String2IntegerIp4VAddrConverter(Ip4vAddrConverter<String, short[]> string2ShortsIp4VAddrConverter,
                                           Ip4vAddrConverter<short[], Integer> shorts2IntegerIp4VAddrConverter) {
        this.string2ShortsIp4VAddrConverter = string2ShortsIp4VAddrConverter;
        this.shorts2IntegerIp4VAddrConverter = shorts2IntegerIp4VAddrConverter;
    }

    @Override
    public Integer convert(String s) {
        short[] shorts = string2ShortsIp4VAddrConverter.convert(s);
        return shorts2IntegerIp4VAddrConverter.convert(shorts);
    }
}
