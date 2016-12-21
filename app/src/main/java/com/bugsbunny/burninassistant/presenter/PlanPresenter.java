package com.bugsbunny.burninassistant.presenter;

import android.os.Handler;

import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.bean.PlanBean;
import com.bugsbunny.burninassistant.manager.PreferenceManager;
import com.bugsbunny.burninassistant.model.IPlanModel;
import com.bugsbunny.burninassistant.model.PlanModel;
import com.bugsbunny.burninassistant.services.MusicService;
import com.bugsbunny.burninassistant.view.IPlanView;

/**
 * Created by Administrator on 2015/12/10.
 */
public class PlanPresenter {
    private IPlanView planView;
    private IPlanModel planModel;

    private long remainTime = 0;
    private PlanBean planBean;
    private boolean isPlay = false;
    private boolean isPause = false;

    private MusicService musicService;

    private OnCountDownListener onCountDownListener;

    public interface OnCountDownListener {
        void onCountDown(long ms);
    }

    public void setOnCountDownListener(
            OnCountDownListener onCountDownListener) {
        this.onCountDownListener = onCountDownListener;
    }

    public PlanPresenter(IPlanView planView) {
        this.planView = planView;
        planModel = new PlanModel();
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void setMusicService(MusicService ms) {
        this.musicService = ms;
        ms.setMusic(planBean.getSelectedMusic());
    }

    public void load() {
       planBean = planModel.load();
        if (planBean != null) {
            int hour = (int)(planBean.getDuration() / PlanModel.HH_MS);
            int minute = (int)(planBean.getDuration() % PlanModel.MM_MS);
            planView.showDuration(hour, minute);

            minute = (int)(planBean.getIntervalTime() / PlanModel.MM_MS);
            planView.showIntervalTime(minute);

            MusicBean ms = planBean.getSelectedMusic();
            if (ms != null) {
                planView.showMusic(ms);
            }

            planView.showLastTime(planBean.getLastTime());
            planView.showTotalTime(planBean.getTotalTime());
        }

    }

    public void save() {
        planModel.save();
    }

    public void setDuration(int hour, int minute) {
        planModel.setDuration(hour, minute);
        planView.showDuration(hour, minute);
    }

    public void setIntervalTime(int minute) {
        planModel.setIntervalTime(minute);
        planView.showIntervalTime(minute);
    }

    Handler handler = new Handler();
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (remainTime > 0) {
                remainTime -= PlanModel.SS_MS;
                //planView.showTotalTime(planBean.getTotalTime());

                int hour = (int)(remainTime / PlanModel.HH_MS);
                int minute = (int)((remainTime / PlanModel.SS_MS) - hour * 3600) / 60;
                int second = (int)(remainTime / PlanModel.SS_MS - hour * 3600 - minute * 60);
                //planView.showCountdownTime(hour, minute, second);
                if (onCountDownListener != null) {
                    onCountDownListener.onCountDown(remainTime);
                }
            } else {
                if (isPlay) {
                    isPlay = false;
                    remainTime = planBean.getIntervalTime();
                } else {
                    remainTime = planBean.getDuration();
                    isPlay = true;
                }
            }

            handler.postDelayed(this, 1000);
        }
    };

    public void startCountdown() {
        if (isPause) {
            remainTime = planBean.getRemainingTime();
        } else {
            remainTime = planBean.getDuration();
            isPause = false;
        }
        isPlay = true;
        handler.postDelayed(runnable, 1000);
        if (musicService != null) {
            musicService.playOrPause();
        }
    }

    public void pauseCountdown() {
        isPlay = false;
        isPause = true;
        handler.removeCallbacks(runnable);
        if (musicService != null) {
            musicService.playOrPause();
        }
        planBean.setRemainingTime(remainTime);
    }


    public void stopCountdown() {
        isPlay = false;
        handler.removeCallbacks(runnable);
        if (musicService != null) {
            musicService.playOrPause();
        }
        planBean.setRemainingTime(remainTime);
        planBean.setLastTime(planBean.getDuration()-remainTime);
        long total = planBean.getTotalTime() + planBean.getLastTime();
        planBean.setTotalTime(total);

        planView.showLastTime(planBean.getLastTime());
        planView.showTotalTime(planBean.getTotalTime());
    }
}
