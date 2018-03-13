package com.google.littleDog;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.google.utils.MiUtils;
import com.google.utils.XmApi;
import com.google.utils.XmParms;
import com.google.xiaomiads_new.MainActivity;
import com.umeng.analytics.MobclickAgent;
import com.xiaomi.ad.SplashAdListener;
import com.xiaomi.ad.adView.SplashAd;

import java.util.ArrayList;

/**
 * Created by appchina on 2017/3/7.
 */

public class SplashActivity extends Activity   {

    static final String ADPID = "1705100002";
    private static final boolean ASK_BANNER_AD = true;
//    static final String UMENG_KEY = "58be889dae1bf87353001091";
    private static final int SHOWHINTSPLASH = 6;
    private static final int ADCLICK = 7;
    private static final int SHOWPROGRESS = 8;
    private static boolean isAdClick = false;   // 广告是不是被点击了
    private static boolean isAdSkip = true; // 是否点广告跳过
    private static final String TAG = "SplashActivity_xyz";
    private static final boolean ISDEBUG = false;
    private static boolean has_permission = false;
    private boolean dataIsCopy = false;
    private boolean splashIsShow = false;
    private int splashAdNeedHintShowCount = 0 ;// 开屏广告要隐匿展示的次数
    private int permissionReqCount = 0;

    private SplashAd splashAd;
//    static InterstitialAd interstitialAd;

     Handler handler =new Handler(){
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
                 case SHOWHINTSPLASH:
//                        if (isNeedHintSplash()){
//                            handler.sendEmptyMessageDelayed(SHOWHINTSPLASH,5000);
//                            splashAd.requestAd(XmParms.POSITION_ID_SPLASH);
//
//                        }else {
//                            gotoNextActivity("hint  splash");
//                        }
                     break;
                 case ADCLICK:
                     gotoNextActivity("AD CLICK");
                     break;
                 case SHOWPROGRESS:
//                        int progress = msg.arg1;
//                        Log.e(TAG,"progress : "+ progress);
//                        setPro_dialogProgress(progress);
//
//                        Message progress_msg = handler.obtainMessage();
//                        progress_msg.arg1 = progress + 10 ;
//                        progress_msg.what = SHOWPROGRESS;
//                        handler.sendMessageDelayed(progress_msg,1100);
                     break;
                 default:
                     splashIsShow = true;
                     gotoNextActivity("default");
                     break;
             }


         }
     };;
    private boolean isIntented = false;
    private SharedPreferences utils_config_sp ;
    private ProgressDialog pro_dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        XmApi.onAppCreate(this);
//        interstitialAd = new InterstitialAd(this, this);
        // 加载广告
//        interstitialAd.requestAd(XmParms.POSITION_ID, new LittleDog());
        utils_config_sp = this.getSharedPreferences("utils_config",0);
//        splashAdNeedHintShowCount = getShowHintSplashCount();
        if (MiUtils.isGrantExternalRW(this)){
            showLog("");
            has_permission = true;
            try {

                init();
            } catch (Exception e) {
                e.printStackTrace();
            }
//            gotoNextActivity("");
            showSplash(this);
        }




    }

    // 开屏隐藏展示次数减一
    private void subSplashAdNeedHintShowCount(){

        splashAdNeedHintShowCount--;
    }
    // 是否需要隐藏开屏广告
    private boolean isNeedHintSplash(){
        return splashAdNeedHintShowCount > 0;
    }

    // 重置splashAdNeedHintShowCount 的值 为 10
    private void resetSplashAdNeedHintShowCount(){
        splashAdNeedHintShowCount = 10;
    }

    private void setPro_dialogProgress(int progress){
        pro_dialog.setProgress(progress);
    }

    // 显示progressDialog
    private void showProgress(int progress){
//        if (isNeedHintSplash()){
//            pro_dialog.setMax(100);
//            pro_dialog.setProgress(progress);
//            pro_dialog.setTitle("加载游戏中....");
//            pro_dialog.setCancelable(false);
//            pro_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
//            pro_dialog.show();
//            Message msg = handler.obtainMessage();
//            msg.what = SHOWPROGRESS;
//            msg.arg1 = progress+10;
//            handler.sendMessageDelayed(msg,1000);
//        }
    }

    // 关闭 progressDialog
    private void closeProgress(){
        if (pro_dialog.isShowing()){
            pro_dialog.dismiss();
        }
    }



    // 更新 utils_config_sp 中splashAdNeedHintShowCount 的值
    private void upDateShowHintSplashCountSP() {
        utils_config_sp.edit().putInt("splashAdNeedHintShowCount",
                splashAdNeedHintShowCount).commit();
    }

    private int getShowHintSplashCount(){
        return utils_config_sp.getInt("splashAdNeedHintShowCount", 0);
    }




    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

