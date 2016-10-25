package com.liyeyu.rxhttp;


/**
 * Created by Liyeyu on 2016/10/25.
 */
public abstract class DefaultProgressListener implements OnProgressListener {

    public abstract void onProgress(int progress);

    @Override
    public void onProgress(long hasWrittenLen, long totalLen, boolean hasFinish) {
        int percent = (int) (hasWrittenLen * 100 / totalLen);
        if (percent > 100) percent = 100;
        if (percent < 0) percent = 0;
        onProgress(percent);
    }
}