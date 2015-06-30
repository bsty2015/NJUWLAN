package com.jjlink.jieyun.njuwlan.service;


import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;

import com.jjlink.jieyun.njuwlan.log.JieyunLog;
import com.jjlink.jieyun.njuwlan.util.ContextUtil;
import com.jjlink.jieyun.njuwlan.util.NetUtil;
import com.jjlink.jieyun.njuwlan.util.WifiUtil;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zlu on 15-2-28.
 */
public class AutoConnection extends Service {
    private Timer timer = null;
    private TimerTask timerTask;
    private boolean started;
    private boolean threadDisable;
    private static String URL_ADDR = "http://www.baidu.com/";
    private static String SERVER_NAME = "[redirect.nju.edu.cn]";
    private ContextUtil app;
    private static final String URL_ADDRESS = "www.baidu.com";
    private static final String W_SSID = "NJU-WLAN";
    private static final String action = "http://p.nju.edu.cn:8080/portalSpring/applogin";
    private static final String appPackageName = "com.jjlink.jieyun";
    private SharedPreferences sp;
    private static final String W_PASSWORD = "";
    private static final int WIFI_LEVEL = -80;
    private static final int W_TYPE = 1;
    private int count = 1;
    String ssidStr = "\"" + W_SSID + "\"";
    WifiManager wifiManager;
    WifiUtil wifiUtil = new WifiUtil(ContextUtil.getInstance());
    JieyunLog logger = new JieyunLog();

    public void addNotification(int delayTime, String tickerText, String contentTitle, String contentText) {
        Intent intent = new Intent(ContextUtil.getInstance(), AutoConnection.class);
        intent.putExtra("delayTime", delayTime);
        intent.putExtra("tickerText", tickerText);
        intent.putExtra("contentTitle", contentTitle);
        intent.putExtra("contentText", contentText);
        ContextUtil.getInstance().startService(intent);

    }

    boolean ssid_exist = false;

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        logger.debug("自动连接服务:开始>>>>>>>>>>>");
        long period = 5 * 1000;
        // int delay = intent.getIntExtra("delayTime", 0);
        int delay = 0;
        //logger.debug(delay);
        if (timer == null) {
            timer = new Timer();
        }

        timerTask = new TimerTask() {
            @Override
            public void run() {
                wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                app = (ContextUtil) getApplication();
                String ssidStr = "\"" + W_SSID + "\"";
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                //先执行一次网络检查,如果断开或者不能联网则尝试自动登录,三次都不能成功则关闭自动登陆,如果是联网状态,则不需要执行自动登录
                //if (!isSSID_connected()) {
                if (app.isSsidCon() == false) {
                    //createWifiSettingNotification(W_SSID + "未连接", R.drawable.portal_unconnect);
                    logger.debug("开始自动连接wifi>>>>>>>>>>>>>>>>>>>>>");
                    autoLogin();
                    //为了刷新通知,需要添加该判断
                    /*try {
                        Thread.sleep(5 * 1000);
                        // if (isSSID_connected() && checkNetwork()) {
                        if (app.isNetwork() == true) {
                            NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "可以上网", R.drawable.logo, StartPage.class);
                            //createNotification("可以上网", R.drawable.logo);
                        } else {
                            NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "不可以上网", R.drawable.portal_unconnect, StartPage.class);

                        }
                    } catch (InterruptedException e) {
                        logger.debug("线程休眠发生异常:" + e.getMessage());
                    }*/


                } else {

                    if (app.isWifiToInter() == false) {
                        //logger.debug("网络不可用......");
                        ///NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "不能上网", R.drawable.portal_unconnect, StartPage.class);
                        //如果在wifi连接状态下用其他方式断开了portal,比如用浏览器注销了本机登录的portal,网络则不可用,app会自动重连3次,如果3次都不成功,则关闭自动重连
                        if (app.isAuto()) {
                            //logger.debug("处于自动登陆portal状态>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                            //NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "网络不可用，正在登录", R.drawable.portal_unconnect, Welcome.class);
                            logger.debug("是否自动登录portal：" + app.isAuto());
                            autoLoginPortal();
                            //为了刷新通知,需要添加该判断
                            if (app.isNetwork() == true) {
                                //NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "可以上网", R.drawable.logo, Welcome.class);
                                    /*Intent intent = new Intent(ContextUtil.getInstance(), Welcome.class);
                                    startActivity(intent);*/
                            } else {
                                notificationManager.cancel(66);
                            }
                        }
                    } else {
                        //logger.debug("可以上网...>>><<<<<....");
                        //NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        // manager.cancel(66);
                        //createNotification("可以上网", R.drawable.logo);
                    }

                }
            }
        };
        timer.schedule(timerTask, delay, period);

