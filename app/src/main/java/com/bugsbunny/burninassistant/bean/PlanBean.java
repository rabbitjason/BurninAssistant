package com.bugsbunny.burninassistant.bean;

import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/12/10.
 */
public class PlanBean {

    private long duration;
    private long intervalTime;
    private long remainingTime;
    private long totalTime;
    private long lastTime;
    private List<MusicBean> musicList;

    public long getTotalTime() {
        return totalTime;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public long getLastTime() {
        return lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getIntervalTime() {
        return intervalTime;
    }

    public void setIntervalTime(long intervalTime) {
        this.intervalTime = intervalTime;
    }

    public long getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(long remainingTime) {
        this.remainingTime = remainingTime;
    }

    public List<MusicBean> getMusicList() {
        return musicList;
    }

    public void setMusicList(List<MusicBean> musicList) {
        this.musicList = musicList;
    }

    public MusicBean getSelectedMusic() {

        for (MusicBean m : musicList) {
            if (m.getSelected()) {
                return m;
            }
        }
        return null;
    }
}
