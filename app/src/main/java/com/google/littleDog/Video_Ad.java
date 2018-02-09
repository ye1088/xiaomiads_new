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
    public static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case SHOW_VIDEO:
                    show_video();
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
            videoAd.playVideoAd();
        }else {
            videoAd.loadVideoAd();
            Toast.makeText(mContext, "广告加载中,请稍后再试....", Toast.LENGTH_SHORT).show();
        }
    }


    public static void postShwoVideo(){
        mHandler.removeMessages(SHOW_VIDEO);
        mHandler.sendEmptyMessage(SHOW_VIDEO);
    }







    @Override
    public void onVideoAdReady() {
        Log.e(this.getClass().getName(),"onVideoAdReady");
    }

    @Override
    public void onVideoAdStart() {
        Log.e(this.getClass().getName(),"onVideoAdStart");
    }

    @Override
    public void onVideoAdProgress(int i, int i1) {
        Log.e(this.getClass().getName(),"onVideoAdProgress");
    }

    @Override
    public void onVideoAdFailed(String s) {

        Log.e(this.getClass().getName(),"onVideoAdFailed : "+s);
    }

    @Override
    public void onVideoAdComplete() {
        Log.e(this.getClass().getName(),"onVideoAdComplete");
        MobclickAgent.onEvent(mContext,XmParms.umeng_event_video_show);
    }

    @Override
    public void onVideoAdClose() {
        Log.e(this.getClass().getName(),"onVideoAdClose");
    }
}
