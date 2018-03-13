package com.google.littleDog;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.google.utils.XmParms;
import com.umeng.analytics.MobclickAgent;
import com.uniplay.adsdk.AdView;
import com.uniplay.adsdk.VideoAd;
import com.uniplay.adsdk.VideoAdListener;


/**
 * Created by admin on 2018/2/5.
 */

public class Video_Ad implements VideoAdListener {

    private static Context mContext;
    private static VideoAd videoAd;

    private static final int SHOW_VIDEO = 0;
    private static final int LOAD_VIDEO = 1;
    public static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_VIDEO:
                    show_video();
                    break;
                case LOAD_VIDEO:
                    loadAd();
                    break;
            }
        }
    };


    public static void  init(Context context){
        mContext = context;
        AdView adView = new AdView(mContext, XmParms.VIDEO_ID);

        videoAd = VideoAd.getInstance().init(mContext,XmParms.VIDEO_ID, new Video_Ad());
        videoAd.loadVideoAd();
    }

    public static void show_video(){

        if (videoAd.isVideoReady()){
            Log.e("xyz","playVideoAd() !!!");
            videoAd.playVideoAd();
        }else {
            Log.e("xyz","video ad is not ready !!!");
            loadAd();
            Toast.makeText(mContext, "广告加载中,请稍后再试....", Toast.LENGTH_SHORT).show();
        }
    }


    public static void postShwoVideo(){
        Log.e("xyz","postShwoVideo() !!!");
        mHandler.removeMessages(SHOW_VIDEO);
        mHandler.sendEmptyMessage(SHOW_VIDEO);
    }


    public static void  postLoadAdDelay(){
        Log.e("xyz","postLoadAdDelay() !!!");
        mHandler.removeMessages(LOAD_VIDEO);
        mHandler.sendEmptyMessageDelayed(LOAD_VIDEO,1000);
    }

    public static void loadAd(){
        Log.e("xyz","loadAd video() !!!");
        videoAd.loadVideoAd();
    }







    @Override
    public void onVideoAdReady() {
        Log.e(this.getClass().getName()+"xyz","onVideoAdReady");
    }

    @Override
    public void onVideoAdStart() {
        Log.e(this.getClass().getName()+"xyz","onVideoAdStart");
    }

    @Override
    public void onVideoAdProgress(int i, int i1) {
        Log.e(this.getClass().getName()+"xyz","onVideoAdProgress");
    }

    @Override
    public void onVideoAdFailed(String s) {

        Log.e(this.getClass().getName()+"xyz","onVideoAdFailed : "+s);
    }

    @Override
    public void onVideoAdComplete() {
        Log.e(this.getClass().getName()+"xyz","onVideoAdComplete");
        MobclickAgent.onEvent(mContext,XmParms.umeng_event_video_show);


    }

    @Override
    public void onVideoAdClose() {
        Log.e(this.getClass().getName()+"xyz","onVideoAdClose");
        postLoadAdDelay();
    }
}
