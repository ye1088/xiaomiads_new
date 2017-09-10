package com.google.utils;

import android.content.Context;
import android.content.res.AssetManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by yezi_01 on 2017/5/23.
 */

public class DoMain {

    public static String initial_ads_id = "1999";
    public static String banner_ads_id = "1998";
    public static String splash_id = "2000";
    public static String app_id = "182";
    public static String umeng_key = "58de3d6c734be4386c0003bb";

    public static void initPara(Context context) {
       AssetManager assets = context.getAssets();
        InputStream open = null;
        try {
            open = assets.open("zconfig.zbin");

            JSONObject jsonObject = new JSONObject(SUtils.inputString2String(open));
           initial_ads_id = jsonObject.getString("INITIAL_ADS_ID");
           banner_ads_id= jsonObject.getString("BANNER_ADS_ID");
           app_id= jsonObject.getString("APP_ID");
           splash_id= jsonObject.getString("SPLASH_ID");
           umeng_key= jsonObject.getString("UMENG_KEY");

           if (umeng_key.length()!=24||app_id.length()<=1){
               System.exit(0);
           }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}
