package com.jjlink.jieyun.njuwlan.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by zlu on 15-3-9.
 */
public class CMDExecute {
    public synchronized String run(String[] cmd,String workdirectory) throws IOException{
        String result="";
        try{
            ProcessBuilder builder=new ProcessBuilder(cmd);
            //设置一个路径
            if(workdirectory!=null){
                builder.directory(new File(workdirectory));
                builder.redirectErrorStream(true);
                Process process=builder.start();
                InputStream in =process.getInputStream();
                byte[] re=new byte[1024];
                while(in.read(re)!=-1) {
                    result = result + new String(re);
                }
                if(in!=null)
                in.close();
            }

        }catch(Exception e){

        }
        return result;

    }
}
