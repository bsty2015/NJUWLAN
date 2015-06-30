package com.jjlink.jieyun.njuwlan.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.jjlink.jieyun.njuwlan.activity.FloatWindowService;
import com.jjlink.jieyun.njuwlan.activity.StartPage;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zlu on 5/25/15.
 */
public class DaemonService extends Service {

    public static final String TAG="DaemonService";
    private Thread thread;
    private Timer timer;
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(thread!=null){
            return START_STICKY;
        }
        thread=new Thread(new Runnable() {
            @Override
            public void run() {
                timer=new Timer();
                TimerTask task=new TimerTask() {
                    @Override
                    public void run() {
                        Log.e(TAG, "DaemonService Run: " + System.currentTimeMillis());
                        boolean checkNet= StartPage.isServiceWorked(getApplicationContext(), "com.jjlink.service.CheckNetService");
                        boolean floatWin=StartPage.isServiceWorked(DaemonService.this,"com.jjlink.jieyun.FloatWindowService");
                        if(!checkNet){
                            Intent service=new Intent(DaemonService.this,CheckNetService.class);
                            startService(service);
                            //Log.e(TAG,"Start CheckNetService");
                        }

                        if(!floatWin){
                            Intent service= new Intent(getApplicationContext(), FloatWindowService.class);
                            startService(service);
                            //Log.e(TAG,"Start floatWindowService");
                        }

                    }
                };
                timer.schedule(task,0,5000);
            }
        });
        thread.setDaemon(true);
        thread.start();
        return START_STICKY;
    }



    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(thread!=null){
            if(timer!=null){
                timer.cancel();
                timer=null;
            }
            thread.interrupt();
            thread=null;
        }
    }
}
