package com.liqun.lib_audio.mediaplayer.core;

import com.liqun.lib_audio.mediaplayer.events.AudioPlayModeEvent;
import com.liqun.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.liqun.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;

/**
 * 控制播放逻辑类
 */
public class AudioController {
    /**
     * 播放方式
     */
    public enum PlayMode{
        /**
         * 列表循环
         */
        LOOP,
        /**
         * 随机
         */
        RANDOM,
        /**
         * 单曲循环
         */
        REPEAT
    }

    private static class SingletonHolder{
        private static AudioController instance = new AudioController();
    }

    public static AudioController getInstance(){
        return SingletonHolder.instance;
    }

    private AudioPlayer mAudioPlayer; // 核心播放器
    private ArrayList<AudioBean> mQueue; // 歌曲队列
    private int mQueueIndex;  // 当前播放歌曲索引
    private PlayMode mPlayMode; // 循环模式

    private AudioController(){
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    public ArrayList<AudioBean> getQueue(){
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    /**
     * 设置播放队列
     * @param queue
     */
    public void setQueue(ArrayList<AudioBean> queue){
        mQueue = queue;
    }

    public void setQueue(ArrayList<AudioBean> queue, int queueIndex){
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    public PlayMode getPlayMode(){
        return mPlayMode;
    }

    /**
     * 对外提供, 设置播放模式
     * @param playMode
     */
    public void setPlayMode(PlayMode playMode) {
        mPlayMode = playMode;
        // 还要对外发送切换事件, 更新UI
        EventBus.getDefault().post(new AudioPlayModeEvent(mPlayMode));
    }

    public void setPlayIndex(int index){
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空,请先设置播放队列.");
        }
        mQueueIndex = index;
        play();
    }

    public int getPlayIndex(){
        return mQueueIndex;
    }

    /**
     * 对外提供的play方法
     */
    private void play() {
        AudioBean bean = getNowPlaying();
        mAudioPlayer.load(bean);
    }

    public void pause(){
        mAudioPlayer.pause();
    }

    public void resume(){
        mAudioPlayer.resume();
    }

    public void release(){
        mAudioPlayer.release();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 播放下一首歌曲
     */
    public void next(){
        AudioBean bean = getNextPlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 播放前一首歌曲
     */
    public void previous(){
        AudioBean bean = getPrePlaying();
        mAudioPlayer.load(bean);
    }

    /**
     * 自动切换播放/暂停
     * @return
     */
    public void playOrPause(){
        if (isStartState()) {
            pause();
        }else if(isPauseState()){
            resume();
        }
    }

    private boolean isStartState() {
        return false;
    }

    private boolean isPauseState() {
        return false;
    }

    private AudioBean getNowPlaying() {
        return null;
    }

    private AudioBean getNextPlaying() {
        return null;
    }

    private AudioBean getPrePlaying() {
        return null;
    }

}
