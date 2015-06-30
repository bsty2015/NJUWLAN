package com.jjlink.jieyun.njuwlan.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Created by zlu on 15-3-10.
 */
public class Tlv implements Serializable{
    /** 子域Tag标签 */
    private String tag;
    /** 子域取值的长度 */
    private int length;
    /** 子域取值 */
    private String value;

    public Tlv(String tag, int length, String value) {
        this.tag = tag;
        this.length = length;
        this.value = value;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Tlv{" +
                "tag='" + tag + '\'' +
                ", length='" + length + '\'' +
                ", value='" + value + '\'' +
                '}';
    }

}
