package com.jjlink.jieyun.njuwlan.util;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by zlu on 15-3-11.
 */
public class ToolUtil {

    /**
     * 合并两个数组
     */
    public static byte[] uniteArry(byte[] a, byte[] b) {
        byte[] c = new byte[a.length + b.length];
        System.arraycopy(a, 0, c, 0, a.length);
        System.arraycopy(b, 0, c, a.length, b.length);
        return c;
    }

    /**
     * char转换成byte[]
     *
     * @param c
     * @return
     */
    public static byte[] charToByte(char c) {
        byte[] b = new byte[2];
        b[0] = (byte) ((c & 0xFF00) >> 8);
        b[1] = (byte) (c & 0xFF);
        return b;
    }

    /**
     * int转换为byte[]
     *
     * @param i
     * @return
     */
    public static byte[] intToByte(int i) {

        byte[] abyte0 = new byte[4];

        abyte0[0] = (byte) (0xff & i);

        abyte0[1] = (byte) ((0xff00 & i) >> 8);

        abyte0[2] = (byte) ((0xff0000 & i) >> 16);

        abyte0[3] = (byte) ((0xff000000 & i) >> 24);

        return abyte0;

    }

    /**
     * byte[]转换成int
     *
     * @param bytes
     * @return
     */
    public static int bytesToInt(byte[] bytes) {

        int addr = bytes[0] & 0xFF;

        addr |= ((bytes[1] << 8) & 0xFF00);

        addr |= ((bytes[2] << 16) & 0xFF0000);

        addr |= ((bytes[3] << 24) & 0xFF000000);

        return addr;

    }

    /**
     * 转化字符串为十六进制编码
     */
    public static String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = (int) s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * byte数组转换成16进制字符串
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    private static byte toByte(char c) {
        byte b = (byte) "0123456789ABCDEF".indexOf(c);
        return b;
    }

    /**
     * 16进制字符串转换成byte[]
     *
     * @param hex
     * @return
     */
    public static byte[] hexStringToByte(String hex) {
        int len = (hex.length() / 2);
        byte[] result = new byte[len];
        char[] achar = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
        }
        return result;
    }

    /**
     * 将对象序列化成byte[]
     *
     * @param obj
     * @return
     */
    public static byte[] objectToBytes(Object obj) {
        byte[] bytes = null;

        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream oo = new ObjectOutputStream(bo);
            oo.writeObject(obj);
            bytes = bo.toByteArray();
            bo.close();
            oo.close();
        } catch (IOException e) {
            Log.i("ex:", "translation" + e.getMessage());
        }

        return bytes;
    }

    /**
     * 将byte[]转换成object
     *
     * @param bytes
     * @return
     */
    public static Object byteToObject(byte[] bytes) {
        Object obj = null;

        try {
            ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
            ObjectInputStream oi = new ObjectInputStream(bi);
            obj = oi.readObject();
            bi.close();
            oi.close();
        } catch (Exception e) {
            Log.i("ex:", "translation" + e.getMessage());
        }
        return obj;
    }

}
