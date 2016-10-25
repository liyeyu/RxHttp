package com.liyeyu.rxhttp;

/**
 * Created by Liyeyu on 2016/10/25.
 */

public interface OnProgressListener {
    void onProgress(long writtenLen, long totalLen, boolean hasFinish);
}
