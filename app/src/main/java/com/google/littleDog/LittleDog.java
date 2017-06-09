package com.google.littleDog;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.utils.XmParms;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.adView.BannerAd;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;
import com.xiaomi.ad.internal.b.a;

import java.io.IOException;

/**
 * Created by appchina on 2017/2/21.
 */

public class LittleDog implements AdListener{

    static final boolean ASK_SPLASH_AD = true; //  是否要有开屏广告
    static final boolean ASK_INTER_AD = true;   // 是否要有插屏广告
    private static final String TAG =  "xyz";
    private static final int SHOW_BANNER =  0;
    static boolean ASK_BANNER_AD = true;  // banner 广告是不是已经显示了
    static boolean  isFirstExc = true;  // 是否为第一次执行




    private static Context mContext;
    static InterstitialAd interstitialAd;

    static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_BANNER:
                    setVisibleBanner();
                    Message message = mHandler.obtainMessage();
                    message.what = SHOW_BANNER;
                    mHandler.sendMessageDelayed(message,360000);
                    break;

            }
        }
    };

    public static void onCreate(Context context){
        mContext = context;


//        init(context);
        init_ad(context);

    }

    public static void init(Context context) {
    }


    private static long old_time;



    private static boolean vide0_first = true;
    private static boolean isBannerShowed = false;
    // 初始化广告

//    static LinearLayout.LayoutParams layoutParams;
//    static FrameLayout flayout;
//    static FrameLayout btn_frameLayout;

    //    static FrameLayout.LayoutParams tvlayout;
    static BannerAd h5BannerAd;
    static FrameLayout flayout;
    private static void bannerLayout(final Activity activity){

        flayout = new FrameLayout(activity);

        flayout.removeAllViews();
//        flayout.setAlpha((float) 0.5);
//        flayout.setHeight(-1);
//        flayout.setWidth(-1);
//        flayout.c(false);

//        tvlayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        // 设置 layout 的 大小各种样式
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        /*****************************************************************************************/

        // 设置banner 广告的样式

        WindowManager windowManager = (WindowManager) activity.getApplicationContext()
                .getSystemService(activity.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 手机屏幕的 宽 高
        int phone_width = windowManager.getDefaultDisplay().getWidth();
        int phone_heigh = windowManager.getDefaultDisplay().getHeight();
        // 宽高比例
        double scal_x_y = 0;
        if (phone_heigh<phone_width){
            scal_x_y = phone_heigh*1.0/phone_width;
            params.width = (int) (phone_heigh * 0.9);
        }else {
            scal_x_y =phone_width*1.0/ phone_heigh;
            params.width = (int) (phone_width * 0.9);
        }
        //  强制设置为前台显示
        params.type =WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;


        params.height = (int) (188 * scal_x_y);

        /*************************************************************************************************/




        // 设置 banner 显示的宽度
//        layoutParams.width = (int) (phone_width * 0.9);
//        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
//        flayout.setOrientation(LinearLayout.HORIZONTAL);
//        flayout.setLayoutParams(layoutParams);

        // 等下还原
        ImageView button = new ImageView(activity);

        if (XmParms.isBannerCanClose){// XmParms.isBannerCanClose
            try {
                // 设置图片
                button.setImageBitmap(BitmapFactory.decodeStream(activity.getAssets().open("my_cancel.png")));

            } catch (IOException e) {
                e.printStackTrace();
            }

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hideBanner();
//                    Toast.makeText(activity, "dianjile", Toast.LENGTH_SHORT).show();
                }
            });
        }


//        button.setGravity(Gravity.CENTER_VERTICAL);
        // 设置 layout 的 大小各种样式 对子view进行处理（其实就是banner广告）
        FrameLayout.LayoutParams ban_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        ban_par.weight = 15;
//        ban_par.height = (int) (188 * scal_x_y);
        ban_par.gravity = Gravity.CENTER_VERTICAL;
        FrameLayout.LayoutParams btn_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        btn_par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        btn_par.gravity = Gravity.RIGHT;;
        btn_par.width = (int) (100*scal_x_y);
        btn_par.height = (int) (100*scal_x_y);
        Log.e("LittleDog : ","width : "+btn_par.width+" height : "+ btn_par.height);
//        btn_par.width = 100;
//        btn_par.height = 100;

//        ban_par.gravity = Gravity.BOTTOM;
        FrameLayout ban_frameLayout = new FrameLayout(activity);
        ban_frameLayout.setLayoutParams(ban_par);
//        btn_frameLayout = new FrameLayout(activity);

        button.setLayoutParams(btn_par);
//        flayout.
//        flayout.

        flayout.addView(ban_frameLayout);
        flayout.addView(button);
//
        if (!XmParms.isBannerTop){
            // 把广告放到底部
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        }
//        activity.getWindow().getDecorView().setTop();//.getWindow()
//        activity.getWindow().addContentView(flayout, params);
        windowManager.addView(flayout, params);
//        ViewGroup container = (ViewGroup) activity.findViewById(R.id.container);
        h5BannerAd = new BannerAd(activity.getApplicationContext(), ban_frameLayout, new BannerAd.BannerListener() {


            @Override
            public void onAdEvent(AdEvent adEvent) {
                if (adEvent.mType == AdEvent.TYPE_CLICK) {
                    Log.d(TAG, "ad has been clicked!");
                } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                    Log.d(TAG, "x button has been clicked!");
                } else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                    Log.d(TAG, "ad has been showed!");
                    isBannerShowed = true;

                }
            }


        });
    }

    public static void showBanner(final Activity activity){
        h5BannerAd.show(XmParms.BANNER_ID);
    }


    public static void hideBanner(){
        if (flayout==null){
            return;
        }
        flayout.setVisibility(View.INVISIBLE);
    }
    public static void hideBannerDelay30s(){
        if (flayout==null){
            return;
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                while (!isBannerShowed);
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        flayout.setVisibility(View.INVISIBLE);
                    }
                },30000);
            }
        }.start();


    }
    // 设置 banner 广告显示
    public static void setVisibleBanner(){
        if (flayout==null){
            return;
        }
        flayout.setVisibility(View.VISIBLE);
        // banner 广告自动关闭
        if (XmParms.isBannerAutoHide){
            hideBannerDelay30s();
        }
    }



    public static void init_ad(Context context){


//        initBanner((Activity) context);

        if (XmParms.needBanner){
            bannerLayout((Activity) context);
            showBanner((Activity) context);
            Message message = mHandler.obtainMessage();
            message.what = SHOW_BANNER;
            mHandler.sendMessageDelayed(message,360000);
        }

        if (ASK_INTER_AD) {
            interstitialAd = new InterstitialAd(context.getApplicationContext(), (Activity) context);
            // 加载广告
            interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog());

        }


    }


    public static void onResume(final Context context){




        Log.d("LittleDog : ","onResume");
        MobclickAgent.onResume(context);

        LittleDog.setVisibleBanner();
        // banner 广告自动关闭
        if (XmParms.isBannerAutoHide){
            hideBannerDelay30s();
        }

        // 加载广告
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("LittleDog : ","run");
                show_ad(context);


            }
        },6000);

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
