package com.google.xiaomiads_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.littleDog.LittleDog;

public class MainActivity extends AppCompatActivity {

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
            case R.id.hideBan:
                LittleDog.hideBanner();
                break;
            case R.id.showBan:
                LittleDog.postShowBanner();
                break;
            case R.id.showInter:
                LittleDog.postShowInterstitial();
        }
    }
}
