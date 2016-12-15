package com.bugsbunny.burninassistant;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.IBinder;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bugsbunny.burninassistant.bean.MusicBean;
import com.bugsbunny.burninassistant.bean.PlanBean;
import com.bugsbunny.burninassistant.manager.PreferenceManager;
import com.bugsbunny.burninassistant.model.PlanModel;
import com.bugsbunny.burninassistant.presenter.PlanPresenter;
import com.bugsbunny.burninassistant.services.MusicService;
import com.bugsbunny.burninassistant.utils.AndroidTools;
import com.bugsbunny.burninassistant.view.IPlanView;

import java.text.SimpleDateFormat;

public class MainActivity extends BaseActivity implements IPlanView, View.OnClickListener {
    private View llDuration, llInterval, llMusicMore;
    private WebView myWebView;
    private TextView tvDurationHour,tvDurationMinute,tvIntervalMinute;
    private TextView tvCountdownTime, tvTotalTime, tvLastTime, tvMusicName, tvMusicDetail;
    private PlanPresenter planPresenter;
    private Button btnPlay;

    private static final int SELECTED_MUSIC_RC = 100;

    private MusicService musicService;
    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {

        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            musicService = ((MusicService.MusicBinder)service).getService();
            planPresenter.setMusicService(musicService);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        planPresenter = new PlanPresenter(this);
        initView();
        planPresenter.load();

        Intent intent = new Intent(this, MusicService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }



    private void initView() {
        tvDurationHour = (TextView)findViewById(R.id.tvDurationHour);
        tvDurationMinute = (TextView)findViewById(R.id.tvDurationMinute);
        tvIntervalMinute = (TextView)findViewById(R.id.tvIntervalMinute);

        tvCountdownTime = (TextView)findViewById(R.id.tvCountdownTime);

        tvTotalTime = (TextView) findViewById(R.id.tvTotalTime);
        tvLastTime = (TextView) findViewById(R.id.tvLastTime);

        llDuration = findViewById(R.id.llDuration);
        llDuration.setOnClickListener(this);

        llInterval = findViewById(R.id.llInterval);
        llInterval.setOnClickListener(this);

        llMusicMore = findViewById(R.id.llMusicMore);
        llMusicMore.setOnClickListener(this);

        btnPlay = (Button)findViewById(R.id.btnPlay);
        btnPlay.setOnClickListener(this);

        tvMusicName = (TextView) findViewById(R.id.tvMusicName);
        tvMusicDetail = (TextView) findViewById(R.id.tvMusicDetail);
    }


    private void initWebView() {
        myWebView = (WebView) findViewById(R.id.webView);
        //myWebView.loadUrl("https://ipcrs.pbccrc.org.cn/");// 百度链接
        myWebView.loadUrl("http://baidu.com");
        myWebView.requestFocusFromTouch();
        // JavaScript使能(如果要加载的页面中有JS代码，则必须使能JS)
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        // 更强的打开链接控制：自己覆写一个WebViewClient类：除了指定链接从WebView打开，其他的链接默认打开
        myWebView.setWebViewClient(new MyWebViewClient());
    }

    @Override
    public void showDuration(int hour, int minute) {
        tvDurationHour.setText(String.format("%02d", hour));
        tvDurationMinute.setText(String.format("%02d", minute));
    }

    @Override
    public void showIntervalTime(int minute) {
        tvIntervalMinute.setText(String.format("%02d", minute));
    }

    @Override
    public void showCountdownTime(int hour, int minute, int second) {
        tvCountdownTime.setText(String.format("%02d:", hour)
                + String.format("%02d:", minute) + String.format("%02d", second));
    }

    @Override
    public void showStatus(String status) {

    }

    @Override
    public void showMusic(MusicBean mb) {
        tvMusicName.setText(mb.getName());
        SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");//初始化Formatter的转换格式。
        String hms = formatter.format(mb.getLength());
        String detail = "";
        if (mb.getArtist() != null && mb.getAlbum() != null) {
            detail += mb.getArtist() + " - " + mb.getAlbum();
        } else if (mb.getArtist() != null) {
            detail = mb.getArtist();
        } else if (mb.getAlbum() != null) {
            detail = mb.getAlbum();
        }
        tvMusicDetail.setText(hms + " " + detail);
    }

    @Override
    public void showTotalTime(long ms) {
        tvTotalTime.setText(AndroidTools.getTimeDescription(ms));
    }

    @Override
    public void showLastTime(long tm) {
        tvLastTime.setText(AndroidTools.getTimeDescription(tm));
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        int hour;
        int minute;
        switch (v.getId()) {
            case R.id.llDuration:
                hour = Integer.parseInt(tvDurationHour.getText().toString());
                minute = Integer.parseInt(tvDurationMinute.getText().toString());
                TimeSelectorDialog.actionStart(this, "煲机时长", hour, minute, TimeSelectorDialog.REQUEST_DURATION);
                break;
            case R.id.llInterval:
                minute = Integer.parseInt(tvIntervalMinute.getText().toString());
                TimeSelectorDialog.actionStart(this, "间隔周期", 0, minute, TimeSelectorDialog.REQUEST_INTERVAL);
                break;
            case R.id.btnPlay:
                if (planPresenter.isPlay()) {
                    planPresenter.stopCountdown();
                    btnPlay.setText("开始");
                } else {
                    planPresenter.startCountdown();
                    btnPlay.setText("停止");
                }
                break;
            case R.id.llMusicMore:
                MusicActivity.actionStart(this, SELECTED_MUSIC_RC);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onDestroy() {

        planPresenter.save();
        unbindService(conn);

        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (TimeSelectorDialog.REQUEST_DURATION == requestCode) {
            if (RESULT_OK == resultCode) {
                if (data != null) {
                    int hour = data.getIntExtra(TimeSelectorDialog.HH, 0);
                    int minute = data.getIntExtra(TimeSelectorDialog.MM, 0);
                    planPresenter.setDuration(hour, minute);
                }
            }
        } else if (TimeSelectorDialog.REQUEST_INTERVAL == requestCode) {
            if (RESULT_OK == resultCode) {
                if (data != null) {
                    int minute = data.getIntExtra(TimeSelectorDialog.MM, 0);
                    planPresenter.setIntervalTime(minute);
                }
            }
        } else if (SELECTED_MUSIC_RC == requestCode) {

            showMusic(MusicActivity.stMusic);

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

        @Override
        public boolean shouldOverrideKeyEvent(WebView view, KeyEvent event) {

            return super.shouldOverrideKeyEvent(view, event);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            //handler.cancel(); // Android默认的处理方式
            //handler.proceed();  // 接受所有网站的证书
            //handleMessage(Message msg); // 进行其他处理
            super.onReceivedSslError(view, handler, error);
        }
    }
}
