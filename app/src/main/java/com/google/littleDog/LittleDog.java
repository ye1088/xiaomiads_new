package com.google.littleDog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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

import com.google.utils.ButtonUtils;
import com.google.utils.MiUtils;
import com.google.utils.XmApi;
import com.google.utils.XmParms;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.AdListener;
import com.xiaomi.ad.SplashAdListener;
import com.xiaomi.ad.adView.BannerAd;
import com.xiaomi.ad.adView.InterstitialAd;
import com.xiaomi.ad.adView.SplashAd;
import com.xiaomi.ad.common.pojo.AdError;
import com.xiaomi.ad.common.pojo.AdEvent;

import java.io.File;
import java.io.IOException;

import static com.google.littleDog.SplashActivity.ADPID;

/**
 * Created by appchina on 2017/2/21.
 */

public class LittleDog implements AdListener{

    static final boolean ASK_SPLASH_AD = true; //  是否要有开屏广告
    static final boolean ASK_INTER_AD = true;   // 是否要有插屏广告
    private static final String TAG =  "xyz";
    private static final int SHOW_BANNER_VISIBLE =  0;
    private static final int SHOW_BANNER = 1;
    private static final int HINTSPLASH = 2;    // 显示隐藏性的开屏广告
    private static final int SHOW_POST_INTERSTITIAL = 3;
    private static final int EXC_METHOD = 4;
    private static final int REQ_INTER_AD = 5;
    private static final int HIDE_BANNER = 6;
    private static final int EXC_METHOD_ARG = 7;
    static boolean ASK_BANNER_AD = true;  // banner 广告是不是已经显示了
    static boolean  isFirstExc = true;  // 是否为第一次执行
    private static boolean isOnPause = false;
    private static boolean interstitialAdShowed = false;





    private static Context mContext;
    static InterstitialAd interstitialAd;


