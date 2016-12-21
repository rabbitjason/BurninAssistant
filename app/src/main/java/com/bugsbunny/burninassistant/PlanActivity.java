package com.bugsbunny.burninassistant;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bugsbunny.burninassistant.model.PlanModel;
import com.bugsbunny.burninassistant.presenter.PlanPresenter;

public class PlanActivity extends BaseActivity implements View.OnClickListener {

    private int maxVolume, currentVolume;
    private SeekBar sbVolume;
    private AudioManager audioManager;
    private View rlPlayOrPause;

    private TextView tvCountdownTime;
    private ImageView ivPlayOrPause;

    private static PlanPresenter planPresenter;

    public static void actionStart(Context context, PlanPresenter pp) {
        planPresenter = pp;
        Intent intent = new Intent(context, PlanActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        initView();

        initVolumeSeekBar();

        if (planPresenter != null) {
            planPresenter.setOnCountDownListener(new PlanPresenter.OnCountDownListener() {
                @Override
                public void onCountDown(long ms) {
                    showCountdownTime(ms);
                }
            });
            planPresenter.startCountdown();
        }

    }

    private void showCountdownTime(long remainTime) {
        int hour = (int)(remainTime / PlanModel.HH_MS);
        int minute = (int)((remainTime / PlanModel.SS_MS) - hour * 3600) / 60;
        int second = (int)(remainTime / PlanModel.SS_MS - hour * 3600 - minute * 60);
        if (tvCountdownTime != null) {
            tvCountdownTime.setText(String.format("%02d:", hour)
                    + String.format("%02d:", minute) + String.format("%02d", second));
        }
    }

    private void initView() {
        rlPlayOrPause = findViewById(R.id.rlPlayOrPause);
        rlPlayOrPause.setOnClickListener(this);

        tvCountdownTime = (TextView) findViewById(R.id.tvCountdownTime);

        ivPlayOrPause = (ImageView) findViewById(R.id.ivPlayOrPause);
    }

    private void initVolumeSeekBar() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        sbVolume = (SeekBar) findViewById(R.id.sbVolume);
        sbVolume.setMax(maxVolume);
        currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        sbVolume.setProgress(currentVolume);
        sbVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, progress, 0);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rlPlayOrPause:
                if (planPresenter.isPlay()) {
                    planPresenter.pauseCountdown();
                    ivPlayOrPause.setBackground(getResources().getDrawable(R.drawable.player_play_small));
                } else {
                    planPresenter.startCountdown();
                    ivPlayOrPause.setBackground(getResources().getDrawable(R.drawable.player_pause_small));
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        if (planPresenter.isPlay()) {
            planPresenter.stopCountdown();
        }
        super.onDestroy();
    }
}