        //flags = START_NOT_STICKY;
        return super.onStartCommand(intent, flags, startId);
    }

   /* private void createWifiSettingNotification(String content, int icon) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextUtil.getInstance());
        builder.setContentTitle("捷运上网助手");
        builder.setContentText(content);
        builder.setSmallIcon(icon);
        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(66, notification);
    }*/


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

    /**
     * 用于在没有连接wifi或者登录portal的时候自动重连
     */
    private synchronized void autoLogin() {

        wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        Context context = ContextUtil.getInstance();
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        app = (ContextUtil) getApplication();
        sp = ContextUtil.getInstance().getSharedPreferences("userInfo", Context.MODE_WORLD_READABLE);
        if (!wifiManager.isWifiEnabled()) {
            logger.debug("wifi未打开,正在打开wifi");
            //createWifiSettingNotification("正在打开WIFI", R.drawable.portal_unconnect);
            boolean isSuccess = wifiManager.setWifiEnabled(true);
            logger.debug("打开wifi结果:" + isSuccess);
        }
/*

        //当有相同ssid时,连接信号强度高的AP,如果是5G和2.4G之分的则优先连接5G
        ScanResult theSSID=null;
        for(int i=0;i<wifiList.size()-1;i++){
            theSSID=wifiList.get(i);
            for(int j=1;j<ssidNames.size();j++){
                if(theSSID.SSID.equals(wifiList.get(j).SSID)){
                    sameSSID.add(theSSID);
                }
            }
        }

        if(sameSSID.size()>=2){
            ScanResult strongRssi=sameSSID.get(0);
            for(int i=1;i<=sameSSID.size()-1;i++){
                if(sameSSID.get(i).level>strongRssi.level){
                    strongRssi=sameSSID.get(i);
                }
            }
        }
*/
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            logger.debug(String.valueOf("是否連上wifi>>>" + wifiInfo.getSSID() == null));
            if (wifiInfo.getSSID() != null) {
                logger.debug("是否連上的是:" + W_SSID + ">>>>>>" + wifiInfo.getSSID().equalsIgnoreCase(W_SSID));
                logger.debug("是否連上的是:" + ssidStr + ">>>>>>" + wifiInfo.getSSID().equalsIgnoreCase(ssidStr));
            }
        }

        if (wifiInfo.getSSID() == null || (!wifiInfo.getSSID().equalsIgnoreCase(W_SSID) && !wifiInfo.getSSID().equalsIgnoreCase(ssidStr))) {
            logger.debug("连上的wifi:" + wifiUtil.getSSID());
            //扫描当前wifi列表
            //扫描wifi
            wifiManager.startScan();

            List<ScanResult> wifiList = wifiManager.getScanResults();
            List<ScanResult> sameSSID = new ArrayList<ScanResult>();
            List<String> ssidNames = new ArrayList<String>();

            if (wifiList != null && wifiList.size() > 0) {
                for (int i = 0; i < wifiList.size(); i++) {
                    ssidNames.add(wifiList.get(i).SSID);
                }
            }

            //如果扫描列表里存在需要连接的ssid
            if (ssidNames.contains(W_SSID)) {
                logger.debug(W_SSID + "存在");
                //自动配置指定的SSID并连接
                WifiConfiguration wifiConfiguration = wifiUtil.createWifiInfo(W_SSID, W_PASSWORD, W_TYPE);
                wifiUtil.addNetwork(wifiConfiguration);
                 /*如果连上了指定的wifi，则检查是否能联网*/
                //Thread.sleep(3*1000);
                //logger.debug(String.valueOf(wifiUtil.getSSID().equalsIgnoreCase(ssidStr)));
                //logger.debug("连上的wifi是:" + wifiUtil.getSSID());
                app.setAuto(true);
                logger.debug("重新建立了wifi连接：app.setAuto()变成了" + app.isAuto());
               /* if (checkNetwork()) {
                    logger.debug("重新连接上"+W_SSID+"================");
                    createNotification("可以上网", R.drawable.logo);
                }*/

            } else {
                logger.debug("扫描wifi:在网络列表里没有指定的SSID");
                //createWifiSettingNotification(W_SSID + "不在范围内", R.drawable.portal_unconnect);
                //app = (ContextUtil) getApplication();
                //app.setAuto(false);
                logger.debug("找不到" + W_SSID);
            }

        }
    }


    /**
     * 自动登录portal
     */
    private synchronized void autoLoginPortal() {
       // NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //WifiInfo wifiInfo=wifiManager.getConnectionInfo();
        app = (ContextUtil) getApplication();
        //当连上了指定的ssid
        //       if (isSSID_connected()) {

        //           if (!checkNetwork()) {
        try {
            int count = 1;
            while (!checkNetwork() && count < 4) {
                //createNotification(W_SSID + "已连接,请稍等,正在登录portal", R.drawable.portal_unconnect);
                logger.debug("当前wifi不能联网");
                SharedPreferences sp = ContextUtil.getInstance().getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                //String username = sp.getString("USER_NAME", "");
                // pasword = sp.getString("PASSWORD", "");
                //logger.debug("记住的用户名======username:" + username + "\n记住的密码=======pasword:" + pasword);
                Bundle bundle = new Bundle();
                bundle.putString("username", sp.getString("uname", ""));
                bundle.putString("password", sp.getString("pwd", ""));
                bundle.putString("imsi", sp.getString("imsi", ""));
                bundle.putString("imei", sp.getString("imei", ""));
                bundle.putString("mac", sp.getString("mac", ""));
                NetUtil.LoginOfPost(bundle, action);
                //createNotification("不能上网,登录中", R.drawable.portal_unconnect);
                logger.debug("完成了第" + count + "次登录");
                count++;
                //Thread.sleep(10 * 1000);
            }
            //logger.debug("自动登陆portal第" + count + "次");
            if (count >= 4) {
                //NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "自动登录关闭,请手动登录", R.drawable.portal_unconnect, StartPage.class);
                app.setAuto(false);
            }
        } catch (Exception e) {
            logger.debug("自动登录portal:发送登录请求失败");
            logger.debug(e.getMessage());
            //NetUtil.createNotification(ContextUtil.getInstance(), notificationManager, "登录失败,不能上网", R.drawable.portal_unconnect, StartPage.class);
            //e.printStackTrace();
        }
        //}

           /* else {
                logger.debug("已登录,可以上网");
               *//* Intent intent = new Intent("com.jjlink.jieyun.loging");
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);*//*
                createNotification("已登录,可以上网", R.drawable.logo);
                return;
            }*/

        // }
    }

    /**
     * 判断连接的网络是否是移动网络
     *
     * @param context
     * @return
     */
    public boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo != null) {
                return networkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 创建常驻通知,提示用户portal登录状态
     *
     * @param
     * @param
     */
  /*  public synchronized void createNotification(String contextText, int icon) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        //notificationManager.cancel(66);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextUtil.getInstance());
        builder.setContentTitle("捷运上网助手");
        builder.setContentText(contextText);
        builder.setSmallIcon(icon);
        //Intent i=new Intent(BringToFrontReceiver.ACTION_BRING_TO_FRONT);
//        builder.setContentIntent(PendingIntent.getBroadcast(getApplicationContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT));
        ////////Intent intent=new Intent(this,this.getClass());
        ///////PendingIntent pendingIntent=PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        //////builder.setContentIntent(pendingIntent);
        //Intent intent=this.getPackageManager().getLaunchIntentForPackage(appPackageName);
        //PendingIntent pendingIntent=PendingIntent.getActivity(ContextUtil.getInstance(),0,intent,0);
        //builder.setOngoing(true);
        Intent i = new Intent(this, Welcome.class);
        if (i == null) {
            i = new Intent(this, LoginActivity.class);
        }
        //i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(66, notification);
    }

    private void createVoidNotifaction(String contextText, int icon) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ContextUtil.getInstance());
        builder.setContentTitle("捷运上网助手");
        builder.setContentText(contextText);
        builder.setSmallIcon(icon);
       *//* Intent i = new Intent(this, LoginActivity.class);
        //i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(intent);*//*
        Notification notification = builder.build();
        notification.flags = Notification.FLAG_ONGOING_EVENT;
        notificationManager.notify(66, notification);
    }*/

    private boolean checkNetwork() {
        return isWifiToInternet();
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

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        logger.debug("停止了服务:调用onDestroy()");
        timer.cancel();
        SharedPreferences sp=ContextUtil.getInstance().getSharedPreferences("NETSTAT",Context.MODE_WORLD_READABLE);
        app.setIsNetwork(false);
        sp.edit().putBoolean("NETWORKENABLE",false).commit();
        timerTask.cancel();
        //stopSelf(START_NOT_STICKY);
        super.onDestroy();
    }

    @Override
    public boolean stopService(Intent name) {
        logger.debug("停止了服务:调用stopService()");
        stopSelf(START_NOT_STICKY);
        timer.cancel();
        timerTask.cancel();
        return super.stopService(name);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
}
