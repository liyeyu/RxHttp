package com.liyeyu.rxhttp;

import android.content.Context;

import com.liyeyu.rxhttp.download.DownLoadHelper;

/**
 * Created by Liyeyu on 2016/8/22.
 */
public class RxHttpConfig {

    public static void init(Context mApp){
        RetrofitHelper.init();
        DownLoadHelper.init(mApp);
    }
}
