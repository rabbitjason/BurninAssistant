package com.bugsbunny.burninassistant.manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.ProgressCallback;
import com.avos.avoscloud.SaveCallback;
import com.bugsbunny.burninassistant.R;
import com.bugsbunny.burninassistant.bean.Update;

import java.lang.reflect.Field;

/**
 * Created by lipple-server on 16/12/27.
 */

public class UploadManager {

    private static final String TAG = UploadManager.class.getSimpleName();

    private static Context mContext;
    private volatile static UploadManager singleton;

    private Update update;
    private ProgressDialog progressDialog;

    private static int MSG_UPLOAD_FILE = 0;
    private static int MSG_UPLOAD_COMPLETED = 1;

    private UIUploadFileHandler handlerUploadFile = new UIUploadFileHandler();
    class UIUploadFileHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            //super.handleMessage(msg);
            if (MSG_UPLOAD_FILE == msg.what) {
                uploadingFile((String)msg.obj);
            } else if (MSG_UPLOAD_COMPLETED == msg.what) {
                saveUpdateInfo();
            }
        }
    }

    public static void init(final Context context) {
        mContext = context;
    }

    private UploadManager (){}

    public static UploadManager getSingleton() {
        if (singleton == null) {
            synchronized (UploadManager.class) {
                if (singleton == null) {
                    singleton = new UploadManager();
                }
            }
        }
        return singleton;
    }

    public void startUploadVideoFile(final String uploadFilename) {
        if (update == null) {
            update  = new Update();
        }

        if (progressDialog == null) {
            progressDialog = new ProgressDialog(mContext);
            progressDialog.setTitle(null);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }
        final View view = LayoutInflater.from(mContext).inflate(R.layout.upload_version_dlg, null);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        builder.setIcon(android.R.drawable.ic_menu_info_details);
        builder.setView(view);       //将EditText添加到builder中
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                EditText etVer = (EditText)view.findViewById(R.id.etVer);
                EditText etDesc = (EditText)view.findViewById(R.id.etDesc);
                String desc = etDesc.getText().toString().trim();
                String name = etVer.getText().toString().trim();
                if (name.length() > 0) {
                    try
                    {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        //设置mShowing值，欺骗android系统
                        field.set(dialog, true);
                    }catch(Exception e) {
                        e.printStackTrace();
                    }
                    update.setVersion(name);
                    update.setDescription(desc);
                    android.os.Message msg = new Message();
                    msg.obj = uploadFilename;
                    msg.what = MSG_UPLOAD_FILE;
                    handlerUploadFile.sendMessage(msg);
                } else {
                    try
                    {
                        Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                        field.setAccessible(true);
                        //设置mShowing值，欺骗android系统
                        field.set(dialog, false);
                    }catch(Exception e) {
                        e.printStackTrace();
                    }

                }

            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try
                {
                    Field field = dialog.getClass().getSuperclass().getDeclaredField("mShowing");
                    field.setAccessible(true);
                    //设置mShowing值，欺骗android系统
                    field.set(dialog, true);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
        });
        builder.create().show();
    }

    private void uploadingFile(final String filePath) {

        try {
            final AVFile file = AVFile.withAbsoluteLocalPath("teamUploadVideo.mp4", filePath);
            progressDialog.show(mContext, "", "正在上传文件(0%)");
            file.saveInBackground(new SaveCallback() {
                @Override
                public void done(AVException e) {
                    progressDialog.dismiss();
                    if (e == null) {
                        update.setUrl(file.getUrl());
                        handlerUploadFile.sendEmptyMessage(MSG_UPLOAD_COMPLETED);
                    } else {
                        Toast.makeText(mContext, "文件上传失败", Toast.LENGTH_LONG).show();
                    }
                }
            }, new ProgressCallback() {
                @Override
                public void done(Integer integer) {
                    String msg = String.format("正在上传文件(%d%%)", integer);
                    progressDialog.setMessage(msg);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void saveUpdateInfo() {
        update.saveEventually(new SaveCallback() {
            @Override
            public void done(AVException e) {
                if (null == e) {
                    Toast.makeText(mContext, "文件上传完毕", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mContext, "文件上传失败", Toast.LENGTH_LONG).show();
                }
                update = null;
            }
        });
    }

}
