package com.bugsbunny.burninassistant.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.bugsbunny.burninassistant.ApplicationBurnin;

/**
 * Created by lipple-server on 16/12/8.
 */

public class PreferenceManager {

    static SharedPreferences sharedPreferences;

    static String DURATION_KEY = "duration";
    static String INTERVAL_TIME = "intervalTime";
    static String REMAINING_TIME = "remainingTime";
    static String TOTAL_TIME = "totalTime";
    static String LAST_TIME = "lastTime";


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
        editor.commit();
    }

    public static long getDuration() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(DURATION_KEY, 0);
    }

    public static void saveIntervalTime(long intervalTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(INTERVAL_TIME, intervalTime);
    }

    public static long getIntervalTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(INTERVAL_TIME, 0);
    }

    public static void saveRemainingTime(long remainingTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(REMAINING_TIME, remainingTime);
    }

    public static long getRemainingTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(REMAINING_TIME, 0);
    }

    public static void saveTotalTime(long totalTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(TOTAL_TIME, totalTime);
    }

    public static long getTotalTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(TOTAL_TIME, 0);
    }

    public static void saveLastTime(long lastTime) {
        SharedPreferences sharedPreferences = getSharedPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putLong(LAST_TIME, lastTime);
    }

    public static long getLastTime() {
        SharedPreferences sharedPreferences = getSharedPreferences();
        return sharedPreferences.getLong(LAST_TIME, 0);
    }
}
