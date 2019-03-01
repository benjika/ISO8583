package org.hit.fintech2018.katz;

import java.util.Map;

public interface ISO8583Serializer {
    public byte[] serializeISO8583(int version, Map<Integer, byte[]> data) throws Exception;
}
