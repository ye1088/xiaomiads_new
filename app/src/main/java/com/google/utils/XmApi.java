package com.google.utils;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDex;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.umeng.analytics.MobclickAgent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * Created by appchina on 2016/10/27.
 */

public class XmApi  {
    public static String TAG = "wsyzg";




    public static boolean isFirstStart = false;

    public static boolean isPortrait = true;

    public static boolean isJiShuOnResume = true;//設置flag,让插屏广告第一次启动时不显示

    public static boolean isInterstitialAdShowed = false;


    public static void onAppCreate(Context context){
        loadProperities(context);
        if(XmParms.appCreate){
            return;
        }
        XmParms.appCreate = true;




        Log.e("xyz", XmParms.APP_ID+"\n"+ XmParms.POSITION_ID+"\n"+ XmParms.POSITION_ID_SPLASH+"\n"+
                XmParms.UMENG_KEY+"\n");

        // 刚开始 就显示一次 插屏

//        AdSdk.initialize(context.getApplicationContext(), XmParms.APP_ID);
        MobclickAgent.UMAnalyticsConfig umconfig = new MobclickAgent.UMAnalyticsConfig(context,
                XmParms.UMENG_KEY, XmParms.UMENG_CHANNEL);
        MobclickAgent.startWithConfigure(umconfig);
    }


    public static void setOritation(int orientation){
        //判断是不是 竖屏

        if (orientation != 1){
            isPortrait = false;
        }
        Log.e("position_id_splash_h", String.valueOf(orientation));
    }


    public static void onAppAttachBaseContext(Context context){
        XmParms.appAttach = true;
        MultiDex.install(context);
    }

    // 设置 isPortrait 是横屏还是竖屏


    private static void loadProperities(Context context){
        try {
            Properties pro = new Properties();
            pro.load(context.getAssets().open("pro.properties"));
            XmParms.APP_ID = pro.getProperty("app_id", XmParms.APP_ID).trim();
            XmParms.BANNER_ID = pro.getProperty("banner_id", XmParms.BANNER_ID).trim();
            // 是否需要banner 广告
            if (!"0".equals(pro.getProperty("needBanner","1"))) XmParms.needBanner = true;
            // banner 广告显示的位置
            if ("0".equals(pro.getProperty("isBannerTop","1"))) XmParms.isBannerTop = false;
            if ("0".equals(pro.getProperty("isBannerCanClose","1"))) XmParms.isBannerCanClose = false;
            if ("0".equals(pro.getProperty("isBannerAutoHide","1"))) XmParms.isBannerAutoHide = false;
            if ("1".equals(pro.getProperty("isADCover","0"))) XmParms.isADCover = true;
            if ("1".equals(pro.getProperty("isHengPin","0"))) XmParms.isHengPin = true;

            if (!XmParms.isHengPin){
                //竖屏开屏广告id      竖屏广告
                XmParms.POSITION_ID_SPLASH = pro.getProperty("position_id_splash_h", XmParms.POSITION_ID_SPLASH).trim();
                XmParms.POSITION_ID = pro.getProperty("position_id", XmParms.POSITION_ID).trim();

            }else {
                //横屏 开屏广告id  横屏广告
                XmParms.POSITION_ID_SPLASH = pro.getProperty("position_id_splash", XmParms.POSITION_ID_SPLASH).trim();
                XmParms.POSITION_ID = pro.getProperty("position_id_h", XmParms.POSITION_ID).trim();
            }
//            Log.e("position_id_splash_h",XmParms.POSITION_ID_SPLASH+"  "+ isPortrait);

            XmParms.pkgname = pro.getProperty("pkgname", XmParms.pkgname).trim();
            XmParms.launcher = pro.getProperty("launcher", XmParms.launcher).trim();
            XmParms.UMENG_CHANNEL = context.getPackageName();
            XmParms.UMENG_KEY = pro.getProperty("umeng_key", XmParms.UMENG_KEY).trim();
//            XmParms.filelen = Long.parseLong(pro.getProperty("filelen"));
//            XmParms.obbname = pro.getProperty("obbname");
        }catch (IOException e){
            e.printStackTrace();
        }

    }


//    private InterstitialAd mInterstitialAd = null;
//    public XmApi(Activity activity, InterstitialAd interstitialAd){
//        mInterstitialAd = interstitialAd;
//        mActivity = activity;
//    }
    public static long timestmp = -1;
    public static long timeinterval = 1000*10;
//    static InterstitialAd interstitialAd;

