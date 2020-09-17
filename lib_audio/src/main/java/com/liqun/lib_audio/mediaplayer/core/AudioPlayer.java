package com.liqun.lib_audio.mediaplayer.core;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;

import com.liqun.lib_audio.mediaplayer.app.AudioHelper;

/**
 * 1、播放音频
 * 2、对外发送各种类型的事件
 */
public class AudioPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, AudioFocusManager.AudioFocusListener{

    private static final String TAG = AudioPlayer.class.getSimpleName();
    private static final int TIME_MSG = 0x01;
    private static final int TIME_INVAL = 100;

    // 真正负责音频播放
    private CustomMediaPlayer mMediaPlayer;
    private WifiManager.WifiLock mWifiLock;
    private AudioFocusManager mAudioFocusManager;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case TIME_MSG:
                    break;
            }
        }
    };

    public AudioPlayer() {
        init();
    }

    // 初始化
    private void init() {
        mMediaPlayer = new CustomMediaPlayer();
        mMediaPlayer.setWakeMode(null, PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnBufferingUpdateListener(this);
        mMediaPlayer.setOnErrorListener(this);
        // 初始化WifiLock
        mWifiLock = ((WifiManager) AudioHelper.getContext().getApplicationContext()
                .getSystemService(Context.WIFI_SERVICE)).createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        mAudioFocusManager = new AudioFocusManager(AudioHelper.getContext(), this);
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {

    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {

    }

    @Override
    public void audioFocusGrant() {

    }

    @Override
    public void audioFocusLoss() {

    }

    @Override
    public void audioFocusLossTransient() {

    }

    @Override
    public void audioFocusLossDuck() {

    }
}
