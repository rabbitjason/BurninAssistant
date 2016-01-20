package com.bugsbunny.burninassistant.model;

import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.bean.PlanBean;

import java.util.List;

/**
 * Created by Administrator on 2015/12/10.
 */
public interface IPlanModel {
    public void setDuration(int hour, int minute);
    public void setIntervalTime(int minute);
    public void setRemainingTime(long remainingTime);
    public void setMusicList(List<MusicBean> musicList);
    public PlanBean load();
    public void save();
}
