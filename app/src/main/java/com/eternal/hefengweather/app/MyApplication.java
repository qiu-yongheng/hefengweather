package com.eternal.hefengweather.app;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * @author 邱永恒
 * @time 2017/4/8 12:32
 * @desc ${TODO}
 */

public class MyApplication extends Application{

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        LitePalApplication.initialize(context);
    }
    public static Context getContext() {
        return context;
    }
}
