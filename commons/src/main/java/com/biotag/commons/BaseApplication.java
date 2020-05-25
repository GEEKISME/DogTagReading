package com.biotag.commons;

import android.app.Application;

import com.biotag.commons.utils.CrashHandler;

import androidx.multidex.MultiDex;


public class BaseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        ObjectBoxIni.init(this);
        LoggerIni.init();
        BuglyIni.init(getApplicationContext());
        //全局异常捕获
        CrashHandler crashHandler = CrashHandler.getsInstance();
        crashHandler.init(this);
    }
}
