package com.google.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ye1088 on 2017/4/15.
 */

public class SUtils {

    private static final int BUFF_SIZE = 1024 * 1024;
    static int[] sizes = {0,0};
    private static Context mContext ;

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    Toast.makeText(mContext, "如果一直卡在这里请清除数据并给予游戏存储权限!!!!", Toast.LENGTH_LONG).show();
                    pro_dialog = new ProgressDialog(mContext);
                    pro_dialog.setMax(sizes[0]);
                    pro_dialog.setTitle("拷贝数据中....");
                    pro_dialog.setCancelable(false);
                    pro_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pro_dialog.show();
                    break;
                case 1:
                    pro_dialog.setProgress(sizes[1]);
                    break;
                case -1:
                    pro_dialog.dismiss();
                    break;
            }
        }
    };


    public static int[] getSizes() {
        return sizes;
    }



    static ProgressDialog pro_dialog;

    static String[] assetsFileNames = null;

    public static void copy_data(Context context) throws Exception {
        mContext = context;
        AssetManager manager = context.getAssets();
        InputStream open = null;
        InputStream open2 = null;
        InputStream open3 = null;
        if (assetsFileNames==null){
            assetsFileNames = getAssetsFileNames(manager);
        }

        for (String name:assetsFileNames) {
            if ("extobb.save".equals(name)){
                open = manager.open("extobb.save");
                sizes[0] += open.available();
            }else if ("extdata.save".equals(name)){
                open2 = manager.open("extdata.save");
                sizes[0] += open2.available();
            }else if ("data.save".equals(name)){
                open3 = manager.open("data.save");
                sizes[0] += open3.available();
            }
        }
        mHandler.sendEmptyMessage(0);
        if (open != null){

            unZip_data(open,"/sdcard/Android/obb/"+context.getPackageName());
            open.close();
        }
        if (open2 != null){
            unZip_data(open2,"/sdcard/Android/data/"+context.getPackageName());
            open2.close();
        }
        if (open3 != null){
            unZip_data(open3,"/data/data/"+context.getPackageName());
            open3.close();
        }
        mHandler.sendEmptyMessage(-1);


    }



    public static void copyFile2where(InputStream inputStream, String destFile) throws Exception {

        FileOutputStream out = new FileOutputStream(destFile);
        byte buffer[] = new byte[BUFF_SIZE];
        int realLength;
        long currentTime = 0;
        long oldTime = System.currentTimeMillis();
        while ((realLength = inputStream.read(buffer)) > 0) {
            sizes[1] += realLength;
            currentTime = System.currentTimeMillis();
            if ((currentTime-oldTime)>500){
                oldTime=currentTime;
                mHandler.sendEmptyMessage(1);
            }
            out.write(buffer, 0, realLength);
        }
//        inputStream.close();
        out.close();
    }


    public static void unZip_data(InputStream srcPath, String dstPath) throws Exception {
        ZipInputStream zis = new ZipInputStream(srcPath);
        File dstPathFile = new File(dstPath);
        if (!dstPathFile.exists()){
            dstPathFile.mkdirs();
        }
        ZipEntry entry;
        while ((entry = zis.getNextEntry())!=null){
            if (entry.isDirectory()){
                new File(dstPath+ File.separator+entry.getName()).mkdirs();
            }else {
                String entryParent =dstPath+ File.separator+entry.getName();
                File entryDir = new File(new File(entryParent).getParent());
                if (!entryDir.exists()){
                    entryDir.mkdirs();
                }
                copyFile2where(zis,dstPath+ File.separator+entry.getName());
            }
        }

        zis.closeEntry();
        zis.close();
    }

    // 判断是否为第一次运行
    public static boolean isFirstRun(Context context)  {

        SharedPreferences sp = context.getSharedPreferences("utils_config", 0);
        boolean isFirstRun = sp.getBoolean("isFirstRun", false);
        if (!isFirstRun){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("isFirstRun", true);
            edit.commit();
        }

        return isFirstRun;
    }

    // 查看 assetsFileNames 里面是否有某一个文件
    public static boolean hasSomeFileInAssetsFileNames(String findName,Context context) throws IOException {
        if (assetsFileNames==null){
            AssetManager manager = context.getAssets();
            assetsFileNames = getAssetsFileNames(manager);
        }
        for (String name :
                assetsFileNames) {
            if (findName.equals(name)){
                return true;
            }
        }
        return false;
    }


    // 判断是不是新版本要不要跟新obb
    public static boolean isNewObbVersion(Context context) throws Exception {


        if (!hasSomeFileInAssetsFileNames("extobb.save",context)){
            return false;
        }
        String obb_path = context.getObbDir().getPath()+File.separator+"main."+
                getVersionCode(context)+"."+context.getPackageName()+".obb";
        System.out.println("obb path : "+obb_path);
        File obb_file = new File(obb_path);
        if (obb_file.exists()){
            return false;
        }else {
            return true;
        }
    }

    // 获取应用版本名字
    public static String getVersionCode(Context context) throws PackageManager.NameNotFoundException {
        return String.valueOf(context.getPackageManager().
                getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA).versionCode);
    }

    // 获取 assets 目录下的文件名数组
    public static String[] getAssetsFileNames(AssetManager manager) throws IOException {
        return manager.list("");
    }
}
