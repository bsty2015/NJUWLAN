package com.jjlink.jieyun.njuwlan.util;

import android.app.Application;

/**
 * android设置全局变量
 * 重写application的onCreate方法
 * Created by zlu on 15-3-27.
 */
public class Jieyun extends Application {
    private boolean IS_AUTO;

    public boolean isIS_AUTO() {
        return IS_AUTO;
    }

    public void setIS_AUTO(boolean IS_AUTO) {
        this.IS_AUTO = IS_AUTO;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setIS_AUTO(false);
    }
}
