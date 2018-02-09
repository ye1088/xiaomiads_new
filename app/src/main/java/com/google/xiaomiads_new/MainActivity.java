package com.google.xiaomiads_new;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.google.littleDog.LittleDog;
import com.google.littleDog.Video_Ad;
import com.google.utils.ButtonUtils;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LittleDog.onCreate(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        LittleDog.onResume(this);

    }



    @Override
    protected void onPause() {
        super.onPause();
        LittleDog.onPause(this);
    }

    public void bt_click(View view) {

        switch (view.getId()){
            case R.id.showBanner:
                LittleDog.setVisibleBanner();
                break;

            case R.id.hideBanner:
                LittleDog.hideBanner();
                break;

            case R.id.showInter:
                LittleDog.show_ad();
                break;
            case R.id.selDialog:
                ButtonUtils.postSelDialog();
                break;
            case R.id.showVideo:
                Video_Ad.postShwoVideo();
                break;
        }

    }
}