    static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
//            Message message = mHandler.obtainMessage();
//            if (flayout==null){
//                return;
//            }
            switch (msg.what){

                case EXC_METHOD_ARG:
                    try{
                        int what = msg.arg1;
                        if (isInterShowed){ // 如果 插屏广告被调用展示了,就通过判断
                            switch (what){
                                case 0:
                                    Log.e(TAG,"0000000000000000000000");
                                    break;
                                case 1:
                                    Log.e(TAG,"11111111111111111111111");
                                    break;
                                case 2:
                                    Log.e(TAG,"2222222222222222222222222");
                                    break;
                                case 3:
                                    Log.e(TAG,"33333333333333333333333333");
                                    break;
                            }
//                            XmParms.xInstance.add10Fish();
                            Toast.makeText(mContext, "be called", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, "广告加载失败,请10秒后再试....", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;

                case EXC_METHOD:
                    try{
//                        Toast.makeText(mContext, "out : " +isInterShowed, Toast.LENGTH_SHORT).show();
//                        Video_Ad.postShwoVideo();
                        if (isInterShowed){ // 如果 插屏广告被调用展示了,就通过判断
//                            XmParms.xInstance.add10Fish();
                            Toast.makeText(mContext, "be called", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, "广告加载失败,请10秒后再试....", Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    break;
                case SHOW_BANNER_VISIBLE:

//                    if (!getRootViewIsVisible()){
                    setVisibleBanner();
//                    }

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
                case REQ_INTER_AD:
                    initAndRequestInterAd();
                    break;
                case HIDE_BANNER:
                    hideBanner();
                    break;

            }
        }
    };
    private static boolean canShowBanner = false;

    public LittleDog(){
        super();
    }

    private LittleDog(Context mContext, InterstitialAd interstitialAd) {
        this.mContext = mContext;
        this.interstitialAd = interstitialAd;
    }

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
        isBannerShowed = false;
        isInterShowed = false;
        canShowBanner = false;
        init_ad(mContext);
//        Video_Ad.init(mContext);
        ButtonUtils.init(mContext);



        sendReceiverMsg("com.google.isOurGame" ,"这是我们的广告");

//        String errorMsg = MiUtils.backupSaveData(mContext);

//        init(context);

//        reportError(mContext,errorMsg);


    }

    /***
     * 向umeng报告错误
     * @param context
     * @param errorMsg : 错误信息
     */
    public static void reportError(Context context,String errorMsg){
        if (errorMsg.length()>1){
            MobclickAgent.reportError(mContext,errorMsg);
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

        //设置window type

//        params.token = activity.getWindow().getDecorView().getWindowToken();
//        params.type = WindowManager.LayoutParams.LAST_SUB_WINDOW;
        //设置图片格式，效果为背景透明
//        params.type = WindowManager.LayoutParams.TYPE_TOAST;
//        params.format = PixelFormat.RGBA_8888;
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;


        params.height =  ViewGroup.LayoutParams.WRAP_CONTENT; //MiUtils.dip2px(activity,50);

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
        ban_par.gravity = Gravity.CENTER_VERTICAL;
        FrameLayout.LayoutParams btn_par = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
//        btn_par.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);

        btn_par.gravity = Gravity.RIGHT;
        btn_par.width = MiUtils.dip2px(activity,25);
        btn_par.height = MiUtils.dip2px(activity,25);
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
                    MobclickAgent.onEvent(mContext,XmParms.umeng_event_banner_click);
                } else if (adEvent.mType == AdEvent.TYPE_SKIP) {
                    Log.d(TAG, "x button has been clicked!");
                } else if (adEvent.mType == AdEvent.TYPE_LOAD){
                    Log.d(TAG, "banner has load");
                }else if (adEvent.mType == AdEvent.TYPE_VIEW) {
                    Log.d(TAG, "ad has been showed!,这个是轮播事件");
                    isBannerShowed = true;

                    MobclickAgent.onEvent(mContext,XmParms.umeng_event_banner_show);
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
        h5BannerAd.show(XmParms.BANNER_ID);

        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.sendEmptyMessageDelayed(SHOW_BANNER_VISIBLE,180000);
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
        h5BannerAd.show(XmParms.BANNER_ID);
    }


    /**
     * 如果广告逻辑被执行了,就将标志信息写入sd卡中
     * @param whatAds
     */
    public static void writeFlag2Sdcard(String whatAds){
        try {
            new File("/sdcard/"+whatAds).createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void postShowBanner(){

        writeFlag2Sdcard("postShowBanner");

        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_BANNER_VISIBLE;
        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.sendMessage(msg);
        sendReceiverMsg("com.google.adCoverMsg","新型 banner 广告被执行了");
    }




    public static void postExcMethod_arg(int i){
        Message msg = mHandler.obtainMessage();
        msg.what = EXC_METHOD_ARG;
        msg.arg1 = i;
        mHandler.removeMessages(EXC_METHOD_ARG);
        mHandler.sendMessageDelayed(msg,100);
    }

    public static void postExcMethod(){
        Message msg = mHandler.obtainMessage();
        msg.what = EXC_METHOD;
        mHandler.removeMessages(EXC_METHOD);
        mHandler.sendMessageDelayed(msg,100);
    }

    public static void sendReceiverMsg(String intentMsg ,String msg){
        try {
            Intent intent = new Intent(intentMsg);
            intent.putExtra("msg",msg);
            mContext.sendBroadcast(intent);
        }catch (Exception e){
            e.printStackTrace();
        }

    }


    public static void hideBanner(){
        if (flayout==null){
            return;
        }
//        if (!XmParms.isADCover){
//            mHandler.sendEmptyMessageDelayed(SHOW_BANNER_VISIBLE,360000);
//        }
        controlCloseButton(false);
        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.sendEmptyMessageDelayed(SHOW_BANNER_VISIBLE,360000);
        flayout.setVisibility(View.INVISIBLE);
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
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                setVisibleBanner();
//            }
//        },15000);
    }
    // 设置 banner 广告显示
    public static void setVisibleBanner(){
//        flayout.setVisibility(View.VISIBLE);
        Log.d(TAG,"isInterShowed : "+isInterShowed+"  isBannerShowed : "+isBannerShowed);
        if (flayout==null|| isInterShowed ||!isBannerShowed){//
            Log.e(TAG,"set Visible banner failed ");
            hideBanner();
            canShowBanner = true;
            mHandler.removeMessages(SHOW_BANNER);
            mHandler.sendEmptyMessage(SHOW_BANNER);

            return;
        }

        Log.e(TAG,"show banner success");
        controlCloseButton(true);
        flayout.setVisibility(View.VISIBLE);
        // banner 广告自动关闭
        if (XmParms.isBannerAutoHide){
            hideBannerDelay30s();
        }
    }



    public static void init_ad(final Context context){


//        initBanner((Activity) context);
//        showSplash((Activity)context);
        XmApi.setOritation(((Activity)mContext).getRequestedOrientation());
        XmApi.onAppCreate(mContext);

        // 展示 ok dialog
        String dialogMsg = "test";
        MiUtils.showOkDialog((Activity) mContext,dialogMsg);


        if (XmParms.needBanner){
            bannerLayout((Activity) mContext);
            if (!XmParms.isADCover){// 如果不是替换广告则按原来的老计划搞广告
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showBanner((Activity) mContext);
                        mHandler.removeMessages(SHOW_BANNER);
                        mHandler.sendEmptyMessage(SHOW_BANNER);
                        setVisibleBanner();
                    }
                },18000);
                Message message = mHandler.obtainMessage();
                message.what = SHOW_BANNER_VISIBLE;
                // 延迟 6 分钟 再次 让banner 可见
                mHandler.removeMessages(SHOW_BANNER_VISIBLE);
                mHandler.sendMessageDelayed(message,360000);
            }

        }





    }



    public static void initAndRequestInterAd(){

        if (ASK_INTER_AD) {
            Log.e(TAG,"initAndRequestInterAd");


            if (isFirstExc){
                Log.e(TAG, "showInterstitalad---is firt run");
                isFirstExc = false;
                interstitialAd = new InterstitialAd(mContext,
                        ((Activity) mContext).getWindow().getDecorView());
                // 加载广告
                interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog(mContext,interstitialAd));
                mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
                mHandler.sendEmptyMessageDelayed(SHOW_POST_INTERSTITIAL,4000);
                return;

            }

            if (interstitialAd.isReady()) {
                Log.e(TAG, "showInterstitalad---is ready");
                if (!MiUtils.hasAccessTime()){
                    Log.e(TAG, "showInterstitalad---is not time");
                    return;
                }
                MobclickAgent.onEvent(mContext,XmParms.umeng_event_inter_show);
                isInterShowed = true;
                controlCloseButton(false);
                hideBanner();
                Log.e(TAG, "showInterstitalad---showed");
                interstitialAd.show();
                interstitialAd = new InterstitialAd(mContext,
                        ((Activity) mContext).getWindow().getDecorView());
                interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog(mContext,interstitialAd));

                return;
            }else {
                Log.e(TAG, "showInterstitalad---is not ready");
                interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog(mContext,interstitialAd));
                mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
                mHandler.sendEmptyMessageDelayed(SHOW_POST_INTERSTITIAL,3000);
                return;
            }




        }
    }


