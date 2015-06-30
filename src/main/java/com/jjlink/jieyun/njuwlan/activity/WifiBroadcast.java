package com.jjlink.jieyun.njuwlan.activity;/*
package com.jjlink.jieyun;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.util.Log;

import com.jjlink.tools.ContextUtil;
import com.jjlink.tools.WifiUtil;

*/
/**
 * Created by zlu on 15-3-2.
 *//*

public class WifiBroadcast extends BroadcastReceiver {
    private String TAG="WIFI:";
    private static final String WIFI_NOTIFACTION = "捷运上网助手";
    private static final String WIFI_SATUS_CONTEXT ="WIFI状态改变";


    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intentSetting = new Intent(Settings.ACTION_WIFI_SETTINGS);
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        //Notification n = new Notification(R.drawable.wifi, "wifi状态", System.currentTimeMillis());
        Notification notification=new Notification(R.drawable.wifi,WIFI_NOTIFACTION,System.currentTimeMillis());
        notification.flags|=Notification.FLAG_NO_CLEAR;

        // Notification.Builder builder = new Notification.Builder(ContextUtil.getInstance());
        PendingIntent pi = PendingIntent.getActivity(ContextUtil.getInstance(), 0, intentSetting, 0);
        //builder.addAction(R.drawable.checkbox_unselect, " 点我打开WIFI设置", pi);

        Intent service=new Intent(context,AutoConnection.class);
        service.setAction(LoginActivity.AUTO_CONN_SERVICE);
        if(intent.getAction().equals(WifiManager.RSSI_CHANGED_ACTION)){

        }else if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
            Log.i(TAG,"网络状态改变");
           NetworkInfo info=intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
           if(info.getState().equals(NetworkInfo.State.DISCONNECTED)||info.getState().equals(NetworkInfo.State.DISCONNECTING)){
               //如果断开连接
               Log.i(TAG,"断开连接");
               context.startService(service);
               notification.setLatestEventInfo(ContextUtil.getInstance(),"手动连接WIFI","WIFI已断开",pi);
               notification.defaults = Notification.DEFAULT_SOUND;

               nm.notify(3, notification);

           }
           if(info.getState().equals(NetworkInfo.State.CONNECTING)){
               Log.i(TAG,"连接到Wifi");

           }
       }else if(intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
           //WIFI开关
           int wifistate=intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,WifiManager.WIFI_STATE_DISABLED);
           if(wifistate==WifiManager.WIFI_STATE_DISABLED){
               Log.i(TAG,"系统关闭wifi");
               context.startService(service);
              // notification.setLatestEventInfo(ContextUtil.getInstance(),"打开WIFI","WIFI已关闭",pi);
               //notification.defaults = Notification.DEFAULT_SOUND;
              // nm.notify(3, notification);
           }

           if(wifistate==WifiManager.WIFI_STATE_ENABLED){
               Log.i(TAG,"系统打开wifi");
           }
       }
    }
}
*/
