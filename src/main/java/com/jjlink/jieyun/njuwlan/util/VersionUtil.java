package com.jjlink.jieyun.njuwlan.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by zlu on 15-3-18.
 */
public class VersionUtil {
    /**
     * 获取版本号
     * @param context
     * @return
     * @throws PackageManager.NameNotFoundException
     */
    public static String getCurVersion(Context context) throws PackageManager.NameNotFoundException {
        //获取PackageManager实例
        PackageManager packageManager=context.getPackageManager();
        //获取context的版本属性，0表示获取版本信息
        PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(),0);
        return packageInfo.versionName;
    }
}
