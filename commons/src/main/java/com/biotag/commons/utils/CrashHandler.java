package com.biotag.commons.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.os.SystemClock;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CrashHandler implements Thread.UncaughtExceptionHandler {

    //文件夹目录
    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/crash_log/";
    //文件名
    private static final String FILE_NAME = "crash";
    //文件名后缀
//    private static final String FILE_NAME_SUFFIX = ".trace";
    private static final String FILE_NAME_SUFFIX = ".txt";
    //上下文
    private Context mContext;

    private static volatile  CrashHandler sInstance = null;
    private CrashHandler(){}

    public static CrashHandler getsInstance() {
        if(sInstance == null){
            synchronized (CrashHandler.class){
                if(sInstance == null){
                    sInstance = new CrashHandler();
                }
            }
        }
        return sInstance;
    }

    public  void init(Context context){
        //将当前实例设为系统默认的异常处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        //异常输出到SD
        exportExceptionToSDCard(e);
//        //异常上传服务器
//        uploadExceptionToServer(e);
        //延时一秒杀死进程

        SystemClock.sleep(1000);
        restartApplication();
        Process.killProcess(Process.myPid());
    }

    private void restartApplication() {
        Intent intent = mContext.getPackageManager().getLaunchIntentForPackage( mContext.getPackageName() );
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mContext.startActivity(intent);

    }

    private void uploadExceptionToServer(Throwable e) {
        String buginfo = getStackTrace(e);
        Log.i("tms",buginfo);
//        OkhttpPlusUtil.getInstance().Post(Constants.UPLOAD_BUGSINFO,buginfo);
        StringBuilder sb = new StringBuilder();
        String jsonbuginfo  =  sb.append("{").append("\"msg\":").append(buginfo).append("}").toString();
//        SharedPreferencesUtils.saveString(mContext,"buginfo",jsonbuginfo);
    }

    private void exportExceptionToSDCard(Throwable ex) {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            return;
        }
        //创建文件夹
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        //获取当前时间
        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        //以当前时间创建log文件
        File file = new File(PATH + FILE_NAME + time + FILE_NAME_SUFFIX);
        try {
            //输出流操作
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            //导出手机信息和异常信息
            PackageManager pm = mContext.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
            pw.println("发生异常时间：" + time);
            pw.println("应用版本：" + pi.versionName);
            pw.println("应用版本号：" + pi.versionCode);
            pw.println("android版本号：" + Build.VERSION.RELEASE);
            pw.println("android版本号API：" + Build.VERSION.SDK_INT);
            pw.println("手机制造商:" + Build.MANUFACTURER);
            pw.println("手机型号："  + Build.MODEL);
            ex.printStackTrace(pw);
            //关闭输出流
            pw.close();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public String getStackTrace(Throwable throwable){
        StringWriter stringWriter=new StringWriter();
        PrintWriter printWriter=new PrintWriter(stringWriter);

        try {
            throwable.printStackTrace(printWriter);
            return stringWriter.toString();
        }finally {
            printWriter.close();
        }

    }



}
