package com.google.littleDog;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.google.utils.SUtils;
import com.google.utils.XmApi;
import com.google.utils.XmParms;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.adView.BannerAd;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.io.IOException;

/**
 * Created by appchina on 2017/2/21.
 */

public class LittleDog implements AdListener{

    static final boolean ASK_SPLASH_AD = true; //  是否要有开屏广告
    static final boolean ASK_INTER_AD = true;   // 是否要有插屏广告
    private static final String TAG =  "xyz";
    private static final int SHOW_BANNER_VISIBLE =  0;
    static boolean ASK_BANNER_AD = true;  // banner 广告是不是已经显示了
    static boolean  isFirstExc = true;  // 是否为第一次执行




    private static Context mContext;
    static InterstitialAd interstitialAd;

    private static final int SHOW_BANNER = 1;
    static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Message message = mHandler.obtainMessage();
            if (flayout==null){
                return;
            }
            switch (msg.what){
                case SHOW_BANNER_VISIBLE:

                    if (!getRootViewIsVisible()){
                        setVisibleBanner();
                    }

                    break;
                case SHOW_BANNER:
                    if (!getRootViewIsVisible()){
                        controlCloseButton(false);
                        showBanner((Activity) mContext);
                    }
                    message.what = SHOW_BANNER;
                    if(canShowBanner){
                        mHandler.sendMessageDelayed(message,3000);
                    }

                    break;

            }
        }
    };
    private static boolean canShowBanner = false;

    public static boolean getRootViewIsVisible(){
        if (flayout == null){
            return false;
        }
        if (flayout.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

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
    private static boolean isInterShowed = false;
    // 初始化广告

//    static LinearLayout.LayoutParams layoutParams;
//    static FrameLayout flayout;
//    static FrameLayout btn_frameLayout;

    //    static FrameLayout.LayoutParams tvlayout;
    static BannerAd h5BannerAd;
    static FrameLayout flayout;
    static ImageView button;
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

        WindowManager windowManager = (WindowManager) activity
                .getSystemService(activity.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();

        // 手机屏幕的 宽 高
//        int phone_width = windowManager.getDefaultDisplay().getWidth();
//        int phone_heigh = windowManager.getDefaultDisplay().getHeight();
        // 宽高比例
        double scal_x_y = 0;
//        if (phone_heigh<phone_width){
//            scal_x_y = phone_heigh*1.0/phone_width;
//            params.width = (int) (phone_heigh * 0.9);
//        }else {
//            scal_x_y =phone_width*1.0/ phone_heigh;
//            params.width = (int) (phone_width * 0.9);
//        }
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        //设置window type

//        params.token = activity.getWindow().getDecorView().getWindowToken();
//        params.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
        //设置图片格式，效果为背景透明
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
//        params.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;


        params.height =  ViewGroup.LayoutParams.WRAP_CONTENT; //SUtils.dip2px(activity,50);

        /*************************************************************************************************/




        // 设置 banner 显示的宽度
//        layoutParams.width = (int) (phone_width * 0.9);
//        layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
//        flayout.setOrientation(LinearLayout.HORIZONTAL);
//        flayout.setLayoutParams(layoutParams);

        // 等下还原

        button = new ImageView(activity);

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
        FrameLayout.LayoutParams ban_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        ban_par.weight = 15;
//        ban_par.height = (int) (188 * scal_x_y);
        ban_par.gravity = Gravity.CENTER_VERTICAL;
        FrameLayout.LayoutParams btn_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        btn_par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        btn_par.gravity = Gravity.RIGHT;;
        btn_par.width = SUtils.dip2px(activity,25);
        btn_par.height = SUtils.dip2px(activity,25);
        Log.e("LittleDog : ","width : "+btn_par.width+" height : "+ btn_par.height);
//        btn_par.width = 100;
//        btn_par.height = 100;

//        ban_par.gravity = Gravity.BOTTOM;
        FrameLayout ban_frameLayout = new FrameLayout(activity);
        ban_frameLayout.setLayoutParams(ban_par);
//        btn_frameLayout = new FrameLayout(activity);
        button.setLayoutParams(btn_par);
        // 刚开始关闭按钮 banner广告加载成功后才 显示
        controlCloseButton(false);

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
        hideBanner();
//        ViewGroup container = (ViewGroup) activity.findViewById(R.id.container);
        h5BannerAd = new BannerAd(activity, ban_frameLayout, new BannerAd.BannerListener() {


            @Override
            public void onAdEvent(AdEvent adEvent) {
                Log.d(TAG, "onAdEvent : "+ adEvent);
                if (adEvent.mType == AdEvent.TYPE_CLICK) {
                    Log.d(TAG, "ad has been clicked!");
                    hideBanner();
                } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                    Log.d(TAG, "x button has been clicked!");
                } else if (adEvent.mType == AdEvent.TYPE_LOAD){

                }else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                    Log.d(TAG, "ad has been showed!,这个是轮播事件");
                    isBannerShowed = true;
                    // banner广告加载成功后才 显示关闭按钮
                    controlCloseButton(true);
                    if (canShowBanner){
                        canShowBanner = false;
                        setVisibleBanner();
                    }

                }else if (adEvent.mType == AdEvent.TYPE_INTERRUPT){
                    Log.d(TAG, "AdEvent.TYPE_INTERRUPT : "+AdEvent.TYPE_INTERRUPT);
                }else if (adEvent.mType == AdEvent.TYPE_LOAD_FAIL){
                    Log.d(TAG, "AdEvent.TYPE_LOAD_FAIL : "+AdEvent.TYPE_LOAD_FAIL);
                }else if (adEvent.mType == AdEvent.TYPE_APP_LAUNCH_FAIL){
                    Log.d(TAG, "AdEvent.TYPE_APP_LAUNCH_FAIL : "+AdEvent.TYPE_APP_LAUNCH_FAIL);
                }else {
                    Log.d(TAG, "unknow : "+adEvent.mType);
                }
            }


        });
    }
    public static void controlCloseButton(boolean close){
        if (button != null){
            if (close){
                button.setVisibility(ImageView.VISIBLE);
            }else {
                button.setVisibility(ImageView.INVISIBLE);
            }

        }
    }

    public static void showBanner(final Activity activity){
        isBannerShowed = false;
        h5BannerAd.show(XmParms.BANNER_ID);
    }


    public static void hideBanner(){
        if (flayout==null){
            return;
        }
        mHandler.sendEmptyMessageDelayed(SHOW_BANNER_VISIBLE,360000);
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
                        isBannerShowed = false;
                        flayout.setVisibility(View.INVISIBLE);
                    }
                },30000);
            }
        }.start();


    }

    // 15s 后 显示广告
    public static  void setVisibleBannerDelay15s(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibleBanner();
            }
        },15000);
    }
    // 设置 banner 广告显示
    public static void setVisibleBanner(){
        Log.d(TAG,"isInterShowed : "+isInterShowed+"  isBannerShowed : "+isBannerShowed);
        if (flayout==null|| isInterShowed ||!isBannerShowed){//
            canShowBanner = true;
            mHandler.sendEmptyMessage(SHOW_BANNER);
            return;
        }


        flayout.setVisibility(View.VISIBLE);
        // banner 广告自动关闭
        if (XmParms.isBannerAutoHide){
            hideBannerDelay30s();
        }
    }



    public static void init_ad(final Context context){


//        initBanner((Activity) context);
        XmApi.setOritation(((Activity)context).getRequestedOrientation());
        XmApi.onAppCreate(context);

        if (XmParms.needBanner){
            bannerLayout((Activity) context);
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showBanner((Activity) context);
                    mHandler.sendEmptyMessage(SHOW_BANNER);
                    setVisibleBanner();
                }
            },18000);
            Message message = mHandler.obtainMessage();
            message.what = SHOW_BANNER_VISIBLE;
            // 延迟 6 分钟 再次 让banner 可见
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