    public static void onResume(final Context context){


        isOnPause = false;
//        ButtonUtils.selDialog();
        // 刷开屏广告展示量
//        requestSplashAd();
//        mHandler.sendEmptyMessage(HINTSPLASH);
        Log.d("LittleDog : ","onResume");
        MobclickAgent.onResume(mContext);
//        MobclickAgent.onResume(context);

//        LittleDog.setVisibleBanner();






        if (isFirstExc){

            mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
            mHandler.sendEmptyMessage(SHOW_POST_INTERSTITIAL);

            Log.e(TAG,"sendEmptyMessageDelayed REQ_INTER_AD");

            return;
        }

        if (XmParms.isADCover){
            return;
        }



        mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
        mHandler.sendEmptyMessage(SHOW_POST_INTERSTITIAL);





    }

    public static void onPause(Context context){

        isOnPause = true;
        MobclickAgent.onPause(mContext);
//        MobclickAgent.onPause(mContext);
//        mHandler.removeMessages(SHOW_BANNER_VISIBLE);
        mHandler.removeMessages(HINTSPLASH);
    }


    private static boolean inter_isshowed = true;
    private static boolean inter_isshowed2 = true;

//    public static void show_ad(Context context){
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
//        interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog()) ;
//
//
//    }

    public static void show_ad(){



        mHandler.removeMessages(REQ_INTER_AD);
        mHandler.sendEmptyMessage(REQ_INTER_AD);


//
//        if (interstitialAd.isReady()){
//            mHandler.removeMessages(SHOW_BANNER_VISIBLE);
////            if (!isFirstExc){
////                hideBanner();
////            }else {
////                isFirstExc = false;
////            }
//
//            hideBanner();
////            if (isFirstExc){
////                isFirstExc = false;
////            }
//
//            Log.e(TAG,"show ad ");
//
//            interstitialAd.show();
//            isInterShowed  = true;
//        }



    }


