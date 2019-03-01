package org.hit.fintech2018.katz;

import java.util.Arrays;

public class Encoder {

    public Encoder() {
    }


    public byte[] encode(ISO8583Field field, byte[] data) throws Exception {

        if (field == null || data == null)
            throw new Exception("Null pointer error");

        if (data.length == 0)
            throw new Exception("Field #" + field.getId() + " " + "data field is empty");


        String className = field.getClassName();

        if (className.equalsIgnoreCase("Bitmap"))
            return bitmapEncoding(field, data);

        else if (className.equalsIgnoreCase("Ascii")
                || className.equalsIgnoreCase("Ascii-L")
                || className.equalsIgnoreCase("Ascii-LL")
                || className.equalsIgnoreCase("Ascii-LLL"))
            return asciiEncoding(field, data);

        else if (className.equalsIgnoreCase("Binary"))
            return binaryEncoding(field, data);


        else if (className.equalsIgnoreCase("Numeric") ||
                className.equalsIgnoreCase("Numeric-L") ||
                className.equalsIgnoreCase("Numeric-LL"))
            return numericEncoding(field, data);

        else
            throw new Exception("Field #" + field.getId() + " " + "Wrong class name");
    }

    byte[] mtiEncoding(ISO8583Field field, int data) throws Exception {
        if (9999 < data)
            throw new Exception("Field #" + field.getId() + " " + "wrong MTI length");

        byte[] MTI = new byte[4];
        MTI[0] = (byte) (data);
        MTI[1] = (byte) (1);


        return packArrBitToByte(MTI);


    }

    private byte[] bitmapEncoding(ISO8583Field field, byte[] data) throws Exception {

        validateBinaryArray(data, field.getId());

        if (data.length != 4 * field.getLength())
            throw new Exception("Field #" + field.getId() + " " + "Wrong input length");

        byte[] result = new byte[field.getLength()];

        for (int i = 0, j = 0; i < result.length; i++, j += 4) {
            result[i] = packBinaryToHex(Arrays.copyOfRange(data, j, j + 4));
        }

        return packArrBitToByte(result);
    }

    private byte[] numericEncoding(ISO8583Field field, byte[] data) throws Exception {

        validateNumericArray(data, field.getId());

        if (field.isFixed()) {

            if (data.length > field.getLength())
                throw new Exception("Field #" + field.getId() + " " + "input is too long");

            byte[] result = new byte[field.getLength() - data.length];

            result = concatArrays(result, data);

            return packArrBitToByte(result);

        } else {

            if (data.length > field.getLength())
                throw new Exception("Field #" + field.getId() + " " + "input is too long");

            byte[] prefix = null;

            if (field.getClassName().equalsIgnoreCase("Numeric-L"))
                prefix = getPrefix(data.length, 1, field.getId());

            else if (field.getClassName().equalsIgnoreCase("Numeric-LL"))
                prefix = getPrefix(data.length, 2, field.getId());

            else
                throw new Exception("Field #" + field.getId() + " " + "wrong class name(not fixed & not prefix length");

            // byte[] result = concatArrays(prefix, data);

            return packArrBitToByte(concatArrays(prefix, data));
        }
    }

    private byte[] asciiEncoding(ISO8583Field field, byte[] data) throws Exception {

        if (field.isFixed()) {

            if (data.length > field.getLength())
                throw new Exception("Field #" + field.getId() + " " + "input is too long");

            StringBuilder hexStringBuilder = new StringBuilder();

            for (int i = 0; i < field.getLength() - data.length; i++) {
                hexStringBuilder.append(Integer.toHexString((int) ' '));
            }

            byte[] result = asciiToHex(hexStringBuilder, data, field.getId());

            return packArrBitToByte(result);

        } else {

            if (data.length > field.getLength())
                throw new Exception("Field #" + field.getId() + " " + "input is too long");

            byte[] prefix = null;

            if (field.getClassName().equalsIgnoreCase("Ascii-L"))
                prefix = getPrefix(data.length, 1, field.getId());

            else if (field.getClassName().equalsIgnoreCase("Ascii-LL"))
                prefix = getPrefix(data.length, 2, field.getId());

            else if (field.getClassName().equalsIgnoreCase("Ascii-LLL"))
                prefix = getPrefix(data.length, 3, field.getId());

            else
                throw new Exception("Field #" + field.getId() + " " + "wrong class name(not fixed & not prefix length");


            byte[] result = asciiToHex(new StringBuilder(), data, field.getId());

            return packArrBitToByte(concatArrays(prefix, result));
        }
    }