//        LittleDog.setVisibleBanner();


        // 加载广告
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("LittleDog : ","run");

                if (!isInterShowed){
                    show_ad(context);
                }



            }
        },3000);

    }

    public static void onPause(Context context){

        MobclickAgent.onPause(context);
    }


    private static boolean inter_isshowed = true;
    private static boolean inter_isshowed2 = true;

    public static void show_ad(Context context){
        if (interstitialAd.isReady()){

            if (!isFirstExc){
                mHandler.removeMessages(SHOW_BANNER_VISIBLE);
                hideBanner();
            }else {
                isFirstExc = false;
            }

            interstitialAd.show();
            isInterShowed  = true;
        }
        interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog()) ;


    }


    @Override
    public void onAdError(AdError adError) {
        Log.e(TAG, "onAdError : " + adError.toString());
        isInterShowed = false;
        MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_error);
        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_error);
    }
    @Override
    public void onAdEvent(AdEvent adEvent) {
        try {
            switch (adEvent.mType) {
                case AdEvent.TYPE_SKIP:
                    //用户关闭了广告
                    isInterShowed = false;
                    setVisibleBannerDelay15s();
                    Log.e(TAG, "ad skip!");
                    MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_close);
                    XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_close);
                    break;
                case AdEvent.TYPE_CLICK:
                    //用户点击了广告

                    isInterShowed = false;
                    // 15s 后 显示广告
                    setVisibleBannerDelay15s();
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
