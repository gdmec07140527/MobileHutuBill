package com.example.lzk.mobilehutubill;

import android.app.Application;

/**
 * Created by Lzk on 2017/12/30.
 */

public class CrashApplication extends Application {

    private static CrashApplication sInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        //在这里为应用设置异常处理程序，然后我们的程序才能捕获未处理的异常
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }

    public static CrashApplication getInstance() {
        return sInstance;
    }

}