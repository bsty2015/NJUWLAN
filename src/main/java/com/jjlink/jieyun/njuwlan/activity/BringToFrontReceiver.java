package com.jjlink.jieyun.njuwlan.activity;/*
package com.jjlink.jieyun;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.List;

*/
/**
 * Created by zlu on 15-4-1.
 *//*

public class BringToFrontReceiver extends BroadcastReceiver {
    public static final String ACTION_BRING_TO_FRONT="com.jjlink.jieyun.BringToFront";
    public BringToFrontReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ActivityManager mAm= (ActivityManager) context.getSystemService(context.ACTIVITY_SERVICE);
        //获得当前运行的task
        List<ActivityManager.RunningTaskInfo> taskList=mAm.getRunningTasks(100);
        for(ActivityManager.RunningTaskInfo rti:taskList){
            //找到当前应用的task，并启动task的栈顶activity，达到程序切换到前台
            if(rti.topActivity.getPackageName().equals(context.getPackageName())){
                mAm.moveTaskToFront(rti.id,0);
                return;
            }
            //若没有找到运行的task，用户结束了task或被系统释放，则重新启动Loginctivity
            Intent resultIntent=new Intent(context,LoginActivity.class);
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);
            context.startActivity(resultIntent);
        }
    }
}
*/
