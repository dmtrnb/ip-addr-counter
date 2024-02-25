package org.example.ip4v_counter.converter;

public class String2ShotrsIp4VAddrConverter implements Ip4vAddrConverter<String, short[]> {

    private final short[] shorts = new short[COUNT_OF_OCTETS];

    @Override
    public short[] convert(String s) {
        String[] strings = s.split("\\.");

        for (int i = 0; i < COUNT_OF_OCTETS; i++) {
            shorts[i] = Short.parseShort(strings[i]);
        }

        return shorts;
    }
}