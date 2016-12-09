package com.bugsbunny.burninassistant.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.bugsbunny.burninassistant.bean.MusicBean;

import java.io.IOException;

/**
 * Created by lipple-server on 16/12/9.
 */

public class MusicService extends Service {

    private MediaPlayer mediaPlayer;
    private boolean playerPrepared = false;
    private boolean playing = false;
    private static MusicBean music = null;
    private MusicBean playingMusic = null;


    private static Context context;

    public static void init(Context ctx) {
        context = ctx;
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
    }

    public static void setMusic(MusicBean mus) {
        music = mus;
    }

    private void initMediaPlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {

            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                playerPrepared = true;
                if (playing) {
                    mediaPlayer.start();
                }
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
//                playNext();
            }
        });
    }

    public void playOrPause() {
        if (mediaPlayer == null) {
            return;
        }
        playing = !playing;
        if (playing) {
            play();
        } else {
            pause();
        }
//        handler.sendEmptyMessage(1);
    }

    private void play() {
        if (playingMusic != null && music.getUrl().equals(playingMusic.getUrl())) {
            if (playerPrepared && !mediaPlayer.isPlaying()) {
                mediaPlayer.start();;
            }
            return;
        }
        playingMusic = music;
        try {
            playerPrepared = false;
            mediaPlayer.reset();
            mediaPlayer.setDataSource(music.getUrl());
            mediaPlayer.prepareAsync();
            //handler.sendEmptyMessage(0);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pause() {
        playing = false;
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        try {
            initMediaPlayer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {

        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class MusicBinder extends Binder {
        /**
         * 获取当前Service的实例
         * @return
         */
        public MusicService getService(){
            return MusicService.this;
        }
    }

}
