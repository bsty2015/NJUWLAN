package com.jjlink.jieyun.njuwlan.log;

import android.app.Activity;
import android.os.Environment;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;

import de.mindpipe.android.logging.log4j.LogConfigurator;

/**
 * Created by zlu on 15-4-13.
 */
public class AndroidLog{
    public static Logger configLog(Class clazz){
        Logger gLogger;
        final LogConfigurator logConfigurator=new LogConfigurator();
        String logFileName=Environment.getExternalStorageState() + File.separator + "jieyun.log";
        File logFile=new File(logFileName);
        logConfigurator.setFileName(logFileName);
        // Set the root log level
        logConfigurator.setRootLevel(Level.DEBUG);
        // Set log level of a specific logger
        logConfigurator.setLevel("org.apache", Level.ERROR);
        logConfigurator.configure();
        gLogger=Logger.getLogger(clazz);
        return gLogger;
    }
}
