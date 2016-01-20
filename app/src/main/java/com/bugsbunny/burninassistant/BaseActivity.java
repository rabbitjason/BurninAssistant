package com.bugsbunny.burninassistant;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.bugsbunny.burninassistant.utils.AndroidTools;

/**
 * Created by Administrator on 2015/12/4.
 */
public class BaseActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidTools.setInvisibleTitle(this);
        Log.d("BaseActivity", getClass().getSimpleName());
    }

}
