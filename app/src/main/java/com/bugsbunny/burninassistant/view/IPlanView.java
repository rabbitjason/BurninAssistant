package com.bugsbunny.burninassistant.view;

import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.bean.PlanBean;

import java.util.List;

/**
 * Created by Administrator on 2015/12/10.
 */
public interface IPlanView {
    public void showDuration(int hour, int minute);
    public void showIntervalTime(int minute);
    public void showCountdownTime(int hour, int minute, int second);
    public void showStatus(String status);
}
