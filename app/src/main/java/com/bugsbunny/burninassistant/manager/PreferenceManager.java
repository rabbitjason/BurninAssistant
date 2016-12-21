package com.bugsbunny.burninassistant.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.bugsbunny.burninassistant.ApplicationBurnin;
import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.utils.AndroidTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by lipple-server on 16/12/8.
 */

public class PreferenceManager {

    static SharedPreferences sharedPreferences;

    static final String DURATION_KEY = "duration";
    static final String INTERVAL_TIME = "intervalTime";
    static final String REMAINING_TIME = "remainingTime";
    static final String TOTAL_TIME = "totalTime";
    static final String LAST_TIME = "lastTime";
    static final String BURNIN_MUSICS = "burningMusics";

    public static final long HH_MS = 60 * 60 * 1000;
    public static final long MM_MS = 60 * 1000;
    public static final long SS_MS = 1 * 1000;


    private static SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null)
            sharedPreferences = ApplicationBurnin.getInstance().getSharedPreferences(
                    "BURN_PREF", Context.MODE_PRIVATE);
        return sharedPreferences;
    }

    public static void saveDuration(long duration) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(DURATION_KEY, duration);
        editor.apply();
    }

    public static long getDuration() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(DURATION_KEY, 4 * HH_MS);
    }

    public static void saveIntervalTime(long intervalTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(INTERVAL_TIME, intervalTime);
        editor.apply();
    }

    public static long getIntervalTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(INTERVAL_TIME, 15 * MM_MS);
    }

    public static void saveRemainingTime(long remainingTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(REMAINING_TIME, remainingTime);
        editor.apply();
    }

    public static long getRemainingTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(REMAINING_TIME, 4 * HH_MS);
    }

    public static void saveTotalTime(long totalTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TOTAL_TIME, totalTime);
        editor.apply();
    }

    public static long getTotalTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(TOTAL_TIME, 0);
    }

    public static void saveLastTime(long lastTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME, lastTime);
        editor.apply();
    }

    public static long getLastTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(LAST_TIME, 0);
    }

    public static List<MusicBean> getMusics() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        Set<String> jsonSet = sharedPreferences.getStringSet(BURNIN_MUSICS, null);
        List<MusicBean> musicList = new ArrayList<MusicBean>();
        if (jsonSet != null) {
            for (String json : jsonSet) {
                MusicBean m = MusicBean.objectFromJSONString(json);
                musicList.add(m);
            }
        } else {
            MusicBean musicWhite = new MusicBean();
            musicWhite.setName("白噪音");
            musicWhite.setAlbum("煲机助手");
            musicWhite.setUrl("white.mp3");
            musicWhite.setIsAssetType(true);
            musicWhite.setSelected(false);
            AndroidTools.getMP3MetaData(ApplicationBurnin.getInstance(), musicWhite);
            musicList.add(musicWhite);

            MusicBean musicRed = new MusicBean();
            musicRed.setName("粉红噪音");
            musicRed.setAlbum("煲机助手");
            musicRed.setUrl("red.mp3");
            musicRed.setIsAssetType(true);
            musicRed.setSelected(true);
            AndroidTools.getMP3MetaData(ApplicationBurnin.getInstance(), musicRed);
            musicList.add(musicRed);
        }
        return musicList;
    }

    public static void saveMusics(List<MusicBean> mbs) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Set<String> jsonSet = new HashSet<String>();
        for (MusicBean m : mbs) {
            jsonSet.add(m.toJSONString());
        }
        editor.putStringSet(BURNIN_MUSICS, jsonSet);
        editor.apply();
    }
}
