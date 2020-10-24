package com.liqun.lib_audio.mediaplayer.core;

import com.liqun.lib_audio.mediaplayer.events.AudioPlayModeEvent;
import com.liqun.lib_audio.mediaplayer.exception.AudioQueueEmptyException;
import com.liqun.lib_audio.mediaplayer.model.AudioBean;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Random;

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
        EventBus.getDefault().register(this);
        mAudioPlayer = new AudioPlayer();
        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        mPlayMode = PlayMode.LOOP;
    }

    private AudioBean getPlaying() {
        if (mQueue != null && !mQueue.isEmpty() && mQueueIndex >= 0 && mQueueIndex < mQueue.size()) {
            return mQueue.get(mQueueIndex);
        }else{
            throw new AudioQueueEmptyException("当前播放队列为空, 请先设置播放队列");
        }
    }

    /**
     * 获取播放器当前状态
     * @return
     */
    private CustomMediaPlayer.Status getStatus(){
        return mAudioPlayer.getStatus();
    }

    private AudioBean getNextPlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex + 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    private AudioBean getPrePlaying() {
        switch (mPlayMode) {
            case LOOP:
                mQueueIndex = (mQueueIndex - 1) % mQueue.size();
                break;
            case RANDOM:
                mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
                break;
            case REPEAT:
                break;
        }
        return getPlaying();
    }

    public ArrayList<AudioBean> getQueue(){
        return mQueue == null ? new ArrayList<AudioBean>() : mQueue;
    }

    /**
     * 设置播放队列
     * @param queue
     */
    public void setQueue(ArrayList<AudioBean> queue){
        setQueue(queue, 0);
    }

    public void setQueue(ArrayList<AudioBean> queue, int queueIndex){
        mQueue.addAll(queue);
        mQueueIndex = queueIndex;
    }

    private int queryAudio(AudioBean bean) {
        return 0;
    }
    private void addCustomAudio(int index, AudioBean bean) {

    }

    /**
     * 添加单一歌曲到指定位置
     * @param bean
     */
    public void addAudio(AudioBean bean){
        this.addAudio(0, bean);
    }

    public void addAudio(int index, AudioBean bean){
        if (mQueue == null) {
            throw new AudioQueueEmptyException("当前播放队列为空");
        }
        int query = queryAudio(bean);
        if (query <= -1) {
            // 没有添加过
            addCustomAudio(index, bean);
            setPlayIndex(index);
        }else {
            AudioBean currentBean = getNowPlaying();
            if (!currentBean.id.equals(bean.id)) {
                // 已经添加过且不在播放中
                setPlayIndex(query);
            }
        }
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

    /**
     * 是否播放中
     * @return 状态
     */
    private boolean isStartState() {
        return CustomMediaPlayer.Status.STARTED == getStatus();
    }

    /**
     * 是否暂停
     * @return 状态
     */
    private boolean isPauseState() {
        return CustomMediaPlayer.Status.PAUSED == getStatus();
    }

    /**
     * 获取当前歌曲信息
     * @return 当前歌曲信息
     */
    private AudioBean getNowPlaying() {
        return getPlaying();
    }
}
