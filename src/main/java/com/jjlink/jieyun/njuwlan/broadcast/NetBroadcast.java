package com.jjlink.jieyun.njuwlan.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.jjlink.jieyun.njuwlan.activity.FloatWindowService;
import com.jjlink.jieyun.njuwlan.activity.StartPage;
import com.jjlink.jieyun.njuwlan.service.CheckNetService;
import com.jjlink.jieyun.njuwlan.service.DaemonService;


/**
 * Created by zlu on 5/13/15.
 */
public class NetBroadcast extends BroadcastReceiver implements View.OnClickListener{
    public static final String CHECK_NET_SERVICE = "com.jjlink.service.checkNetService";
    public static final String FLOAT_WINDOW_SERVICE = "com.jjlink.jieyun.floatWindowService";
    public static final String DAEMON_SERVICE="com.jjlink.service.DaemonService";

    @Override
    public void onClick(View v) {

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        boolean checkNet= StartPage.isServiceWorked(context, "com.jjlink.service.CheckNetService");
        if(!checkNet){
            Intent checkNetwork = new Intent(context, CheckNetService.class);
            checkNetwork.setAction(CHECK_NET_SERVICE);
            context.startService(checkNetwork);
            //Log.d("广播","启动了检查网络的服务");
        }
        boolean floatWin=StartPage.isServiceWorked(context, "com.jjlink.jieyun.FloatWindowService");
        if(!floatWin) {
            Intent floatWindowService = new Intent(context, FloatWindowService.class);
            floatWindowService.setAction(FLOAT_WINDOW_SERVICE);
            context.startService(floatWindowService);
            //Log.d("广播","启动了悬浮窗的服务");
        }
        boolean isDaemon= StartPage.isServiceWorked(context,"com.jjlink.service.DaemonService");
        if(!isDaemon){
            Intent service=new Intent(context, DaemonService.class);
            service.setAction(DAEMON_SERVICE);
            context.startService(service);
        }
    }
}