    public static void postShowInterstitial(){
        Message msg = mHandler.obtainMessage();
        msg.what = SHOW_POST_INTERSTITIAL;
        writeFlag2Sdcard("postShowInterstitial");
        mHandler.removeMessages(SHOW_POST_INTERSTITIAL);
        mHandler.sendMessage(msg);
        sendReceiverMsg("com.google.adCoverMsg","新型 插屏 广告被执行了");
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
                    // 插屏广告关闭15秒后,显示横幅广告
                    if (!XmParms.isADCover){
                        setVisibleBannerDelay15s();
                    }

                    Log.e(TAG, "ad skip!");
                    MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_close);
                    XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_close);
                    break;
                case AdEvent.TYPE_CLICK:
                    //用户点击了广告

                    isInterShowed = false;
                    // 15s 后 显示广告
                    // 插屏广告关闭15秒后,显示横幅广告
                    if (!XmParms.isADCover){
                        setVisibleBannerDelay15s();
                    }
                    Log.e(TAG, "inter ad click!");
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
        Log.e(TAG, "inter ad is loaded : ");
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

//            MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_show);
//            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_show).append("\n");
        }else{
//            MobclickAgent.onEvent(mContext, XmParms.umeng_event_inter_request);
//            XmParms.sBuilder.append("\n").append(XmParms.umeng_event_inter_request);
        }
    }




    // 请求开屏广告
    private static void requestSplashAd(){
//        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        splashAd.requestAd(XmParms.POSITION_ID_SPLASH);
//        ((Activity)mContext).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    /**
     * 开屏广告
     * @param context
     */
    private static SplashAd splashAd;
    public static void showSplash(Activity context){


        Log.e(ADPID,"ASK_SPLASH_AD");



        FrameLayout flayout = new FrameLayout(mContext);
        flayout.setVisibility(View.GONE);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        context.addContentView(flayout,layoutParams);
//        WindowManager windowManager = (WindowManager) context
//                .getSystemService(context.WINDOW_SERVICE);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        windowManager.addView(flayout,layoutParams);
        String imgname = "default_splash_";
        int imgid = mContext.getResources().getIdentifier(imgname, "drawable", mContext.getPackageName());

//        flayout.setVisibility(View.GONE);

        splashAd = new SplashAd(mContext, flayout, imgid, new SplashAdListener() {
            @Override
            public void onAdPresent() {
                // 开屏广告展示
                Log.e(TAG, "onAdPresent");

            }

            @Override
            public void onAdClick() {
                // 如果开屏广告被点击了，就向sp中写入 splashAdNeedHintShowCount
//                flayout.setVisibility(View.GONE);
//                handler.sendEmptyMessage(SHOWHINTSPLASH);
                //用户点击了开屏广告
                Log.e(TAG, "onAdClick");

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
                Log.e(TAG, "onAdDismissed");


            }
            @Override
            public void onAdFailed(String s) {

                Log.e(TAG, "onAdFailed, message: " + s);
                //这个方法被调用时，表示从服务器端请求开屏广告时，出现错误。
            }
        });


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
