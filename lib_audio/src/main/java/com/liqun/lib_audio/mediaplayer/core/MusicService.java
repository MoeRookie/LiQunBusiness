package com.liqun.lib_audio.mediaplayer.core;

import com.liqun.lib_audio.mediaplayer.app.AudioHelper;

public class MusicService {
    /**
     * 接收Notification发送的广播
     */
    public static class NotificationReceiver {
        public static final String ACTION_STATUS_BAR =
                AudioHelper.getContext().getPackageName() + ".NOTIFICATION_ACTIONS";
        public static final String EXTRA = "extra";
        public static final String EXTRA_PLAY = "play_pause";
        public static final String EXTRA_NEXT = "play_next";
        public static final String EXTRA_PRE = "play_previous";
        public static final String EXTRA_FAV = "play_favourite";
    }
}
