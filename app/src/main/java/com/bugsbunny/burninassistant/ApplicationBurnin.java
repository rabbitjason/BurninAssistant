package com.bugsbunny.burninassistant;

import android.app.Application;

/**
 * Created by Administrator on 2015/12/10.
 */
public class ApplicationBurnin extends Application {

    public static ApplicationBurnin instance;
    private static ApplicationBurnin application = null;

    @Override
    public void onCreate() {
        super.onCreate();

        instance = ApplicationBurnin.this;
    }

    public static ApplicationBurnin getInstance() {
        return instance;
    }
}
