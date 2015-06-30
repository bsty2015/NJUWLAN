package com.jjlink.jieyun.njuwlan.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by zlu on 15-3-9.
 */
public class AndroidUtil {
    public static StringBuffer buffer = null;

    /**
     * 获取操作系统版本
     *
     * @return
     */
    public static String fetch_verison_info() {
        String result = null;
        CMDExecute cmdexe = new CMDExecute();

        try {
            String[] args = {"/system/bin/cat", "/proc/version"};
            result = cmdexe.run(args, "system/bin/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String initProperty(String description, String propertyStr) {
        if (buffer == null) {
            buffer = new StringBuffer();
        }
        buffer.append(description).append(":");
        buffer.append(System.getProperty(propertyStr)).append("\n");
        return buffer.toString();
    }

    /**
     * 获取系统信息
     *
     * @return
     */
    public static String getSystemProperty() {
        buffer = new StringBuffer();
        initProperty("java.vendor.url", "java.vendor.url");
        initProperty("java.class.path", "java.class.path");
        return buffer.toString();
    }

    /**
     * 获取运营商信息
     *
     * @param cx
     * @return
     */
    public static String fetch_tel_status(Context cx) {
        String result = null;
        TelephonyManager tm = (TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
        String str = "";
        str += "DeviceID(IMEI)= " + tm.getDeviceId() + "\n";
        str += "DeviceSoftwareVersion = " + tm.getDeviceSoftwareVersion() + "\n";
        int mcc = cx.getResources().getConfiguration().mcc;
        int mnc = cx.getResources().getConfiguration().mnc;
        str += "IMSI MCC(Mobile Country Code):" + String.valueOf(mcc) + "\n";
        str += "IMSI MNC(Moblie Network Code):" + String.valueOf(mnc) + "\n";
        str += "手机号码：" + tm.getLine1Number() + "\n";
        // str+="MmsUserAgent:"+tm.getMmsUserAgent()+"\n";
        str += "SimState:" + tm.getSimState() + "\n";
        str += "SimOperatorName:" + tm.getSimOperatorName() + "\n";
        str += "SimCountryIso:" + tm.getSimCountryIso() + "\n";
        result = str;
        return result;
    }

    /**
     * 获取IMSI
     *
     * @param cx
     * @return
     */
    public static String getIMSI(Context cx) {
        TelephonyManager tm = (TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getSubscriberId();
    }

    public static String getIMEI(Context cx) {
        TelephonyManager tm = (TelephonyManager) cx.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取cpu信息
     *
     * @return
     */
    public static String fetch_cpu_info() {
        String result = null;
        CMDExecute cmdExecute = new CMDExecute();
        try {
            String[] args = {"/system/bin/cat", "/proc/cpuinfo"};
            result = cmdExecute.run(args, "/system/bin");
            Log.i("result", "result=" + result);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 获取内存信息
     *
     * @param context
     * @return
     */
    public static String getMemoryInfo(Context context) {
        StringBuffer memoryInfo = new StringBuffer();
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(outInfo);
        memoryInfo.append("\nTotal Available Memory :").append(outInfo.availMem >> 10).append("k");
        memoryInfo.append("\nTotal Available Memory :").append(outInfo.availMem >> 20).append("M");
        memoryInfo.append("\nTotal Available Memory ：").append(outInfo.lowMemory);
        String result = null;
        CMDExecute cmdExecute = new CMDExecute();
        try {
            String[] args = {"/system/bin/cat", "/proc/meminfo"};
            result = cmdExecute.run(args, "/system/bin");
        } catch (IOException e) {
            Log.i("fetch_process_info", "ex=" + e.toString());
        }

        return (memoryInfo.toString()) + "\n\n" + result;
    }

    /**
     * 获取网络信息
     *
     * @return
     */
    public static String fetch_netcfg_info() {
        String result = null;
        CMDExecute cmdExecute = new CMDExecute();

        try {
            String[] args = {"/system/bin/netcfg"};
            result = cmdExecute.run(args, "/system/bin");
        } catch (IOException e) {
            Log.i("fetch_process_info", "e=" + e.toString());
        }
        return result;
    }

    /**
     * 获取显示屏信息
     *
     * @param cx
     * @return
     */
    public static String getDisplayMetrics(Context cx) {
        String str = "";
        DisplayMetrics dm = new DisplayMetrics();
        dm = cx.getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;
        float density = dm.density;
        float xdpi = dm.xdpi;
        float ydpi = dm.ydpi;
        str += "The absolute width:" + String.valueOf(screenWidth) + "pixels\n";
        str += "The absolute height:" + String.valueOf(screenHeight) + "pixels\n";
        str += "The Logical density of the display.:" + String.valueOf(density) + "\n";
        str += "X dimension:" + String.valueOf(xdpi) + "pixels per inch\n";
        str += "Y dimension:" + String.valueOf(ydpi) + "pixels per inch\n";

        return str;
    }

    /**
     * 获取androidID
     *
     * @param context
     * @return
     */
    public static String getAndroidId(Context context) {
        String androidID = null;
        androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        return androidID + "\n实现代码：androidID= Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID);\n";
    }

    /**
     * 获取服务信息
     *
     * @param context
     * @return
     */
    public static String getRunningServiceInfo(Context context) {
        StringBuffer serviceInfo = new StringBuffer();
        final ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager.getRunningServices(200);
        Iterator<ActivityManager.RunningServiceInfo> it = services.iterator();
        while (it.hasNext()) {
            ActivityManager.RunningServiceInfo si = it.next();
            serviceInfo.append("pid: ").append(si.pid);
            serviceInfo.append("\nprocess: ").append(si.process);
            serviceInfo.append("\nservice: ").append(si.service);
            serviceInfo.append("\ncrashCount: ").append(si.crashCount);
            serviceInfo.append("\nclientCount: ").append(si.clientCount);
            serviceInfo.append("\nactiveSince: ").append((si.activeSince));
            serviceInfo.append("\nlastActivityTime: ").append(si.lastActivityTime);
            serviceInfo.append("\n\n");
        }
        return serviceInfo.toString();
    }

    /**
     * 获取序号
     *
     * @return
     */
    public static String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            Log.v("exception:", e.toString());

        }
        return serial;
    }




    /**
     * 将参数转换成tlv对象
     *
     * @param tag
     * @param value
     * @return Tlv
     */
    public static Tlv getTlvFromParam(String tag, String value) {
        if (value != null && value.length() > 0) {
            return new Tlv(tag, value.length()+2, value);
        }
        return null;
    }

    /**
     * 将tlv对象数组3des加密
     *
     * @param tlvs
     * @return String
     */
    public static String threeDesEncryptTlvs(Tlv[] tlvs, String randStr) throws Exception {
        byte[] bytes=new byte[tlvs.length];
        byte[] data1=ToolUtil.objectToBytes(tlvs[0]);
        byte[] datas=null;
        for(int i=1;i<tlvs.length;i++){
            byte[] data2 = ToolUtil.objectToBytes(tlvs[i]);
            byte[] data3=new byte[data1.length+data2.length];
            System.arraycopy(data1,0,data3,0,data1.length);
            System.arraycopy(data2,0,data3,data1.length,data2.length);
            datas=data3;
        }
        // String randStr=ThreeDes.getRandInt();
        Log.i("randStr:", randStr);
        String key = ThreeDes.getKey(randStr);
        Log.i("key:", key);
        Log.i("keyBytes:", Arrays.toString(key.getBytes("utf-8")));
        Log.i("encryptString:", ThreeDes.toBase64Des(key.getBytes("UTF-8"), datas));
        return ThreeDes.toBase64Des(key.getBytes("UTF-8"), datas);
    }

    public static Notification buildNotification(){
        return null;
    }


}
