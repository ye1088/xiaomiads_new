package com.google.littleDog;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.utils.XmApi;
import com.google.utils.XmParms;
import com.miui.zeus.mimo.sdk.MimoSdk;

/**
 * Created by appchina on 2017/3/9.
 */

public class MulDexApp extends Application {

    private static final String APP_KEY = "fake_app_key";
    private static final String APP_TOKEN = "fake_app_token";
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
//        MimoSdk.setDebugOn();
//         正式上线时候务必关闭stage
//        MimoSdk.setStageOn();


    }

    @Override
    public void onCreate() {
        super.onCreate();
        XmApi.onAppCreate(this);
        MimoSdk.init(this, XmParms.APP_ID, APP_KEY, APP_TOKEN);

    }
}
