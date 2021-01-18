package com.liqun.liqunbusiness.application;

import android.app.Application;

import com.liqun.lib_audio.mediaplayer.app.AudioHelper;

public class LiQunVoiceApplication extends Application {
    private static LiQunVoiceApplication mApplication = null;
    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;
        // 音频SDK初始化
        AudioHelper.init(this);
    }

    public static LiQunVoiceApplication getInstance(){
        return mApplication;
    }
}
