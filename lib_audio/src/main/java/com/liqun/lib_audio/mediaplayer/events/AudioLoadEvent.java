package com.liqun.lib_audio.mediaplayer.events;

import com.liqun.lib_audio.mediaplayer.model.AudioBean;

public class AudioLoadEvent {
    public AudioBean mAudioBean;

    public AudioLoadEvent(AudioBean audioBean) {
        this.mAudioBean = audioBean;
    }
}
