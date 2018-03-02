package com.google.littleDog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.widget.Toast;

import com.google.utils.MiUtils;
import com.google.utils.XmApi;
import com.google.utils.XmParms;
import com.miui.zeus.mimo.sdk.ad.AdWorkerFactory;
import com.miui.zeus.mimo.sdk.ad.IAdWorker;
import com.miui.zeus.mimo.sdk.listener.MimoAdListener;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.common.pojo.AdType;

import java.io.IOException;

/**
 * Created by appchina on 2017/2/21.
 */

public class LittleDog implements MimoAdListener {

    static final boolean ASK_SPLASH_AD = true; //  是否要有开屏广告
    static final boolean ASK_INTER_AD = true;   // 是否要有插屏广告
    private static final String TAG =  "xyz";
    private static final int SHOW_BANNER_VISIBLE =  0;
    private static final int SHOW_BANNER = 1;
    private static final int HINTSPLASH = 2;    // 显示隐藏性的开屏广告
    private static final int SHOW_POST_INTERSTITIAL = 3;
    private static final int EXC_METHOD = 4;
    private static final int HIDE_BANNER = 5;
    static boolean ASK_BANNER_AD = true;  // banner 广告是不是已经显示了
    static boolean  isFirstExc = true;  // 是否为第一次执行
    private static boolean isOnPause = false;


    private static FrameLayout ban_frameLayout;

    private static Context mContext;
    static IAdWorker interstitialAd;


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
                    Log.e(TAG,"handler   :  SHOW_BANNER_VISIBLE");
//                    if (!getRootViewIsVisible()){
//                        setVisibleBanner();
//                    }
                    setVisibleBanner();

                    break;
                case SHOW_BANNER:
//                    if (!getRootViewIsVisible()){
                    controlCloseButton(false);
                    showBanner((Activity) mContext);
                    Log.d(TAG,"SHOW_BANNER : handler");
//                    }
//                    message.what = SHOW_BANNER;
//                    if(canShowBanner&&!isOnPause){
//                        mHandler.removeMessages(SHOW_BANNER);
//                        mHandler.sendMessageDelayed(message,3000);
//                    }

                    break;
                case HINTSPLASH:
//                    requestSplashAd();
                    mHandler.removeMessages(HINTSPLASH);
                    mHandler.sendEmptyMessageDelayed(HINTSPLASH,60000);
                    break;
                case SHOW_POST_INTERSTITIAL:
                    show_ad();
                    break;
                case HIDE_BANNER:
                    hideBanner();
                    break;
                case EXC_METHOD:
                    try{
//                        Toast.makeText(mContext, "out : " +isInterShowed, Toast.LENGTH_SHORT).show();

                        if (isInterShowed){
                            Toast.makeText(mContext, "be called", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, "广告加载失败,请10秒后再试....", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;

            }
        }
    };
    private static boolean canShowBanner = false;

    public static boolean getRootViewIsVisible(){
        if (flayout == null){
            Log.e(TAG,"flayout is null ");
            return false;
        }
        if (flayout.getVisibility() == View.VISIBLE){
            return true;
        }
        return false;
    }

    public static void onCreate(Context context){
        mContext = context;
//        MimoSdk.setDebugOn();
//        // 正式上线时候务必关闭stage
//        MimoSdk.setStageOn();

//        String errorMsg = MiUtils.backupSaveData(context);

//        init(context);
        init_ad(context);
//        reportError(context,errorMsg);


    }



    public static void postExcMethod(){
        Message msg = mHandler.obtainMessage();
        msg.what = EXC_METHOD;
        mHandler.removeMessages(EXC_METHOD);
        mHandler.sendMessageDelayed(msg,100);
    }

