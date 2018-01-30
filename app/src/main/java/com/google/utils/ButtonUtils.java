package com.google.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.littleDog.LittleDog;

import java.io.IOException;

/**
 * Created by admin on 2018/1/18.
 */

public class ButtonUtils {


    static Context mContext;
    static Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){

                case COUNT_DOWN:
                    countDown(msg.arg1,false);
                    break;

                case RECOVER:
                    String aa = "要执行的方法";
                    LittleDog.postShowInterstitial();
                    MiUtils.sendMsg2Unity("myInject","recoverHealth","");
                    showLog("recoverHealth");
                    break;

            }
        }
    };;
    static TextView countDown_tv;
    static AlertDialog dialog;


    public static final int COUNT_DOWN = 0;
    private static final int RECOVER = 1;


    public static void init(Context context){
        mContext = context;
//        countDown_tv = new TextView(mContext);


    }


    public static void showLog(String msg){
        Log.e("ButtonUtils",msg);
    }

    /***
     * 每隔1秒弹出一个吐司
     * @param left 持续 left 秒
     * @return 剩余的秒数
     */
    public static int countDown(int left, boolean isFirst){
        showLog("count down : "+ left);
        if (isFirst){

//            countDown_tv = showTextView(""+left,Gravity.CENTER,Gravity.CENTER);
        }

//        countDown_tv.setText(""+left);
        dialog.getButton(DialogInterface.BUTTON_POSITIVE).setText(String.format("满血复活(%ds)", left));
        if (left == 0){
//            countDown_tv.setText("");
            dialog.dismiss();
            return -1;
        }

        Message msg = mHandler.obtainMessage();
        msg.what = COUNT_DOWN;
        msg.arg1 = left -1;
        mHandler.sendMessageDelayed(msg,1000);
        return left -1;
    }



    public static Activity getActivity(){
        return (Activity)mContext;
    }



    public static WindowManager getWindowManager(){
        return (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
    }


    /**
     * 显示一个textview到游戏界面上
     * @param text  显示的文本
     * @param gravity1  text所在的位置1
     * @param gravity2  text所在的位置2   最终位置 = gravity1 | gravity2
     * @return
     */
    public static TextView  showTextView(String text,int gravity1,int gravity2){
        FrameLayout layout = new FrameLayout(mContext);
        layout.removeAllViews();
        FrameLayout.LayoutParams button_param = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        final WindowManager manager = getWindowManager();
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = gravity1 | gravity2;
        params.format = PixelFormat.RGBA_8888;


        params.height =  ViewGroup.LayoutParams.WRAP_CONTENT; //SUtils.dip2px(activity,50);




        TextView tv = new TextView(mContext);
        tv.setText(text);
        // 文字大小
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, MiUtils.dip2px(getActivity(),45));
//        tv.setAlpha(0x00000000);
        layout.addView(tv,button_param);
//        layout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(mContext, "被点击了", Toast.LENGTH_SHORT).show();
//            }
//        });
        manager.addView(layout,params);

        return tv;



    }



    public static void selDialog(){


        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setCancelable(false);
        builder.setPositiveButton("满血复活", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                Message msg = mHandler.obtainMessage();
                msg.what = RECOVER;
                mHandler.removeMessages(RECOVER);
                mHandler.sendMessage( msg);

            }
        });


        builder.setNegativeButton("还是算了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mHandler.removeMessages(RECOVER);
                MiUtils.sendMsg2Unity("myInject","PlayerKilled","");
            }
        });
        builder.setTitle("Warning");
        builder.setMessage("你的龙倒下了……免费送你一次满血复活的机会，继续战斗吧?");
        builder.create();
        dialog = builder.show();


        // 倒计时
        countDown(5, true);
        // 显示广告并回血
        Message msg = mHandler.obtainMessage();
        msg.what = RECOVER;
        mHandler.removeMessages(RECOVER);
        mHandler.sendMessageDelayed( msg,5000 );

    }


    public static void showButton(){
        FrameLayout layout = new FrameLayout(mContext);
        layout.removeAllViews();
        FrameLayout.LayoutParams button_param = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        WindowManager manager = (WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.format = PixelFormat.RGBA_8888;


        params.height =  ViewGroup.LayoutParams.WRAP_CONTENT; //SUtils.dip2px(activity,50);

        ImageButton button = new ImageButton(mContext);
        try {
            button.setImageBitmap(BitmapFactory.decodeStream(mContext.getAssets().open("recover.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
//        Button button = new Button(mContext);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LittleDog.postShowInterstitial();
            }
        });
//        button.setText("广告");
        button.setBackgroundColor(0x00000000);
        layout.addView(button,button_param);

        manager.addView(layout,params);
    }
}
