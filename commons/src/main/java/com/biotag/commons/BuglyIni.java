package com.biotag.commons;

import android.content.Context;

import com.biotag.commons.utils.CommonUtils;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.crashreport.CrashReport;

// bugly 同时具有bug上传与版本升级功能
public class BuglyIni {
    public static void init(Context context){
        // 获取当前包名
        String packageName = context.getPackageName();
        // 获取当前进程名
        String processName = CommonUtils.getProcessName(android.os.Process.myPid());
        // 设置是否为上报进程
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(context);
        strategy.setUploadProcess(processName == null || processName.equals(packageName));
        // 初始化Bugly
//        CrashReport.initCrashReport(context, "5bf908d850", true, strategy);
        Bugly.init(context,"5bf908d850",true,strategy);
    }
}
