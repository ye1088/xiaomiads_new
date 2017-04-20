package com.google.littleDog;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Created by appchina on 2017/3/9.
 */

public class MulDexApp extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
