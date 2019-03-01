package org.hit.fintech2018.katz;

import java.util.Map;

public class MySerializer implements ISO8583Serializer {

    @Override
    public byte[] serializeISO8583(int version, Map<Integer, byte[]> data) {
        XMLParserISO8583 xmlParserISO8583 = new XMLParserISO8583();
        Encoder encoder = new Encoder();
        byte[] result = null;
        try {

            Map<Integer, ISO8583Field> ISO8583FieldsHashMap = xmlParserISO8583.buildFieldsArray();

            result = encoder.mtiEncoding(ISO8583FieldsHashMap.get(0), version);

            for (int i = 1; i < ISO8583FieldsHashMap.size(); i++) {
                if (data.containsKey(i))
                    result = encoder.concatArrays(result, encoder.encode(ISO8583FieldsHashMap.get(i), data.get(i)));
            }

            System.out.println(Encoder.byteArrayToHex(result));
            return result;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

}
