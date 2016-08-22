package com.liyeyu.rxhttp;

import android.app.Application;

/**
 * Created by Liyeyu on 2016/8/22.
 */
public class RxHttpApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        RxHttpConfig.init(getApplicationContext());
    }
}