    /***
     * 向umeng报告错误
     * @param context
     * @param errorMsg : 错误信息
     */
    public static void reportError(Context context,String errorMsg){
        if (errorMsg.length()>1){
            MobclickAgent.reportError(context,errorMsg);
        }
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
    static IAdWorker h5BannerAd;
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
        int phone_width = windowManager.getDefaultDisplay().getWidth();
        int phone_heigh = windowManager.getDefaultDisplay().getHeight();
        // 宽高比例
        double scal_x_y = 0;
        if (phone_heigh<phone_width){
            scal_x_y = phone_heigh*1.0/phone_width;
            params.width = (int) (phone_heigh * 0.8);
        }else {
            scal_x_y =phone_width*1.0/ phone_heigh;
            params.width = (int) (phone_width * 0.8);
        }

        //设置window type

//        params.token = activity.getWindow().getDecorView().getWindowToken();
//        params.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
        //设置图片格式，效果为背景透明
        params.type = WindowManager.LayoutParams.TYPE_TOAST;
        params.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
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
        FrameLayout.LayoutParams ban_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
//        ban_par.weight = 15;
//        ban_par.height = (int) (188 * scal_x_y);
        ban_par.gravity = Gravity.CENTER_HORIZONTAL|Gravity.TOP;
        ban_par.width = params.width;
        FrameLayout.LayoutParams btn_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        btn_par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        btn_par.gravity = Gravity.RIGHT;;
        btn_par.width = MiUtils.dip2px(activity,25);
        btn_par.height = MiUtils.dip2px(activity,25);
        Log.e("LittleDog : ","width : "+btn_par.width+" height : "+ btn_par.height);
//        btn_par.width = 100;
//        btn_par.height = 100;

//        ban_par.gravity = Gravity.BOTTOM;
        ban_frameLayout = new FrameLayout(activity);
        ban_frameLayout.setLayoutParams(ban_par);
//        btn_frameLayout = new FrameLayout(activity);
        button.setLayoutParams(btn_par);
        // 刚开始关闭按钮 banner广告加载成功后才 显示
        controlCloseButton(false);

//        flayout.addView(ban_frameLayout);
        flayout.addView(button);


//
        if (!XmParms.isBannerTop){
            // 把广告放到底部
            params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            ban_par.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
        }
//        activity.getWindow().getWindowManager().addView(ban_frameLayout, params);//.getWindow()
        activity.getWindow().addContentView(ban_frameLayout, ban_par);
//        windowManager.addView(ban_frameLayout, params);
//        hideBanner();
//        ViewGroup container = (ViewGroup) activity.findViewById(R.id.activity_main);
        try {
            h5BannerAd = AdWorkerFactory.getAdWorker(activity,
                    ban_frameLayout, new MimoAdListener() {
                        @Override
                        public void onAdPresent() {
                            Log.d(TAG, "ad has been showed!,这个是轮播事件");
                            isBannerShowed = true;
                            // banner广告加载成功后才 显示关闭按钮
//                            controlCloseButton(true);
                            MobclickAgent.onEvent(mContext,XmParms.umeng_event_banner_show);
                            if (canShowBanner){
                                canShowBanner = false;
                                setVisibleBanner();

                            }

                        }

                        @Override
                        public void onAdClick() {
                            Log.d(TAG, "ad has been clicked!");
                            hideBanner();
                            MobclickAgent.onEvent(mContext,XmParms.umeng_event_banner_click);
                        }

                        @Override
                        public void onAdDismissed() {
                            Log.d(TAG, "banner onAdDismissed : ");
                            hideBanner();
                        }

                        @Override
                        public void onAdFailed(String s) {
                            Log.d(TAG, "banner failed : "+s);
                        }

                        @Override
                        public void onAdLoaded() {
//                            canShowBanner = true;
                            isBannerShowed = true;
                            Log.d(TAG, "banner onAdLoaded : ");
                        }
                    },
                    AdType.AD_BANNER);
            h5BannerAd.loadAndShow(XmParms.BANNER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "banner error  : ");
        }


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
        Log.d(TAG,"XmParms.BANNER_ID : "+XmParms.BANNER_ID);
        try {
            // 这个setvisibility  到时候要注释掉,
            canShowBanner = true;

//            flayout.setVisibility(View.VISIBLE);
            h5BannerAd.loadAndShow(XmParms.BANNER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            bannerLayout(activity);
        }
    }


    public static void hideBanner(){

        if (flayout==null){
            return;
        }
        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.sendEmptyMessageDelayed(SHOW_BANNER_VISIBLE,360000);
        flayout.setVisibility(View.INVISIBLE);
        ban_frameLayout.setVisibility(View.INVISIBLE);
    }
    public static void hideBannerDelay30s(){
        if (flayout==null){
            return;
        }
        mHandler.removeMessages(HIDE_BANNER);
        mHandler.sendEmptyMessageDelayed(HIDE_BANNER,30000);


    }

    // 15s 后 显示广告
    public static  void setVisibleBannerDelay15s(){
        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.sendEmptyMessageDelayed(SHOW_BANNER_VISIBLE,15000);
    }
    // 设置 banner 广告显示
    public static void setVisibleBanner(){
//        flayout.setVisibility(View.VISIBLE);
        ban_frameLayout.setVisibility(View.VISIBLE);
        Log.d(TAG,"isInterShowed : "+isInterShowed+"  isBannerShowed : "+isBannerShowed);
        if (flayout==null|| isInterShowed ||!isBannerShowed){//
            canShowBanner = true;
            hideBanner();
            mHandler.removeMessages(SHOW_BANNER);
            mHandler.sendEmptyMessage(SHOW_BANNER);

            return;
        }

//        h5BannerAd.loadAndShow(XmParms.BANNER_ID);
//        flayout.setVisibility(View.VISIBLE);
        try {
            h5BannerAd.loadAndShow(XmParms.BANNER_ID);
        } catch (Exception e) {
            e.printStackTrace();
            bannerLayout((Activity) mContext);
        }
        // banner 广告自动关闭
        if (XmParms.isBannerAutoHide){
            hideBannerDelay30s();
        }
    }

    public static void init_inter_ad(){
        try {
            interstitialAd = AdWorkerFactory.getAdWorker(mContext,
                    (ViewGroup) ((Activity) mContext).getWindow().getDecorView(), new LittleDog(),
                    AdType.AD_INTERSTITIAL);
            interstitialAd.load(XmParms.POSITION_ID) ;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void init_ad(final Context context){

        XmApi.onAppCreate(context);
//        initBanner((Activity) context);
//        showSplash((Activity)context);

        XmApi.setOritation(((Activity)context).getRequestedOrientation());


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
            init_inter_ad();


            // 加载广告
            try {
                interstitialAd.load(XmParms.POSITION_ID);
            } catch (Exception e) {
                init_inter_ad();
                e.printStackTrace();
            }

        }



    }

    public static void sendReceiverMsg(String intentMsg ,String msg){
        Intent intent = new Intent(intentMsg);
        intent.putExtra("msg",msg);
        mContext.sendBroadcast(intent);
    }

    public static void postShowBanner(){
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_BANNER_VISIBLE;
        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.sendMessage(msg);
        sendReceiverMsg("com.google.adCoverMsg","新型 banner 广告被执行了");
    }


    public static void postShowInterstitial(){
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_POST_INTERSTITIAL;

        mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
        mHandler.sendMessage(msg);
        sendReceiverMsg("com.google.adCoverMsg","新型 插屏 广告被执行了");
    }


    public static void onResume(final Context context){

//        try {
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        isOnPause = false;
//        requestSplashAd();
//        mHandler.sendEmptyMessage(HINTSPLASH);
        Log.d("LittleDog : ","onResume");
        MobclickAgent.onResume(context);

//        LittleDog.setVisibleBanner();




        if (XmParms.isADCover ){
            return;
        }
        mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
        mHandler.sendEmptyMessageDelayed(SHOW_POST_INTERSTITIAL,3000);

        // 加载广告
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Log.d("LittleDog : ","run");
//
//                if (!isInterShowed){
//                    try {
//                        show_ad();
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }
//                }
//
//
//
//            }
//        },3000);

    }

    public static void onPause(Context context){

        isOnPause = true;
        MobclickAgent.onPause(context);
//        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.removeMessages(HINTSPLASH);
    }


    private static boolean inter_isshowed = true;
    private static boolean inter_isshowed2 = true;



    public static void show_ad(){
        if (!MiUtils.hasAccessTime()){
            return;
        }


        hideBanner();
        try {
            if (interstitialAd.isReady()){
                mHandler.removeMessages(SHOW_BANNER_VISIBLE);


                if (isFirstExc){
                    isFirstExc = false;
                }

                interstitialAd.show();
                MobclickAgent.onEvent(mContext,XmParms.umeng_event_inter_show);
                isInterShowed  = true;

                Log.e(TAG,"interstitialAd has show and loading ad  ");

            }
            interstitialAd.load(XmParms.POSITION_ID) ;



//            init_inter_ad();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,"interstitial exception ");
            init_inter_ad();

        }



    }

//    public static void show_ad(Context context) throws Exception {
//
//
//
//        if (interstitialAd.isReady()){
//            mHandler.removeMessages(SHOW_BANNER_VISIBLE);
//            if (!isFirstExc){
//                hideBanner();
//            }else {
//                isFirstExc = false;
//            }
//
//            interstitialAd.show();
//            isInterShowed  = true;
//        }
//        interstitialAd.load(XmParms.POSITION_ID) ;
//
//
//    }




    @Override
    public void onAdPresent() {

    }

    @Override
    public void onAdClick() {
        //用户点击了广告

        isInterShowed = false;
        // 15s 后 显示广告
        setVisibleBannerDelay15s();
        Log.e(TAG, "ad click!");
        MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_click);
        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_click);
    }

    @Override
    public void onAdDismissed() {
        isInterShowed = false;
        setVisibleBannerDelay15s();
        Log.e(TAG, "onAdDismissed!");

        MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_close);
        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_close);
        try {
            interstitialAd.load(XmParms.POSITION_ID);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAdFailed(String s) {
        Log.e(TAG, "onAdError : " + s);
        isInterShowed = false;

    }

    @Override
    public void onAdLoaded() {
        Log.e(TAG, "ad is loaded : ");
//        interstitialAd.show();
    }





    // 请求开屏广告
    private static void requestSplashAd(){
//        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        splashAd.requestAd(XmParms.POSITION_ID_SPLASH);
//        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 开屏广告
     * @param context
     */
//    private static SplashAd splashAd;
    public static void showSplash(Activity context){
//        Log.e(ADPID,"ASK_SPLASH_AD");
//
//
//
//        FrameLayout flayout = new FrameLayout(context);
//        flayout.setVisibility(View.GONE);
//        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
//                ViewGroup.LayoutParams.MATCH_PARENT);
//        context.addContentView(flayout,layoutParams);
////        WindowManager windowManager = (WindowManager) context
////                .getSystemService(context.WINDOW_SERVICE);
////        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
////        windowManager.addView(flayout,layoutParams);
//        String imgname = "default_splash_";
//        int imgid = context.getResources().getIdentifier(imgname, "drawable", context.getPackageName());
//
////        flayout.setVisibility(View.GONE);
//
//        splashAd = new SplashAd(context, flayout, imgid, new SplashAdListener() {
//            @Override
//            public void onAdPresent() {
//                // 开屏广告展示
//                Log.e(TAG, "onAdPresent");
//
//            }
//
//            @Override
//            public void onAdClick() {
//                // 如果开屏广告被点击了，就向sp中写入 splashAdNeedHintShowCount
////                flayout.setVisibility(View.GONE);
////                handler.sendEmptyMessage(SHOWHINTSPLASH);
//                //用户点击了开屏广告
//                Log.e(TAG, "onAdClick");
//
////                handler.postDelayed(new Runnable() {
////                        @Override
////                        public void run() {
////                            gotoNextActivity("adclick");
////                        }
////                    }, 5000);
//            }
//
//            @Override
//            public void onAdDismissed() {
//                //这个方法被调用时，表示从开屏广告消失。
//                Log.e(TAG, "onAdDismissed");
//
//
//            }
//            @Override
//            public void onAdFailed(String s) {
//
//                Log.e(TAG, "onAdFailed, message: " + s);
//                //这个方法被调用时，表示从服务器端请求开屏广告时，出现错误。
//            }
//        });


        // 如果开屏广告 点跳过 则 执行这个方法
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                if (isAdSkip){
//                    handler.sendEmptyMessage(2);
//                }
//            }
//        },10000);

//        splashAd.requestAd(XmParms.POSITION_ID_SPLASH);
    }



}
