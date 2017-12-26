package com.google.utils;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by ye1088 on 2017/4/15.
 */

public class SUtils {

    private static final String SAVE_DATA_PATH = "save_data";
    private static final boolean ISDEBUG = false;
    private static final int BUFF_SIZE = 1024 * 1024;
    static int[] sizes = {0,0};
    private static Context mContext ;

    private static Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
//                    Toast.makeText(mContext, "如果一直卡在这里请清除数据并给予游戏存储权限!!!!", Toast.LENGTH_LONG).show();

                    pro_dialog.setMax(sizes[0]);
                    pro_dialog.setTitle("解压数据中....");
                    pro_dialog.setCancelable(false);
                    pro_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pro_dialog.show();
                    break;
                case 1:
                    pro_dialog.setMax(sizes[0]);
                    pro_dialog.setTitle("解压数据中....");
                    pro_dialog.setCancelable(false);
                    pro_dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    pro_dialog.show();
                    pro_dialog.setProgress(sizes[1]);

                    break;
                case -1:
                    pro_dialog.dismiss();
                    break;
            }
        }
    };

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Activity mActivity, float dpValue) {
        final float scale = mActivity.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int[] getSizes() {
        return sizes;
    }



    static ProgressDialog pro_dialog = null;

    static String[] assetsFileNames = null;

    public static void copy_data(Context context) throws Exception {
        mContext = context;
        AssetManager manager = context.getAssets();
        InputStream open = null;
        InputStream open2 = null;
        InputStream open3 = null;
        InputStream open4 = null;
        InputStream open5 = null;
        if (assetsFileNames==null){
            assetsFileNames = getAssetsFileNames(manager);
        }

        boolean hasSplitFile = false;
        ArrayList<String> splitFilesName = new ArrayList<>();
        ArrayList<String> splitFilesName_patch = new ArrayList<>();
        for (String name:assetsFileNames) {
            if (name.contains("my_split_patch")){
                hasSplitFile = true;
                splitFilesName_patch.add(name);
            }else if (name.contains("my_split")){
                hasSplitFile = true;
                splitFilesName.add(name);
            }
            if ("extobb.save".equals(name)){
                open = manager.open("extobb.save");
                sizes[0] += open.available();
            }else if ("extdata.save".equals(name)){
                open2 = manager.open("extdata.save");
                sizes[0] += open2.available();
            }else if ("data.save".equals(name)){
                open3 = manager.open("data.save");
                sizes[0] += open3.available();
            }else if ("mcdata".equals(name)){
                open5 = manager.open("mcdata");
                sizes[0] += open5.available();
            }
        }
        if (hasSplitFile){
            open4 = manager.open(splitFilesName.get(0));
            sizes[0]+= open4.available()*(splitFilesName.size()+splitFilesName_patch.size());
            open4.close();
            copy_split_files(splitFilesName,manager,"/sdcard/Android/obb/"+context.getPackageName(),".my_split");
            copy_split_files(splitFilesName_patch,manager,"/sdcard/Android/obb/"+context.getPackageName(),".my_split_patch");
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
        if (open5 != null){
            unZip_data(open4,"/sdcard/huluxia/mctool/");
            open5.close();
        }


        mHandler.sendEmptyMessage(-1);


    }



    /**
     * 判断是否拥有权限
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);

            return false;
        }

        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.READ_PHONE_STATE}, 0);
        }

        return true;
    }


    /**
     *
     * @param splitFilesName : 保存拆分obb 的文件名
     * @param manager   assetmanager
     * @param destFile  应用对应的obb文件夹
     * @param flag_str  拆分的字符串
     * @throws IOException
     */
    public static void copy_split_files(ArrayList<String> splitFilesName, AssetManager manager, String destFile, String flag_str) throws IOException {


        if (!(splitFilesName.size()>0)){
            return;
        }
        File file = new File(destFile);
        if (!file.exists()){
            file.mkdir();
        }
        int split_len = splitFilesName.size();
        String dst_file_name  =  splitFilesName.get(0).substring(0,splitFilesName.get(0).indexOf(flag_str));    //".my_split"
        byte buffer[] = new byte[BUFF_SIZE];
        FileOutputStream out = new FileOutputStream(destFile+ File.separator+dst_file_name);
        int realLength;
        for (int i = 0; i < split_len; i++) {
            InputStream inputStream = manager.open(dst_file_name+flag_str+ String.valueOf(i));//".my_split"
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
                out.flush();
            }
            inputStream.close();
        }

        out.close();



    }



    public static void copyFile2where(InputStream inputStream, String destFile) throws Exception {

        File dst_file = new File(destFile);
        File dst_file_parent = dst_file.getParentFile();
        if (!dst_file_parent.exists()){
            dst_file_parent.mkdirs();
        }

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
            out.flush();
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
        pro_dialog = new ProgressDialog(context);

        SharedPreferences sp = context.getSharedPreferences("utils_config", 0);
        boolean isFirstRun = sp.getBoolean("isFirstRun", true);
        if (isFirstRun){
            SharedPreferences.Editor edit = sp.edit();
            edit.putBoolean("isFirstRun", false);
            edit.commit();
        }

        return isFirstRun;
    }




    /**
     * 备份存档
     * @param context
     */
    public static String backupSaveData(Context context){
        String data_path = context.getFilesDir().getParent();
        showLog("xyz","data_path : "+data_path);
        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (!sdCardExist){
            return "";
        }
        try{

            copyDir2Dst(context,data_path,Environment.getExternalStorageDirectory()+File.separator+
                    SAVE_DATA_PATH+File.separator+context.getPackageName());
        }catch (Exception e){
            Log.e("xyz",e.toString());
            return e.toString();
        }
        return "";
    }

    /**
     * 拷贝文件夹到目标文件夹中
     * @param context 上下文
     * @param srcDir 源文件将爱
     * @param dstDir    目标文件夹
     */
    public static void  copyDir2Dst(Context context ,String srcDir,String dstDir){
        File data_dir = new File(srcDir);
        showLog("xyz","srcDir : "+srcDir);
        showLog("xyz","dstDir : "+dstDir);
        for (File file :
                data_dir.listFiles()) {
            if (file.getName().equals("lib")){
                continue;
            }
            if (file.isFile()) {
                try {
                    copyFile2where(new FileInputStream(file),dstDir+ File.separator + file.getName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                copyDir2Dst(context ,file.getAbsolutePath(),dstDir+File.separator+file.getName());
            }
        }
    }


    public static void showLog(String tag,String msg){
        if (ISDEBUG){
            Log.e(tag,msg);
        }
    }



    // 查看 assetsFileNames 里面是否有某一个文件
    public static boolean hasSomeFileInAssetsFileNames(String findName, Context context) throws IOException {
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

    public static boolean isNewObbVersion(Context context)  {
        String obb_name = "";

        try {
            if (!hasSomeFileInAssetsFileNames("extobb.save",context)){
                return false;
            }else{
                InputStream open = context.getAssets().open("extobb.save");
                obb_name = getFileNameInZip(open);
                open.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String obb_path = context.getObbDir().getPath()+ File.separator+obb_name;
        System.out.println("obb path : "+obb_path);
        File obb_file = new File(obb_path);
        if (obb_file.exists()){
            return false;
        }else {
            return true;
        }
    }

    private static String getFileNameInZip(InputStream inputStream) throws IOException {
        ZipInputStream zis = new ZipInputStream(inputStream);
        ZipEntry entry = null;
        while ((entry = zis.getNextEntry())!=null){
            String file_name = entry.getName();
            if (file_name.contains("obb")){
                zis.close();
                return file_name;
            }
        }
        return null;
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

    public static String inputString2String(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        Scanner scanner = new Scanner(is, "utf-8");
        while (scanner.hasNextLine()){
            sb.append(scanner.nextLine());
        }
        scanner.close();
        return sb.toString();
    }
}
