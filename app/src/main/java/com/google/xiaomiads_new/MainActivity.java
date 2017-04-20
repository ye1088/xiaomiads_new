package com.google.xiaomiads_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

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
}
