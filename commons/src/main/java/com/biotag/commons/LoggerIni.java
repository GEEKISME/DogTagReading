package com.biotag.commons;

import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.FormatStrategy;
import com.orhanobut.logger.Logger;
import com.orhanobut.logger.PrettyFormatStrategy;

public class LoggerIni {
    public static void init(){
        FormatStrategy formatStrategy = PrettyFormatStrategy.newBuilder()
                .showThreadInfo(true)  // 是否打印线程号,默认true
                .methodCount(5)         // 展示几个方法数,默认2
                .methodOffset(7)        // (Optional) Hides internal method calls up to offset. Default 5
                //.logStrategy(customLog) //是否更换打印输出,默认为logcat
                .tag("pretty")   // 全局的tag
                .build();
        Logger.addLogAdapter(new AndroidLogAdapter(formatStrategy));
    }
}
