package com.bugsbunny.burninassistant.manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.GetDataCallback;
import com.avos.avoscloud.ProgressCallback;
import com.bugsbunny.burninassistant.R;
import com.bugsbunny.burninassistant.bean.Update;
import com.bugsbunny.burninassistant.utils.AndroidTools;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by lipple-server on 16/12/29.
 */

public class UpdateManager {
    private static final String TAG = UploadManager.class.getSimpleName();

    private static Context mContext;
    private volatile static UpdateManager singleton;

    private static Update mUpdate;

    private ProgressDialog progressDialog;
    private File appFile;

    private static int MSG_UPDATE_FILE = 0;
    private static int MSG_UPDATE_COMPLETED = 1;

    private UIUpdateFileHandler handlerUpdateFile = new UIUpdateFileHandler();
    class UIUpdateFileHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if (MSG_UPDATE_FILE == msg.what) {
                downloadFile();
            } else if (MSG_UPDATE_COMPLETED == msg.what) {
                if (appFile != null) {
                    installAPK(Uri.fromFile(appFile));
                }
            }
        }
    }

    public static void init(final Context context) {
        mContext = context;

        Update.getTheLastestUpdate(new Update.GetUpdateCallback() {
            @Override
            public void done(Update update, AVException e) {
                if (e == null) {
                    mUpdate = update;
                    getSingleton().checkVersion();
                }
            }
        });
    }

    private UpdateManager (){}

    public static UpdateManager getSingleton() {
        if (singleton == null) {
            synchronized (UploadManager.class) {
                if (singleton == null) {
                    singleton = new UpdateManager();
                }
            }
        }
        return singleton;
    }

    private boolean isNeedUpdate() {
        String v = mUpdate.getVersion(); // 最新版本的版本号
        Log.i(TAG, v);
        if (v.equals(AndroidTools.getVerName(mContext))) {
            return false;
        } else {
            return true;
        }
    }

    public void checkVersion() {

        if ( !isNeedUpdate() ) {
            return;
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle(null);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        final View view = LayoutInflater.from(mContext).inflate(R.layout.download_version_dlg, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        TextView tvDesc = (TextView)view.findViewById(R.id.tvDesc);
        tvDesc.setText(mUpdate.getDescription());
        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setTitle("版本更新");
        builder.setMessage(mUpdate.getDescription());
        //builder.setView(view);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                handlerUpdateFile.sendEmptyMessage(MSG_UPDATE_FILE);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.create().show();
    }

    void downloadFile() {
        AVFile avFile = new AVFile("my_download_file", mUpdate.getUrl(), null);
        progressDialog.show();
        avFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, AVException e) {
                // bytes 就是文件的数据流
                progressDialog.dismiss();
                if (e == null) {
                    //handlerUploadFile.sendEmptyMessage(MSG_UPLOAD_COMPLETED);
                    try {
                        appFile = createCacheFile("burninassis.apk", bytes);
                        handlerUpdateFile.sendEmptyMessage(MSG_UPDATE_COMPLETED);
                    } catch (IOException error) {
                        error.printStackTrace();
                    }
                    Toast.makeText(mContext, "文件下载完成", Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(mContext, "文件下载失败", Toast.LENGTH_LONG).show();
                }
            }
        }, new ProgressCallback() {
            @Override
            public void done(Integer integer) {
                // 上传进度数据，integer 介于 0 和 100。
                String msg = String.format("正在下载文件(%d%%)", integer);
                progressDialog.setMessage(msg);
            }
        });
    }

    public String getDiskCacheDir(Context context) {
        String cachePath = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return cachePath;
    }

    ///data/data/com.bugsbunny.burninassistant/cache/burninassis.apk
    private File createCacheFile(String name, byte[] data) throws IOException {
        File tmpFile = new File(getDiskCacheDir(mContext), name);
        FileOutputStream outputStream = new FileOutputStream(tmpFile);
        outputStream.write(data, 0, data.length);
        outputStream.close();
        return tmpFile;
    }

    /**
     * 安装apk文件
     */
    private void installAPK(Uri apk) {

        // 通过Intent安装APK文件
        Intent intents = new Intent();

        intents.setAction("android.intent.action.VIEW");
        intents.addCategory("android.intent.category.DEFAULT");
        intents.setType("application/vnd.android.package-archive");
        intents.setData(apk);
        intents.setDataAndType(apk,"application/vnd.android.package-archive");
        intents.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        android.os.Process.killProcess(android.os.Process.myPid());
        // 如果不加上这句的话在apk安装完成之后点击单开会崩溃

        mContext.startActivity(intents);

    }
}
