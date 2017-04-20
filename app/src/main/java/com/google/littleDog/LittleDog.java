package com.google.littleDog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.utils.XmParms;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.adView.BannerAd;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

/**
 * Created by appchina on 2017/2/21.
 */

public class LittleDog implements AdListener{

    static final boolean ASK_SPLASH_AD = true; //  是否要有开屏广告
    static final boolean ASK_INTER_AD = true;   // 是否要有插屏广告
    private static final String TAG =  "xyz";
    static boolean ASK_BANNER_AD = true;  // banner 广告是不是已经显示了


    private static Context mContext;
    static InterstitialAd interstitialAd;

    public static void onCreate(Context context){
        mContext = context;


//        init(context);
        init_ad(context);

        if (XmParms.needBanner){
            showBanner((Activity) context);
        }

    }

    public static void init(Context context) {
    }


    private static long old_time;



    private static boolean vide0_first = true;
    private static boolean isBannerShowed = false;
    // 初始化广告

    static FrameLayout.LayoutParams layoutParams;
    static FrameLayout flayout;
    static FrameLayout.LayoutParams tvlayout;
    static BannerAd h5BannerAd;
    private static void bannerLayout(final Activity activity){
        flayout = new FrameLayout(activity);
        tvlayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);

        layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        activity.addContentView(flayout,layoutParams);
//        ViewGroup container = (ViewGroup) activity.findViewById(R.id.container);
        h5BannerAd = new BannerAd(activity.getApplicationContext(), flayout, new BannerAd.BannerListener() {


            @Override
            public void onAdEvent(AdEvent adEvent) {
                if (adEvent.mType == AdEvent.TYPE_CLICK) {
                    Log.d(TAG, "ad has been clicked!");
                } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                    Log.d(TAG, "x button has been clicked!");
                } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                    Log.d(TAG, "ad has been showed!");
                }
            }


        });
    }

    public static void showBanner(final Activity activity){
        h5BannerAd.show(XmParms.BANNER_ID);

    }



    public static void init_ad(Context context){


//        initBanner((Activity) context);
        bannerLayout((Activity) context);

        if (ASK_INTER_AD) {
            interstitialAd = new InterstitialAd(context.getApplicationContext(), (Activity) context);
            // 加载广告
            interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog());

        }


    }


    public static void onResume(final Context context){

        Log.d("LittleDog : ","onResume");
        MobclickAgent.onResume(context);
        // 加载广告


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("LittleDog : ","run");


                show_ad(context);



            }
        },5000);

    }

    public static void onPause(Context context){

        MobclickAgent.onPause(context);
    }


    private static boolean inter_isshowed = true;
    private static boolean inter_isshowed2 = true;

    public static void show_ad(Context context){
        if (interstitialAd.isReady()){
            interstitialAd.show();

        }
        interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog()) ;


    }


    @Override
    public void onAdError(AdError adError) {
        Log.e(TAG, "onAdError : " + adError.toString());
        MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_error);
        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_error);
    }
    @Override
    public void onAdEvent(AdEvent adEvent) {
        try {
            switch (adEvent.mType) {
                case AdEvent.TYPE_SKIP:
                    //用户关闭了广告
                    Log.e(TAG, "ad skip!");
                    MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_close);
                    XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_close);
                    break;
                case AdEvent.TYPE_CLICK:
                    //用户点击了广告
                    Log.e(TAG, "ad click!");
                    MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_click);
                    XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_click);
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onAdLoaded() {
        Log.e(TAG, "ad is loaded : ");
//        interstitialAd.show();
    }

    // 这里是真正显示广告的地方
    @Override
    public void onViewCreated(View view) {
        Log.e(TAG, "ad is ready : -Xmapi ");
        Log.e(TAG, "showInterstitalad inner-Xmapi");
        if(interstitialAd.isReady()){
//            if (!isJiShuOnResume){
//            }

            MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_show);
            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_show).append("\n");
        }else{
            MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_request);
            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_request);
        }
    }



}
