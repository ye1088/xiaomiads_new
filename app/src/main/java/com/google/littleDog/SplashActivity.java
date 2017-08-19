package com.google.littleDog;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.utils.SUtils;
import com.google.utils.XmApi;
import com.google.utils.XmParms;
import com.google.xiaomiads_new.MainActivity;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.SplashAdListener;
import com.xiaomi.ad.adView.SplashAd;

/**
 * Created by appchina on 2017/3/7.
 */

public class SplashActivity extends Activity {

    static final String ADPID = "1705100002";
    private static final boolean ASK_BANNER_AD = true;
    static final String UMENG_KEY = "58be889dae1bf87353001091";
    private static boolean isAdClick = false;   // 广告是不是被点击了
    private static boolean isAdSkip = true; // 是否点广告跳过
    private static final String TAG = "SplashActivity";
    private static final boolean ISDEBUG = false;
    private static boolean has_permission = false;
    private boolean dataIsCopy = false;
    private boolean splashIsShow = false;


    static Handler handler;
    private boolean isIntented = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (SUtils.isGrantExternalRW(this)){
            has_permission = true;
            try {

                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
            showSplash(this);
        }




    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grantResult = grantResults[i];

                if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    if (grantResult == PackageManager.PERMISSION_GRANTED) {
                        //授权成功后的逻辑
                        has_permission = true;
                        try {

                            init();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showSplash(this);


                    } else {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    }
                }
            }
        }
    }

    private void init() throws Exception {

        /**
         * 友盟 初始化
         * cGold : 是渠道号
         * 584912f375ca3528ff00056d : 是友盟 key
         */
        XmApi.setOritation(getRequestedOrientation());
        XmApi.onAppCreate(this);
        if (ASK_BANNER_AD){
//            initBanner(this);
        }




        if (SUtils.isFirstRun(this)||SUtils.isNewObbVersion(this)){

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        SUtils.copy_data(SplashActivity.this);
                        Message msg = handler.obtainMessage();
                        dataIsCopy = true;
                        msg.what = 4;
                        handler.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                        dataIsCopy = true;
                        handler.sendEmptyMessage(5);
                    }
                }
            }.start();
        }else {
            dataIsCopy = true;
        }

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {

                if (msg.what != 4){
                    splashIsShow = true;
                }
                switch (msg.what){
                    case 0:
                        gotoNextActivity("onSplashAdFailed");
                        break;
                    case 1:
                        gotoNextActivity("onSplashAdDismiss");
                        break;
                    case 2:
                        gotoNextActivity("isAdSkip ads");
                        break;
                    case 3:
                        gotoNextActivity("onResume");
                        break;
                    case 4:
                        gotoNextActivity("copyData");
                        break;
                    case 5:
                        gotoNextActivity("Copy Error");
                        break;
                    default:
                        splashIsShow = true;
                        gotoNextActivity("default");
                        break;
                }


            }
        };

    }



    /**
     * 开屏广告
     * @param context
     */
    public void showSplash(final Context context){
            Log.e(ADPID,"ASK_SPLASH_AD");
        // 隐藏标题栏

        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            FrameLayout flayout = new FrameLayout(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((Activity)context).addContentView(flayout,layoutParams);
        String imgname = "default_splash_";
        int imgid = getResources().getIdentifier(imgname, "drawable", getPackageName());

        SplashAd splashAd = new SplashAd(this, flayout, imgid, new SplashAdListener() {
            @Override
            public void onAdPresent() {
                // 开屏广告展示

                Log.e(TAG, "onAdPresent");
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_show);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_show);

            }

            @Override
            public void onAdClick() {
                //用户点击了开屏广告
                Log.e(TAG, "onAdClick");
                isAdClick = true;
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_click);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_click);
//                handler.postDelayed(new Runnable() {
//                        @Override
//                        public void run() {
//                            gotoNextActivity("adclick");
//                        }
//                    }, 5000);
            }

            @Override
            public void onAdDismissed() {
                //这个方法被调用时，表示从开屏广告消失。
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_close);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_close);
                Log.e(TAG, "onAdDismissed");
                if (!isAdClick){
                    handler.sendEmptyMessage(1);
                }

            }
            @Override
            public void onAdFailed(String s) {

                Log.e(TAG, "onAdFailed, message: " + s);
                //这个方法被调用时，表示从服务器端请求开屏广告时，出现错误。
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_error);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_error);
                handler.sendEmptyMessage(1);
            }
        });

        MobclickAgent.onEvent(this, XmParms.umeng_event_splash_request);
        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_request);
        splashAd.requestAd(XmParms.POSITION_ID_SPLASH);
        // 如果开屏广告 点跳过 则 执行这个方法
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isAdSkip){
//                    handler.sendEmptyMessage(2);
//                }
//            }
//        },10000);


        }

    @Override
    protected void onResume() {
        super.onResume();
        if (has_permission){
            MobclickAgent.onResume(this);

            if (splashIsShow){
                handler.sendEmptyMessage(1);
            }

            splashIsShow = true;
        }

//        if (isAdClick){
//            handler.sendEmptyMessageDelayed(3,1000);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (has_permission){
            MobclickAgent.onPause(this);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private  void gotoNextActivity(String msg) {
        showLog(msg);
        if (!isIntented&&dataIsCopy&&splashIsShow){
            isIntented = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }



    public static void showLog(String msg){
        if (ISDEBUG){
            Log.e(TAG,msg);
        }
    }



}