    private static Activity mActivity ;

    public static void showInterstitialAd(Activity activity){

//        if (!isInterstitialAdShowed){
//            // 插屏广告初始化
//            isInterstitialAdShowed = true;
//            interstitialAd = new InterstitialAd(activity.getApplicationContext(), activity);
//            interstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(activity,interstitialAd));
//        }
//
//        Log.e(TAG, "showInterstitalad");
//        Log.e(TAG, "XmParms.POSITION_ID" + XmParms.POSITION_ID);
//        if(interstitialAd.isReady()){
//            Log.e(TAG, "showInterstitalad---showed");
//
////            interstitialAd.show();
//            // 跳过广告后 重新申请广告
//            interstitialAd = new InterstitialAd(activity.getApplicationContext(), activity);
//            interstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(activity,interstitialAd));
//
//
////            isInterstitialAdShowed = false;
//
//            MobclickAgent.onEvent(activity, XmParms.umeng_event_inter_show);
//            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_show);
//        }else{
//            MobclickAgent.onEvent(activity, XmParms.umeng_event_inter_request);
//            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_request);
//            interstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(activity,interstitialAd));
//        }
    }


//    @Override
//    public void onAdError(AdError adError) {
//        Log.e(TAG, "onAdError : " + adError.toString());
//        MobclickAgent.onEvent(mActivity, XmParms.umeng_event_inter_error);
//        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_error);
//    }
//    @Override
//    public void onAdEvent(AdEvent adEvent) {
//        try {
//            switch (adEvent.mType) {
//                case AdEvent.TYPE_SKIP:
//                    //用户关闭了广告
//                    Log.e(TAG, "ad skip!");
//                    MobclickAgent.onEvent(mActivity, XmParms.umeng_event_inter_close);
//                    XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_close);
//                    interstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(mActivity,interstitialAd));
//                    break;
//                case AdEvent.TYPE_CLICK:
//                    //用户点击了广告
//                    Log.e(TAG, "ad click!");
//                    MobclickAgent.onEvent(mActivity, XmParms.umeng_event_inter_click);
//                    XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_click);
//                    isJiShuOnResume = true;
//                    break;
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//    @Override
//    public void onAdLoaded() {
//        Log.e(TAG, "ad is loaded : ");
////        interstitialAd.show();
//    }
//
//    // 这里是真正显示广告的地方
//    @Override
//    public void onViewCreated(View view) {
//        Log.e(TAG, "ad is ready : -Xmapi ");
//        Log.e(TAG, "showInterstitalad inner-Xmapi");
//        mInterstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(mActivity,mInterstitialAd));
//        if(mInterstitialAd.isReady()){
//            Log.e(TAG, "showInterstitalad show 2-Xmapi"+"----isJiShuOnResume "+isJiShuOnResume);
////            if (!isJiShuOnResume){
//                mInterstitialAd.show();
////            }
//            isJiShuOnResume = false;
//
//            MobclickAgent.onEvent(mActivity, XmParms.umeng_event_inter_show);
//            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_show).append("\n");
//        }else{
//            MobclickAgent.onEvent(mActivity, XmParms.umeng_event_inter_request);
//            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_request);
//            mInterstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(mActivity,mInterstitialAd));
//        }
//    }


    public static void onLauncherCreate(Activity activity){
        XmParms.launchCreate = true;



    }

