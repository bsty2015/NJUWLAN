package com.jjlink.jieyun.njuwlan.util;

import android.app.Application;
import android.os.Bundle;

/**
 * Created by zlu on 15-3-6.
 */
public class ContextUtil extends Application {
    private static ContextUtil instance;
    private boolean isAuto;
    private Bundle bundle;
    private boolean isNetwork;
    private boolean isSsidCon;
    private boolean isWifiToInter;
    //悬浮窗是否算单击事件，如果移动了悬浮窗位置，则不算单击事件，如果没有移动位置则算单击事件
    private boolean isFloatWdMove;


    public boolean isFloatWdMove() {
        return isFloatWdMove;
    }

    public void setIsFloatWdMove(boolean isFloatWdMove) {
        this.isFloatWdMove = isFloatWdMove;
    }

    public boolean isSsidCon() {
        return isSsidCon;
    }

    public void setIsSsidCon(boolean isSsidCon) {
        this.isSsidCon = isSsidCon;
    }

    public boolean isWifiToInter() {
        return isWifiToInter;
    }

    public void setIsWifiToInter(boolean isWifiToInter) {
        this.isWifiToInter = isWifiToInter;
    }

    public boolean isNetwork() {
        return isNetwork;
    }

    public void setIsNetwork(boolean isNetwork) {
        this.isNetwork = isNetwork;
    }

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    public boolean isAuto() {
        return isAuto;
    }

    public void setAuto(boolean isAuto) {
        this.isAuto = isAuto;
    }

    public static ContextUtil getInstance(){
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        setAuto(false);
        setIsNetwork(false);
        setIsSsidCon(false);
        setIsWifiToInter(false);
        setIsFloatWdMove(false);
        instance=this;
    }

}
