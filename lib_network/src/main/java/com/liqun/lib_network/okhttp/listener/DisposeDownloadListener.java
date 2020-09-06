package com.liqun.lib_network.okhttp.listener;

/**
 * @function 监听下载进度
 */
public interface DisposeDownloadListener extends DisposeDataListener {
    void onProgress(int progress);
}