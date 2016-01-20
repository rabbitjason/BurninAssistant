package com.bugsbunny.burninassistant;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

/**
 * Created by Administrator on 2015/12/4.
 */
public class TimeSelector extends LinearLayout {

    public TimeSelector(Context context) {
        this(context, null);
    }

    public TimeSelector(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.time_selector, this, true);
    }
}