    private byte[] binaryEncoding(ISO8583Field field, byte[] data) throws Exception {

        validateBinaryArray(data, field.getId());

        if (field.isFixed()) {

            if (data.length > field.getLength())
                throw new Exception("Field #" + field.getId() + " " + "Input is too long");

            byte[] result = new byte[field.getLength()];

            System.arraycopy(data, 0, result, 0, data.length);

            return packArrBitToByte(result);

        } else {
            byte[] result = new byte[data.length + 1];

            //TODO: check packing of length(Binary)
            result[0] = (byte) data.length;

            System.arraycopy(data, 0, result, 1, data.length);

            if (result.length > field.getLength())
                throw new Exception("Field #" + field.getId() + " " + "Input is too long");

            return packArrBitToByte(result);
        }
    }

    private void validateBinaryArray(byte[] arr, int fieldId) throws Exception {
        for (byte b : arr)
            if (b != 1 && b != 0)
                throw new Exception("Field #" + fieldId + " " + "Binary array has not binary values");
    }

    private void validateNumericArray(byte[] arr, int fieldId) throws Exception {
        for (byte b : arr) {
            if (b > (byte) 9)
                throw new Exception("Field #" + fieldId + " " + "not all data members are decimal numbers");
        }
    }

    private byte[] packArrBitToByte(byte[] arr) {

        byte[] result = new byte[arr.length / 2 + arr.length % 2];
        int i = 0;
        boolean even = true;

        for (byte b : arr) {
            if (even) result[i] = (byte) ((b & 0x0f) << 4);
            else result[i++] += (byte) (b % 0x0f);
            even = !even;
        }

        return result;
    }

    byte[] concatArrays(byte[] a, byte[] b) throws Exception {

        if (a == null || b == null)
            throw new Exception("Null pointer");

        if (a.length == 0) return b;
        if (b.length == 0) return a;

        byte[] result = new byte[a.length + b.length];
        System.arraycopy(a, 0, result, 0, a.length);
        System.arraycopy(b, 0, result, a.length, b.length);
        return result;

    }

    private byte[] asciiToHex(StringBuilder stringBuilder, byte[] data, int fieldId) throws Exception {
        if (stringBuilder == null)
            throw new Exception("Field #" + fieldId + " " + "StringBuilder is null pointer");
        if (data == null)
            throw new Exception("Field #" + fieldId + " " + "data is null pointer");
        if (data.length == 0)
            throw new Exception("Field #" + fieldId + " " + "data length is 0");

        char[] chars = Arrays.toString(data).toCharArray();
        for (char aChar : chars) {
            stringBuilder.append(Integer.toHexString((int) aChar));
        }
        String hexString = stringBuilder.toString();

        byte[] result = new byte[hexString.length()];
        for (int i = 0; i < result.length; i++) {
            result[i] = (byte) (Character.digit(hexString.charAt(i), 16));
        }
        return result;

    }

    private byte[] getPrefix(int dataLength, int amountOfLs, int fieldId) throws Exception {

        if (amountOfLs == 1) {
            if (dataLength > 9)
                throw new Exception("Field #" + fieldId + " " + "Input is too long(L prefix)");
            byte[] prefix = new byte[2];
            prefix[1] = (byte) dataLength;
            return prefix;
        } else if (amountOfLs == 2) {
            if (dataLength > 99)
                throw new Exception("Field #" + fieldId + " " + "Input is too long(LL prefix)");
            byte[] prefix = new byte[2];
            prefix[0] = (byte) (dataLength / 16);
            prefix[1] = (byte) (dataLength % 16);
            return prefix;
        } else if (amountOfLs == 3) {
            if (dataLength > 999)
                throw new Exception("Field #" + fieldId + " " + "Input is too long(LLL prefix)");
            byte[] prefix = new byte[4];
            prefix[1] = (byte) (dataLength / 256);
            prefix[2] = (byte) ((dataLength % 256) / 16);
            prefix[3] = (byte) (dataLength % 16);
            return prefix;
        } else
            throw new Exception("Field #" + fieldId + " " + "Wrong L's amount");

    }

    private byte packBinaryToHex(byte[] data) throws Exception {

        if (data.length != 4)
            throw new Exception("");

        byte result = 0;
        int exponent = 8;

        for (byte datum : data) {
            result = (byte) (result + exponent * datum);
            exponent /= 2;
        }

        String hex = Integer.toHexString(result);

        return Byte.parseByte(hex, 16);
    }

    static String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for (byte b : a)
            sb.append(String.format("%02x ", b));
        return sb.toString();
    }
}
