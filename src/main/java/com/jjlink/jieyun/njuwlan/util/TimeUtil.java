package com.jjlink.jieyun.njuwlan.util;

/**
 * 获取过去的时间
 * Created by zlu on 15-3-19.
 */
public class TimeUtil {
    public static long getTimePass(long start){
        return System.currentTimeMillis()-start;
    }
}
