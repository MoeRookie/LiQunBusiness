package com.liqun.lib_audio.mediaplayer.core;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.text.TextUtils;

import com.liqun.lib_audio.mediaplayer.app.AudioHelper;
import com.liqun.lib_audio.mediaplayer.events.AudioLoadEvent;
import com.liqun.lib_audio.mediaplayer.events.AudioPauseEvent;
import com.liqun.lib_audio.mediaplayer.events.AudioStartEvent;
import com.liqun.lib_audio.mediaplayer.model.AudioBean;
import com.liqun.lib_audio.mediaplayer.view.NotificationHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import androidx.annotation.Nullable;

import static com.liqun.lib_audio.mediaplayer.view.NotificationHelper.NOTIFICATION_ID;

/**
 * 音乐后台服务
 * 并更新notification状态
 */
public class MusicService extends Service
implements NotificationHelper.NotificationHelperListener{
    /**
     * 常量区
     */
    private static String DATA_AUDIOS = "AUDIOS";
    private static String ACTION_START = "ACTION_START";

    /**
     * 数据区
     */
    private ArrayList<AudioBean> mAudioBeans;

    private NotificationReceiver mReceiver;

    /**
     * 外部直接启动service的方法
     * @param audioBeans
     */
    public static void startMusicService(ArrayList<AudioBean> audioBeans){
        Intent intent = new Intent(AudioHelper.getContext(), MusicService.class);
        intent.setAction(ACTION_START);
        // 还需要传list数据进来
        intent.putExtra(DATA_AUDIOS, audioBeans);
        AudioHelper.getContext().startService(intent);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        registerBroadcastReceiver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mAudioBeans = (ArrayList<AudioBean>) intent.getSerializableExtra(DATA_AUDIOS);
        if (ACTION_START.equals(intent.getAction())) {
            playMusic();
            // 初始化前台notification
            NotificationHelper.getInstance().init(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void playMusic() {
        AudioController.getInstance().setQueue(mAudioBeans);
        AudioController.getInstance().play();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        unregisterBroadcastReceiver();
    }

    private void registerBroadcastReceiver() {
        if (null == mReceiver) {
            mReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction(NotificationReceiver.ACTION_STATUS_BAR);
            registerReceiver(mReceiver, filter);
        }
    }

    private void unregisterBroadcastReceiver() {
        if (null != mReceiver) {
            unregisterReceiver(mReceiver);
        }
    }

    @Override
    public void onNotificationInit() {
        // service和notification绑定
        startForeground(NOTIFICATION_ID, NotificationHelper.getInstance().getNotification());
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioLoadEvent(AudioLoadEvent event){
        // 更新notification状态为加载状态
        NotificationHelper.getInstance().showLoadStatus(event.mAudioBean);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioStartEvent(AudioStartEvent event){
        // 更新notification状态为播放状态
        NotificationHelper.getInstance().showPlayStatus();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAudioPauseEvent(AudioPauseEvent event){
        // 更新notification状态为暂停状态
        NotificationHelper.getInstance().showPauseStatus();
    }

    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver extends BroadcastReceiver {
        public static final String ACTION_STATUS_BAR =
                AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (null == intent || TextUtils.isEmpty(intent.getAction())) {
                return;
            }
            String action = intent.getStringExtra(EXTRA);
            switch (action) {
                case EXTRA_PLAY:
                    AudioController.getInstance().playOrPause();
                    break;
                case EXTRA_PRE:
                    AudioController.getInstance().previous();
                    break;
                case EXTRA_NEXT:
                    AudioController.getInstance().next();
                    break;
                case EXTRA_FAV:
                    // 收藏广播处理
                    break;
            }
        }
    }
}
