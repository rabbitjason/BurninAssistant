package com.bugsbunny.burninassistant.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;

/**
 * Created by Administrator on 2015/12/4.
 */
public class AndroidTools {
    // 定义对话框变量
    public static ProgressDialog progressDialog;

    /**
     * 设置全屏显示
     *
     * @param activity
     */
    public static void setFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 设置显示状态栏，隐藏标题栏
     *
     * @param activity
     */
    public static void setInvisibleTitle(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }
    // 显示窗口

    /**
     * @param context
     * @since 2014-4-8 更新该对话框不能取消，因为如果获取数据过程中取消的话，会影响数据的接收，还是修改回来吧
     */
    public static void showProgressDialog(final Context context, String message,
                                          boolean isCancelable) {
        DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
                    cancelProgressDialog(context);
                }
                return false;
            }
        };
        // 显示ProgressDialog
        if (progressDialog == null || !progressDialog.isShowing()) {
            // progressDialog = ProgressDialog.show(context, "正在读取", "请稍后……",
            // true, true);
            progressDialog = ProgressDialog.show(context, "", message, true,
                    isCancelable);

        }

        progressDialog.setOnKeyListener(onKeyListener);

    }

    /**
     * 关闭窗口
     *
     * @param context
     */
    public static void cancelProgressDialog(Context context) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void updateProgressDialogMessage(String message) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.setMessage(message);
        }
    }

    /**
     * @param activity
     * @return false 为没有可用网络 true 为当前网络正常
     */
    public static boolean checkNetworkState(Activity activity) {
        ConnectivityManager manager = (ConnectivityManager) activity
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            return false;
        }
        if (info.isRoaming()) {
            return true;
        }
        return true;
    }

    /**
     * 加载本地sdcard图片
     *
     * @param url
     * @return
     */
    public static Bitmap getLoacalBitmap(String url) {
        try {
            FileInputStream fis = new FileInputStream(url);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 得到根据指定大小进行压缩的图片，开发的时候可以设置为480， 800即可
     *
     * @param filePath
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    public static Bitmap getCompressedBitmapBySize(String filePath,
                                                   int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        if (reqHeight == 0) {
            reqHeight = 800;
        }
        if (reqWidth == 0) {
            reqWidth = 480;
        }
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        Bitmap bm = BitmapFactory.decodeFile(filePath, options);
        if (bm == null) {
            return null;
        }
        int degree = readPictureDegree(filePath);
        bm = rotateBitmap(bm, degree);
        ByteArrayOutputStream baos = null;
        try {
            baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        } finally {
            try {
                if (baos != null)
                    baos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return bm;

    }

    /**
     * 获取原始的图片尺寸后，根据目标计算缩放比例系数 官方文档中说，inSampleSize这个属性最好是2的倍数，这样处理更快，效率更高
     *
     * @param options
     * @param reqWidth
     * @param reqHeight
     * @return
     */
    private static int calculateInSampleSize(BitmapFactory.Options options,
                                             int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // Calculate ratios of height and width to requested height and
            // width
            final int heightRatio = Math.round((float) height
                    / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            // Choose the smallest ratio as inSampleSize value, this will
            // guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? widthRatio : heightRatio;
        }
        return inSampleSize;
    }

    /**
     * 旋转图片
     *
     * @param bitmap
     * @param rotate
     * @return
     */
    private static Bitmap rotateBitmap(Bitmap bitmap, int rotate) {
        if (bitmap == null)
            return null;

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        // Setting post rotate to 90
        Matrix mtx = new Matrix();
        mtx.postRotate(rotate);
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    /**
     * 读取图片旋转的度数，因为横屏拍摄的缘故
     *
     * @param path
     * @return
     */
    private static int readPictureDegree(String path) {
        int degree = 0;
        try {
            ExifInterface exifInterface = new ExifInterface(path);
            int orientation = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取图片
     *
     * @param path
     * @return
     */
    public static Bitmap getRemoteImage(final String path) {
        URLConnection conn;
        Bitmap bm = null;
        try {
            URL url = new URL(path);
            conn = url.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
            // return bm;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bm;
    }

    /**
     * 获取当前程序的版本名称和版本号码
     *
     * @param context
     * @return
     */
    public static String getVerName(Context context) {
        String verInfo = "";
        PackageInfo info;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            verInfo = info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verInfo;
    }

    //获取版本的版本号
    public static String getVerCodeString(Context context) {
        PackageInfo info;
        int verCode = 0;
        try {
            info = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);
            verCode = info.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verCode + "";
    }

    /**
     * 获取每台Android设备的唯一标识
     *
     * @param activity
     * @return
     */
    public static String getUniqueCode(Activity activity) {
        return Settings.Secure.getString(activity.getContentResolver(),
                Settings.Secure.ANDROID_ID);
    }

    private static long lastClickTime; // 最后一次点击的时间

    /**
     * 是否是快速的连续点击
     *
     * @return false
     */
    public static boolean isFastDoubleClick() {
        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }


    /**
     * 初始化ActionBar的标题 public static void initActionBar(SherlockActivity
     * activity, String title) { ActionBar bar = activity.getSupportActionBar();
     * if (bar == null) { return; } // 隐藏/显示Tittle
     * bar.setDisplayShowTitleEnabled(true); // 隐藏/显示Custom
     * bar.setDisplayShowCustomEnabled(true); // 添加子标题 // bar.setSubtitle( //
     * "The quick brown fox jumps over the lazy dog."); // 隐藏/显示返回箭头
     * bar.setDisplayHomeAsUpEnabled(true); // 隐藏/显示功能图片
     * bar.setDisplayShowHomeEnabled(false); // 设置使用activity的logo还是activity的icon
     * true为logo bar.setDisplayUseLogoEnabled(true); bar.setTitle(title); }
     */

    /**
     * 设置ListView的高度
     */
    public static void setLvHeight(ListView courseList) {
        ListAdapter listAdapter = courseList.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, courseList);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = courseList.getLayoutParams();
        params.height = totalHeight
                + (courseList.getDividerHeight() * (listAdapter.getCount() - 1));
        ((ViewGroup.MarginLayoutParams) params).setMargins(10, 10, 10, 10);
        courseList.setLayoutParams(params);
    }

    /**
     * 根据Activity获取ViewGroup
     *
     * @param act
     * @return
     */
    public static ViewGroup getViewGroup(Activity act) {
        ViewGroup systemContext = (ViewGroup) act.getWindow().getDecorView()
                .findViewById(android.R.id.content);
        ViewGroup content = null;
        if (systemContext.getChildCount() > 0
                && systemContext.getChildAt(0) instanceof ViewGroup) {
            content = (ViewGroup) systemContext.getChildAt(0);
        }
        return content;
    }

    /**
     * 更改一个界面的所有的字体
     *
     * @param root
     * @param act
     */
    public static void changeFonts(ViewGroup root, Activity act) {
        Typeface tf = Typeface.createFromAsset(act.getAssets(),
                "fonts/font_text.ttf");

        for (int i = 0; i < root.getChildCount(); i++) {
            View v = root.getChildAt(i);
            if (v instanceof TextView) {
                ((TextView) v).setTypeface(tf);
            } else if (v instanceof Button) {
                ((Button) v).setTypeface(tf);
            } else if (v instanceof EditText) {
                ((EditText) v).setTypeface(tf);
            } else if (v instanceof ViewGroup) {
                changeFonts((ViewGroup) v, act);
            }
        }
    }

    /**
     * 设置界面中的所有元素为同一个字体
     *
     * @param act
     */
    public static void setAllTextFonts(Activity act) {
        changeFonts(getViewGroup(act), act);
    }

    // 获取屏幕的宽度
    public static int getScreenWidth(Activity activity) {
        return activity.getResources().getDisplayMetrics().widthPixels;
//       return activity.getWindowManager().getDefaultDisplay().getWidth();
    }

    // 获取屏幕的高度
    public static int getScreenHeight(Activity activity) {
        return activity.getResources().getDisplayMetrics().heightPixels;
    }


    // 判断一个字段是否为空
    public static boolean textEmpty(String msg) {
        if (msg != null && !"".equals(msg) && !"null".equals(msg)) {
            return true;
        } else {
            return false;
        }
    }


    // 初步格式化
    public static boolean checkIsNullString(String input) {
        if (input == null || "".equals(input) || "null".equals(input)
                || "NULL".equals(input) || input == null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 根据月份和日期获得星座
     *
     * @param month
     * @param day
     * @return
     */
    public static String getXingzuoByDate(int month, int day) {
        String s = "魔羯水瓶双鱼牡羊金牛双子巨蟹狮子处女天秤天蝎射手魔羯";
        int[] arr = {20, 19, 21, 21, 21, 22, 23, 23, 23, 23, 22, 22};
        Integer num = month * 2 - (day < arr[month - 1] ? 2 : 0);
        return s.substring(num, num + 2) + "座";
    }

    //获取生日信息
    public static String getBirthInfoString(String birthday) {
        String result = "";
        if (birthday == null || "".equals(birthday) || !birthday.contains("-")) {
            result = "暂无生日星座信息";
        } else {
            String[] splitStrings = birthday.split("-");
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR) - Integer.valueOf(splitStrings[0]);
            result = year + "岁    " + getXingzuoByDate(Integer.valueOf(splitStrings[1]), Integer.valueOf(splitStrings[2]));
        }
        return result;
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    //检查是否为空
    public static boolean checkIfNull(String input) {
        if (TextUtils.isEmpty(input)) {
            return true;
        }
        if ("NULL".equals(input) || "null".equals(input)) {
            return true;
        }
        return false;
    }

    //格式化输出
    public static int getNumberFromStr(String input) {
        if (TextUtils.isEmpty(input)) {
            return 0;
        }
        if (checkIfNull(input)) {
            return 0;
        }
        return Integer.valueOf(input);
    }

    public static Date StrToDate(String str) {

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 取系统当前时间:返回值为如下形式
     * 2002-10-30
     *
     * @return String
     */
    public static String getYYYY_MM_DD() {
        return dateToString(new Date()).substring(0, 10);

    }

    /**
     * 将java.util.Date 格式转换为字符串格式'yyyy-MM-dd HH:mm:ss'(24小时制)<br>
     * 如Sat May 11 17:24:21 CST 2002 to '2002-05-11 17:24:21'<br>
     *
     * @param time Date 日期<br>
     * @return String   字符串<br>
     */


    public static String dateToString(Date time) {
        SimpleDateFormat formatter;
        formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String ctime = formatter.format(time);

        return ctime;
    }

    public static long getIntervelTime(String startTime, String endtime) {
        // 日期转换为毫秒 两个日期想减得到天数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeStart, timeEnd, daySecond = 0;
        //得到毫秒数
        try {
            timeStart = sdf.parse(startTime).getTime();
            timeEnd = sdf.parse(endtime).getTime();
            daySecond = (timeEnd - timeStart) / (1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return daySecond;
//两个日期想减得到天数
    }

    public static long getIntervelTime(Date startTime, Date endtime) {
        // 日期转换为毫秒 两个日期想减得到天数
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long timeStart, timeEnd, daySecond = 0;
        //得到毫秒数
        try {
            timeStart = startTime.getTime();
            timeEnd = endtime.getTime();
            daySecond = (timeEnd - timeStart) / (1000);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return daySecond;
//两个日期想减得到天数
    }

    public static String getCurrentTime() {
        long time = System.currentTimeMillis();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String str = format.format(new Date(time));
        return str;
    }


    public static void hideSoftKeyboard(Context context) {
        Activity activity = (Activity) context;
        View view = activity.getCurrentFocus();
        if (isActiveSoftKeyboard(context) && view != null) {
            ((InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断键盘是否打开
     *
     * @param @return
     * @return boolean
     * @throws
     * @Title: isActiveSoftKeyboard
     */
    public static boolean isActiveSoftKeyboard(Context context) {
        return ((InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE))
                .isActive();
    }

    public static void switchSystemKeyboard(Context context) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, InputMethodManager.HIDE_IMPLICIT_ONLY);
        }
    }


    public static boolean compareDate(String DATE1, String DATE2) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return true;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return false;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return true;
    }

    public static String generatePassword(int strLength) {

        Random rm = new Random();

        // 获得随机数
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);

        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);

        // 返回固定的长度的随机数
        return fixLenthString.substring(1, strLength + 1);
    }

    public static String getDefaultPassword() {
        return "adf3af34!@#%^&5#$^#$%#4563~!@@#d23e%^5873df32$#*&^*^";
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }


    public static Bitmap Bytes2Bitmap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static void hideKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void showKeyboard(Context context, View view) {
        InputMethodManager imm = (InputMethodManager) context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(view, 0);
    }

    public static String secondsToTimeString(int seconds) {
        String time;
        int minutes = seconds / 60;
        int s = seconds % 60;
        String secondString, minuteString;
        if (s >= 0 && s < 10) {
            secondString = "0" + s;
        } else {
            secondString = "" + s;
        }

        if (minutes >= 0 && minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }
        time = minuteString + ":" + secondString;
        return time;
    }

    public static String millisecondsToTimeString(int milliseconds) {
        String time;
        int minutes = milliseconds / (60 * 1000);
        int seconds = milliseconds / 1000 - minutes * 60;
        String secondString, minuteString;
        if (seconds >= 0 && seconds < 10) {
            secondString = "0" + seconds;
        } else {
            secondString = "" + seconds;
        }

        if (minutes >= 0 && minutes < 10) {
            minuteString = "0" + minutes;
        } else {
            minuteString = "" + minutes;
        }
        time = minuteString + ":" + secondString;
        return time;
    }

//    public static RepeatMode loadRepeatMode(Context context) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("repeatmode", Context.MODE_PRIVATE);
//        return DecodeType.getRepeatMode(sharedPreferences.getInt("repeatMode", RepeatMode.RepeatNone.getCode()));
//    }
//
//    public static void updateRepeatMode(Context context, RepeatMode repeatMode) {
//        SharedPreferences sharedPreferences = context.getSharedPreferences("repeatmode", Context.MODE_PRIVATE);
//        sharedPreferences.edit().putInt("repeatMode", repeatMode.getCode()).apply();
//
//    }

    public static Boolean isRandomPlay(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("israndom", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("random", false);
    }

    public static void updateRandomPlay(Context context, Boolean isRandom) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("israndom", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean("random", isRandom).apply();
    }


    public static void setVersionDownloaded(Context context, String version) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DownloadedVersion", Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(version, true).apply();
    }

    public static boolean isVersionDownloaded(Context context, String version) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("DownloadedVersion", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(version, false);
    }

    public static int dp2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int)(dipValue * m + 0.5f) ;
    }


    public static long getAvailableExternalMemorySize(){

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File path = Environment.getExternalStorageDirectory(); //取得sdcard文件路径
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSize();
            long availableBlocks = stat.getAvailableBlocks();
            //(availableBlocks * blockSize)/1024      KIB 单位
            //(availableBlocks * blockSize)/1024 /1024  MIB单位
            return availableBlocks * blockSize;
        } else {
            return 0;
        }
    }

    public static long getFileListSize(String filePath, String fileType) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        long filesSize = 0;
        for (int j = 0; j < files.length; j++) {
            String name = files[j].getName();
            if (files[j].isDirectory()) {
                String dirPath = files[j].toString().toLowerCase();
                //System.out.println(dirPath);
                //getFileListSize(dirPath + "/");
            } else if (files[j].isFile() & name.endsWith(fileType)) {
                filesSize += files[j].length();
            }
        }
        return filesSize;
    }

    public static String formatFileSize(long fileS)
    {// 转换文件大小
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024)
        {
            fileSizeString = df.format((double) fileS) + "B";
        }
        else if (fileS < 1048576)
        {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        }
        else if (fileS < 1073741824)
        {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        }
        else
        {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }

        return fileSizeString;
    }

    public static String getTimeDescription(long ms) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH 小时 mm 分钟");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+00:00"));
        String hms = formatter.format(ms);
        return hms;
    }
}
