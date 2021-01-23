package com.liqun.lib_audio.mediaplayer.app;

import android.content.Context;

import com.liqun.lib_audio.mediaplayer.db.GreenDaoHelper;

/**
 * 唯一与外界通信的帮助类
 */
public class AudioHelper {

    // sdk全局context, 供子模块用
    private static Context sContext;

    public static void init(Context context){
        sContext = context;
        GreenDaoHelper.initDatabase();
    }

    public static Context getContext() {
        return sContext;
    }
}
