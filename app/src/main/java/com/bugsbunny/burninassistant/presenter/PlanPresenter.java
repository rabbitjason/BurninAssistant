package com.bugsbunny.burninassistant.presenter;

import android.os.Handler;

import com.bugsbunny.burninassistant.bean.PlanBean;
import com.bugsbunny.burninassistant.manager.PreferenceManager;
import com.bugsbunny.burninassistant.model.IPlanModel;
import com.bugsbunny.burninassistant.model.PlanModel;
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

    public PlanPresenter(IPlanView planView) {
        this.planView = planView;
        planModel = new PlanModel();
    }

    public boolean isPlay() {
        return isPlay;
    }

    public void load() {
       planBean = planModel.load();
        if (planBean != null) {
            int hour = (int)(planBean.getDuration() / PlanModel.HH_MS);
            int minute = (int)(planBean.getDuration() % PlanModel.MM_MS);
            planView.showDuration(hour, minute);

            minute = (int)(planBean.getIntervalTime() / PlanModel.MM_MS);
            planView.showIntervalTime(minute);
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
            } else {
                if (isPlay) {
                    planView.showStatus("休息中...");
                    isPlay = false;
                    remainTime = planBean.getIntervalTime();
                } else {
                    planView.showStatus("煲机中...");
                    remainTime = planBean.getDuration();
                    isPlay = true;
                }

            }
            int hour = (int)(remainTime / PlanModel.HH_MS);
            int minute = (int)((remainTime / PlanModel.SS_MS) - hour * 3600) / 60;
            int second = (int)(remainTime / PlanModel.SS_MS - hour * 3600 - minute * 60);
            planView.showCountdownTime(hour, minute, second);
            handler.postDelayed(this, 1000);
        }
    };

    public void startCountdown() {
        planView.showStatus("煲机中...");
        remainTime = planBean.getRemainingTime();
        isPlay = true;
        handler.postDelayed(runnable, 1000);
    }

    public void stopCountdown() {
        planBean.setRemainingTime(remainTime);
        isPlay = false;
        planView.showStatus("");
        handler.removeCallbacks(runnable);
    }
}