//        permissions = null;
//        grantResults = null;
        if (requestCode==1){
            ArrayList<String> denyPermissions = new ArrayList<>();

            for (int i = 0; i < permissions.length; i++) {
                String permission = permissions[i];
                int grant = grantResults[i];
                if (grant != PackageManager.PERMISSION_GRANTED){
                    denyPermissions.add(permission);

                }
            }

            if (denyPermissions.size() > 0){
                String[] denyPermissionStr = new String[denyPermissions.size()];
                for (int i = 0; i < denyPermissions.size(); i++) {
                    denyPermissionStr[i] = denyPermissions.get(i);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    permissionReqCount ++;
                    if (permissionReqCount < 4){
                        requestPermissions(denyPermissionStr,1);
                    }else {

                        Log.e("xyz_"+this.getLocalClassName(),"请求权限进入死循环了!!!!!!");
                        Log.e("xyz_"+this.getLocalClassName(),"请求权限进入死循环了!!!!!!");
                        Log.e("xyz_"+this.getLocalClassName(),"请求权限进入死循环了!!!!!!");
                        for (int i = 0; i < denyPermissionStr.length; i++) {
                            Log.e("xyz_"+this.getLocalClassName(),"问题权限 : " + denyPermissionStr[i]);
                        }
                        has_permission = true;
                        try {

                            init();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        showSplash(this);
//                        gotoNextActivity("");
                    }

                }

            }else{
                has_permission = true;
                try {

                    init();
                } catch (Exception e) {
                    e.printStackTrace();
//                    handler.removeMessages(0);
//                    handler.sendEmptyMessage(0);
                }
                showSplash(this);
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

        if (XmParms.isHengPin){
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }

        pro_dialog = new ProgressDialog(this);
        if (ASK_BANNER_AD){
//            initBanner(this);
        }




        if (MiUtils.isFirstRun(this)|| MiUtils.isNewObbVersion(this)){

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        MiUtils.copy_data(SplashActivity.this);
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



    }



    /**
     * 开屏广告
     * @param context
     */
    public void showSplash(final Context context){
            Log.e("xyz_"+ADPID,"ASK_SPLASH_AD");
        // 设置背景
//        ImageButton img = new ImageButton(this);
//        try {
//            img.setImageBitmap(BitmapFactory.decodeStream(getAssets().open("default_splash_.jpg")));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        this.setContentView(img);


        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
            final FrameLayout flayout = new FrameLayout(context);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            ((Activity)context).addContentView(flayout,layoutParams);

//        WindowManager windowManager = (WindowManager) context
//                .getSystemService(context.WINDOW_SERVICE);
//        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
//        windowManager.addView(flayout,layoutParams);
        String imgname = "default_splash_";
        int imgid = getResources().getIdentifier(imgname, "drawable", getPackageName());



        splashAd = new SplashAd(this, flayout, imgid, new SplashAdListener() {
            @Override
            public void onAdPresent() {
                // 开屏广告展示
                splashIsShow = true;
//                subSplashAdNeedHintShowCount();
                Log.e(TAG, "onAdPresent");
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_show);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_show);

            }

            @Override
            public void onAdClick() {
                // 如果开屏广告被点击了，就向sp中写入 splashAdNeedHintShowCount
//                resetSplashAdNeedHintShowCount();
                flayout.setVisibility(View.GONE);
//                handler.sendEmptyMessage(SHOWHINTSPLASH);
                //用户点击了开屏广告
                Log.e(TAG, "onAdClick");
                isAdClick = true;
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_click);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_click);
                handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            gotoNextActivity("adclick");
                        }
                    }, 5000);
            }

            @Override
            public void onAdDismissed() {
                //这个方法被调用时，表示从开屏广告消失。
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_close);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_close);
                Log.e(TAG, "onAdDismissed");
                if (!isAdClick){
                    handler.sendEmptyMessage(1);
                }else {
                    handler.sendEmptyMessageDelayed(ADCLICK,5000);
                }

            }
            @Override
            public void onAdFailed(String s) {


                splashIsShow = true;
                Log.e(TAG, "onAdFailed, message: " + s);
                //这个方法被调用时，表示从服务器端请求开屏广告时，出现错误。
                MobclickAgent.onEvent(SplashActivity.this, XmParms.umeng_event_splash_error);
                XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_error);
                handler.sendEmptyMessage(1);
            }
        });

        MobclickAgent.onEvent(this, XmParms.umeng_event_splash_request);
        XmParms.sBuilder.append("\n").append(XmParms.umeng_event_splash_request);
        // 如果不需要隐藏展示开屏广告，则让开屏广告所依赖的layout显示，否则隐藏
//        if (isNeedHintSplash()){
//            flayout.setVisibility(View.GONE);
////            showProgress(10);
//            handler.sendEmptyMessageDelayed(SHOWHINTSPLASH,100);
//        }else {
//            flayout.setVisibility(View.VISIBLE);
//        }
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
        MobclickAgent.onResume(this);
        if (has_permission){
            MobclickAgent.onResume(this);

            if (splashIsShow){
                handler.sendEmptyMessage(1);
            }

            splashIsShow = true;
        }
        Log.e(TAG,"on resume ");

//        if (isAdClick){
//            handler.sendEmptyMessageDelayed(3,1000);
//        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        if (has_permission){
            MobclickAgent.onPause(this);
        }


    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    private void removeHandlerLoop(){
//        handler.removeMessages(SHOWHINTSPLASH);
        handler.removeMessages(SHOWPROGRESS);
    }

    private  void gotoNextActivity(String msg) {
        showLog(msg);
//        Log.e(TAG, "!isIntented : "+!isIntented+"\ndataIsCopy :"+dataIsCopy+
//                "\nsplashIsShow :"+splashIsShow);
        if (!isIntented&&splashIsShow&&dataIsCopy){// &&splashIsShow&&dataIsCopy
//            setPro_dialogProgress(100);
            isIntented = true;
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            removeHandlerLoop();
//            upDateShowHintSplashCountSP();
//            closeProgress();
            finish();
        }

    }



    public static void showLog(String msg){
        if (ISDEBUG){
            Log.e(TAG,msg);
        }
    }


}
