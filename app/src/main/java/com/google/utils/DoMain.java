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

    public static  String initial_ads_id = "";
    public static String banner_ads_id = "";
    public static String app_id = "";
    public static String umeng_key = "";

    public static void initPara(Context context) throws IOException, JSONException {
       AssetManager assets = context.getAssets();
       InputStream open = assets.open("zconfig.zbin");
       JSONObject jsonObject = new JSONObject(SUtils.inputString2String(open));
       initial_ads_id = jsonObject.getString("INITIAL_ADS_ID");
       banner_ads_id= jsonObject.getString("BANNER_ADS_ID");
       app_id= jsonObject.getString("APP_ID");
       umeng_key= jsonObject.getString("UMENG_KEY");

       if (umeng_key.length()!=24||app_id.length()<=1){
           System.exit(0);
       }

    }

}
