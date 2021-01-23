package com.liqun.lib_audio.mediaplayer.view;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.RemoteViews;

import com.liqun.lib_audio.R;
import com.liqun.lib_audio.mediaplayer.app.AudioHelper;
import com.liqun.lib_audio.mediaplayer.core.AudioController;
import com.liqun.lib_audio.mediaplayer.core.MusicService;
import com.liqun.lib_audio.mediaplayer.model.AudioBean;
import com.liqun.lib_image_loader.app.ImageLoaderManager;

import androidx.core.app.NotificationCompat;

/**
 * 音乐Notification帮助类
 * 01. 完成Notification的创建和初始化
 * 02. 对外提供更新notification的方法
 *
 * todo -> android9.0通知适配 https://blog.csdn.net/guolin_blog/article/details/79854070
 */
public class NotificationHelper {

    public static final String CHANNEL_ID = "channel_id_audio";
    public static final String CHANNEL_NAME = "channel_name_audio";
    public static final int NOTIFICATION_ID = 0x111;

    /**
     * UI相关
     *
     * 最终的Notification显示类
     *
     * todo -> android remoteViews
     */
    private Notification mNotification;
    private RemoteViews mRemoteViews; // 大布局
    private RemoteViews mSmallRemoteViews; //小布局
    private NotificationManager mNotificationManager;
    private NotificationHelperListener mListener;
    private String packageName;

    /**
     * 当前要播的歌曲bean
     */
    private AudioBean mAudioBean;

    public static NotificationHelper getInstance() {
        return SingletonHolder.instance;
    }

    private static class SingletonHolder {
        private static NotificationHelper instance = new NotificationHelper();
    }

    public void init(NotificationHelperListener listener){
        mNotificationManager = (NotificationManager) AudioHelper.getContext()
                .getSystemService(Context.NOTIFICATION_SERVICE);
        packageName = AudioHelper.getContext().getPackageName();
        mAudioBean = AudioController.getInstance().getNowPlaying();
        initNotification();
        mListener = listener;
        if (null != mListener) mListener.onNotificationInit();
    }

    /**
     * 创建Notification
     */
    private void initNotification() {
        if (null == mNotification) {
            // 首先创建布局
            initRemoteViews();
            // 再构建notification
            Intent intent = new Intent(AudioHelper.getContext(), MusicPlayerActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(AudioHelper.getContext(), 0, intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            //适配安卓8.0的消息渠道
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel =
                        new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
                channel.enableLights(false);
                channel.enableVibration(false);
                mNotificationManager.createNotificationChannel(channel);
            }
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(AudioHelper.getContext(), CHANNEL_ID).setContentIntent(
                            pendingIntent)
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setCustomBigContentView(mRemoteViews) //大布局
                            .setContent(mSmallRemoteViews); //正常布局，两个布局可以切换
            mNotification = builder.build();
        }
    }

    public Notification getNotification(){
        return mNotification;
    }

    /**
     * 创建notification的布局, 默认为loading状态
     */
    private void initRemoteViews() {
        int layoutId = R.layout.notification_big_layout;
        mRemoteViews = new RemoteViews(packageName, layoutId);
        mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);

        int smallLayoutId = R.layout.notification_small_layout;
        mSmallRemoteViews = new RemoteViews(packageName, smallLayoutId);
        mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
        mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
        // 点击播放按钮广播
        Intent playIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        playIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PLAY);
        PendingIntent playPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 1, playIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.play_view, playPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        //点击上一首按钮广播
        Intent previousIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        previousIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_PRE);
        PendingIntent previousPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 2, previousIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.previous_view, previousPendingIntent);
        mRemoteViews.setImageViewResource(R.id.previous_view, R.mipmap.note_btn_pre_white);
        //点击下一首按钮广播
        Intent nextIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        nextIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_NEXT);
        PendingIntent nextPendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 3, nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);
        mSmallRemoteViews.setOnClickPendingIntent(R.id.next_view, nextPendingIntent);
        mSmallRemoteViews.setImageViewResource(R.id.next_view, R.mipmap.note_btn_next_white);
        //点击收藏按钮广播
        Intent favouriteIntent = new Intent(MusicService.NotificationReceiver.ACTION_STATUS_BAR);
        favouriteIntent.putExtra(MusicService.NotificationReceiver.EXTRA,
                MusicService.NotificationReceiver.EXTRA_FAV);
        PendingIntent favouritePendingIntent =
                PendingIntent.getBroadcast(AudioHelper.getContext(), 4, favouriteIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT);
        mRemoteViews.setOnClickPendingIntent(R.id.favourite_view, favouritePendingIntent);
    }

    /**
     * 显示notification的加载状态
     */
    public void showLoadStatus(AudioBean bean){
        // 防止空指针crash
        mAudioBean = bean;
        if (null != mRemoteViews) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(), mRemoteViews,
                            R.id.image_view, mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
        }

        // 小布局也要更新
        if (null != mSmallRemoteViews) {
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
            mSmallRemoteViews.setTextViewText(R.id.title_view, mAudioBean.name);
            mSmallRemoteViews.setTextViewText(R.id.tip_view, mAudioBean.album);
            ImageLoaderManager.getInstance()
                    .displayImageForNotification(AudioHelper.getContext(), mSmallRemoteViews,
                            R.id.image_view,mNotification, NOTIFICATION_ID, mAudioBean.albumPic);
            mNotificationManager.notify(NOTIFICATION_ID, mNotification);
        }
    }

    /**
     * 显示notification的播放状态
     */
    public void showPlayStatus(){
        if (null != mRemoteViews) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
        }
        if (null != mSmallRemoteViews) {
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_pause_white);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }

    /**
     * 显示notification的暂停状态
     */
    public void showPauseStatus(){
        if (null != mRemoteViews) {
            mRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        }
        if (null != mSmallRemoteViews) {
            mSmallRemoteViews.setImageViewResource(R.id.play_view, R.mipmap.note_btn_play_white);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mNotification);
    }


    /**
     * 与音乐service的回调通信
     */
    public interface NotificationHelperListener {
        void onNotificationInit();
    }
}
