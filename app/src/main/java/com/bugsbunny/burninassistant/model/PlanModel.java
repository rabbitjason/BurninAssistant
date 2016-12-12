package com.bugsbunny.burninassistant.model;

import android.content.Context;
import android.content.SharedPreferences;

import com.bugsbunny.burninassistant.ApplicationBurnin;
import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.bean.PlanBean;
import com.bugsbunny.burninassistant.manager.PreferenceManager;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Administrator on 2015/12/10.
 */
public class PlanModel implements IPlanModel {
    private PlanBean planBean;
    //Parameters for Plan
    private static final String PLAN_PREF = "PLAN_PREF";
    private static final String DURATION = "DURATION";
    private static final String INTERVAL_TIME = "INTERVAL_TIME";
    private static final String REMAINING_TIME = "REMAINING_TIME";
    private static final String BURNIN_MUSICS = "BURNIN_MUSICS";

    public static final long HH_MS = 60 * 60 * 1000;
    public static final long MM_MS = 60 * 1000;
    public static final long SS_MS = 1 * 1000;

    @Override
    public void setDuration(int hour, int minute) {
        long duration = hour * HH_MS + minute * MM_MS;
        planBean.setDuration(duration);
    }

    @Override
    public void setIntervalTime(int minute) {
        long intervalTime = minute * MM_MS;
        planBean.setIntervalTime(intervalTime);
    }

    @Override
    public void setRemainingTime(long remainingTime) {
        planBean.setRemainingTime(remainingTime);
    }

    @Override
    public void setMusicList(List<MusicBean> musicList) {
        planBean.setMusicList(musicList);
    }

    @Override
    public PlanBean load() {
        if (null == planBean) {
            planBean = new PlanBean();
        }
        planBean.setDuration(PreferenceManager.getDuration());
        planBean.setIntervalTime(PreferenceManager.getIntervalTime());
        planBean.setRemainingTime(PreferenceManager.getRemainingTime());
        planBean.setMusicList(PreferenceManager.getMusics());



//        SharedPreferences accessPref =
//                ApplicationBurnin.getInstance().getSharedPreferences(PLAN_PREF, Context.MODE_PRIVATE);
//
//        planBean.setDuration(accessPref.getLong(DURATION, 4 * HH_MS));
//        planBean.setIntervalTime(accessPref.getLong(INTERVAL_TIME, 15 * MM_MS));
//        planBean.setRemainingTime(accessPref.getLong(REMAINING_TIME, 4 * HH_MS));
//
//        Set<String> jsonSet = accessPref.getStringSet(BURNIN_MUSICS, null);
//        List<MusicBean> musicList = new ArrayList<MusicBean>();
//        if (jsonSet != null) {
//            for (String json : jsonSet) {
//                MusicBean m = MusicBean.objectFromJSONString(json);
////                if (m != null) {
////                    File file = new File(buildOfflinePath(m.getObjectId()));
////                    if (file.exists()) {
////                        musicList.add(m);
////                    }
////                }
//                musicList.add(m);
//            }
//            planBean.setMusicList(musicList);
//        }

        return planBean;
    }

    @Override
    public void save() {
//        SharedPreferences accessPref =
//                ApplicationBurnin.getInstance().getSharedPreferences(PLAN_PREF, Context.MODE_PRIVATE);
//        SharedPreferences.Editor accessEditor = accessPref.edit();
//        Set<String> jsonSet = new HashSet<String>();
//        for (MusicBean m : planBean.getMusicList()) {
//            jsonSet.add(m.toJSONString());
//        }
//        accessEditor.putStringSet(BURNIN_MUSICS, jsonSet);
//        accessEditor.putLong(DURATION, planBean.getDuration());
//        accessEditor.putLong(INTERVAL_TIME, planBean.getIntervalTime());
//        accessEditor.putLong(REMAINING_TIME, planBean.getRemainingTime());
//        accessEditor.apply();
        PreferenceManager.saveDuration(planBean.getDuration());
        PreferenceManager.saveRemainingTime(planBean.getRemainingTime());
        PreferenceManager.saveIntervalTime(planBean.getIntervalTime());
        PreferenceManager.saveMusics(planBean.getMusicList());
    }


}
