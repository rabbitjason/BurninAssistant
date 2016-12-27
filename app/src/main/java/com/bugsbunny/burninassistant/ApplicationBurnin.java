package com.bugsbunny.burninassistant;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.services.MusicService;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ApplicationBurnin extends Application {

    public static ApplicationBurnin instance;
    private static ApplicationBurnin application = null;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化参数依次为 this, AppId, AppKey
        AVOSCloud.initialize(this, "lOsbDV5wP9IDE2NekFVwWvyN-gzGzoHsz", "pzfSlIKV07xKr6t7ijp6KktG");

        instance = ApplicationBurnin.this;

        MusicService.init(this);
    }

    public static ApplicationBurnin getInstance() {
        return instance;
    }
}
