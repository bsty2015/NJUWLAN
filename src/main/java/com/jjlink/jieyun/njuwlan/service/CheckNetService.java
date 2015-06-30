package com.jjlink.jieyun.njuwlan.service;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.jjlink.jieyun.njuwlan.activity.StartPage;
import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;
import com.jjlink.jieyun.njuwlan.util.WifiUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zlu on 5/15/15.
 */
public class CheckNetService extends Service {
    private Timer timer = null;
    private TimerTask timerTask;
    private static final String W_SSID = "NJU-WLAN";
    private static String SERVER_NAME = "[redirect.nju.edu.cn]";
    private static String URL_ADDR = "http://www.baidu.com/";
    JieyunLog logger = new JieyunLog();
    String ssidStr = "\"" + W_SSID + "\"";
    WifiManager wifiManager;
    WifiUtil wifiUtil = new WifiUtil(ContextUtil.getInstance());
    private ContextUtil app;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logger.debug("检查网络连接服务开始。。。");
        long period = 5 * 1000;
        int delay = 0;
        if (timer == null) {
            timer = new Timer();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                boolean daemonService= StartPage.isServiceWorked(getApplicationContext(), "com.jjlink.service.DaemonService");
                if(!daemonService){
                    startService(new Intent(CheckNetService.this,DaemonService.class));
                    Log.e("CheckNetService","Start DaemonService");
                }
                app = ContextUtil.getInstance();
                SharedPreferences sp=app.getSharedPreferences("NETSTAT",Context.MODE_WORLD_READABLE);
                if (isSSID_connected()) {
                    app.setIsSsidCon(true);
                    sp.edit().putBoolean("SSIDCON",true).commit();
                    if (isWifiToInternet()) {
                        app.setIsWifiToInter(true);
                        sp.edit().putBoolean("WIFITOINTER",true).commit();
                        app.setIsNetwork(true);
                        sp.edit().putBoolean("NETWORKENABLE",true).commit();
                    } else {
                        sp.edit().putBoolean("WIFITOINTER",false).commit();
                        app.setIsWifiToInter(false);
                        app.setIsNetwork(false);
                        sp.edit().putBoolean("NETWORKENABLE",false).commit();
                    }
                } else {
                    app.setIsSsidCon(false);
                    sp.edit().putBoolean("SSIDCON",false).commit();
                    app.setIsNetwork(false);
                    sp.edit().putBoolean("NETWORKENABLE",false).commit();
                }

            }
        };
        timer.schedule(timerTask, delay, period);
        return START_REDELIVER_INTENT;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 查看网络是否可用
     *
     * @return
     */
    public boolean isWifiToInternet() {
        HttpURLConnection conn = null;
        try {
            URL url = new URL(URL_ADDR);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(10 * 1000);
            conn.setReadTimeout(10 * 1000);
            conn.connect();
            int code = conn.getResponseCode();
            //conn.getHeaderField()
            //System.out.println(conn.getURL().toString());
            Map<String, List<String>> maps = conn.getHeaderFields();
            Iterator it = maps.entrySet().iterator();
            StringBuffer sb = new StringBuffer();
            String server = Arrays.toString(maps.get("Server").toArray());
            //System.out.println("server:" + server);

            if (code == 200 && !server.trim().equals(SERVER_NAME)) {
                //logger.debug("判断能否连上www.baidu.com:internet可用");
                //createNotification("可以上网",R.drawable.logo);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.debug("检查是否能上网:不能上网" + e.getMessage());
            return false;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
        logger.debug("判断能否连上www.baidu.com:internet不可用");
        return false;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private boolean isSSID_connected() {
        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Context context = ContextUtil.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
/*        logger.debug("wifiManager.isWifiEnabled():"+wifiManager.isWifiEnabled()+"networkInfo.isAvailable():"+networkInfo.isAvailable()
                    +"networkInfo.isConnected():"+networkInfo.isConnected()+"!wifiInfo.getSSID().isEmpty()"+!wifiInfo.getSSID().isEmpty()+"wifiInfo.getSSID().equalsIgnoreCase(ssidStr):"+
                wifiInfo.getSSID().equalsIgnoreCase(ssidStr)+"wifiInfo.getSSID().equalsIgnoreCase(ssidStr):"+wifiInfo.getSSID().equalsIgnoreCase(W_SSID))*/

        if (networkInfo.isAvailable() && networkInfo.isConnected()
                && wifiInfo != null && !wifiInfo.getSSID().isEmpty()
                && (wifiInfo.getSSID().equalsIgnoreCase(W_SSID) || wifiInfo.getSSID().equalsIgnoreCase(ssidStr))) {
            return true;
        }
        //createWifiSettingNotification(W_SSID + "未连接", R.drawable.portal_unconnect);
        logger.debug("没有连接上NJU-WLAN");
        return false;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