    public static void first_show(Activity activity){
        if (!isFirstStart){
            isFirstStart = true;
//            showInterstitialAd(activity);
//            if (interstitialAd.isReady()){
//                interstitialAd.show();
//            }else {
//                interstitialAd.requestAd(XmParms.POSITION_ID, new XmApi(activity,interstitialAd));
//
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        if (interstitialAd.isReady()){
//                            interstitialAd.show();
//                        }
//                    }
//                },5000);
//            }

        }
    }
    public static void onLauncherStart(Activity activity){
        XmParms.launchStart = true;
    }

    public static void onLauncherResume( Activity activity){

        XmParms.launchResume = true;
//        showBanner(activity);
        // 解决频繁展示广告
//        if(System.currentTimeMillis() - timestmp > timeinterval) {
            showInterstitialAd(activity);
            timestmp = System.currentTimeMillis();
//        }
        MobclickAgent.onResume(activity);



        first_show(activity);
    }

    public static void showBanner(final Activity activity){

        // 初始化广告
        TextView tv = new TextView(activity);
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setText("正在加载.........");
        final FrameLayout flayout = new FrameLayout(activity);
        final FrameLayout.LayoutParams tvlayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);

        tv.setLayoutParams(tvlayout);
        flayout.addView(tv,tvlayout);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        activity.addContentView(flayout,layoutParams);

    }

    public static void onLauncherPause(Activity activity){
        XmParms.launchPause = true;
        MobclickAgent.onPause(activity);
        write2File(activity);
    }

    public static void onLauncherStop(Activity activity){
        XmParms.launchStop = true;
        write2File(activity);
    }

    public static void onLauncherDestroy(Activity activity){
        XmParms.launchDestroy = true;
        write2File(activity);
    }

    private static String buildLogStr(String pkgname){
        StringBuilder sbuilder = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sbuilder.append(df.format(new Date())).append("\n");
        if(XmParms.appCreate && XmParms.appAttach &&
                XmParms.launchResume && XmParms.launchPause){
            sbuilder.append("callback ok").append("\n");
        }else{
            sbuilder.append("callback failed").append("\n");
        }
        sbuilder.append("pkgname : ").append(pkgname).append("\n").
                append("appid : ").append(XmParms.APP_ID).append("\n").
                append("position_id : ").append(XmParms.POSITION_ID).append("\n").
                append("splash_id : ").append(XmParms.POSITION_ID_SPLASH).append("\n").
                append("umeng_key : ").append(XmParms.UMENG_KEY).append("\n").
                append("umeng_channel : ").append(XmParms.UMENG_CHANNEL).append("\n").
                append("debug : ").append(XmParms.isdebug).append("\n");
        sbuilder.append(XmParms.sBuilder.append("\n"));
        return sbuilder.toString();
    }
    private static boolean needWriteLog(Context context){
        String logwatcherpkg = "demo.ad.xiaomi.com.logwatcher";
        PackageManager pm = context.getPackageManager();
        boolean needLog =false;
            try{
                pm.getPackageInfo(logwatcherpkg, PackageManager.GET_ACTIVITIES);
                needLog =true;
            }catch(PackageManager.NameNotFoundException e){
                needLog =false;
            }
        return needLog;
    }
    private static void write2File(Context context){
        if(needWriteLog(context)) {
            String pkgname = context.getPackageName();
            String log = buildLogStr(pkgname);
            String logPath = "/sdcard/Android/data/pkgname/xmparams.log".replace("pkgname", pkgname).replace("/", File.separator);
            File logFile = new File(logPath);
            File logDir = logFile.getParentFile();
            if(!logDir.exists()){
                logDir.mkdirs();
            }
            if(logFile.exists()){
                logFile.delete();
            }
            try {
                OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(logFile),"utf-8");
                osw.write(log);
                osw.flush();
                osw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("writelog", "write log success");
        }
    }
}
