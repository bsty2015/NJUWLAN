package com.jjlink.jieyun.njuwlan.log;

import android.nfc.Tag;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zlu on 15-4-14.
 */
public class JieyunLog {
    private static final String TAG="JieYun";
    public void debug(String message){
        /*SimpleDateFormat tempDate=new SimpleDateFormat("yyyy-MM-dd");
        String dateTimeToday=tempDate.format(new Date());
        String filePath="/mnt/sdcard/jieyun_log_"+dateTimeToday+".txt";
        File f=new File(filePath);
        SimpleDateFormat sf=new SimpleDateFormat("HH:mm:ss");
        try {
            if(message!=null&&!message.isEmpty()){
                String now=sf.format(new Date());
                FileWriter fw=new FileWriter(f,true);
                fw.write(now+"  "+message+"\n");*/
                Log.d(TAG,message);
     /*           fw.flush();
                fw.close();
            }
       } catch (IOException e) {
            //e.printStackTrace();
            Log.d("日志读写出现异常:",e.getMessage());
            JieyunLog logger=new JieyunLog();
            logger.debug("日志读写异常:"+e.getMessage());
        }*/
    }

    public void sinfo(String message){
        Log.i("登录信息:",message);
    }
}
