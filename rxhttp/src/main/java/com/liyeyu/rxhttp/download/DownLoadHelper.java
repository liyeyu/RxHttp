package com.liyeyu.rxhttp.download;

import android.content.Context;

import com.liulishuo.filedownloader.FileDownloader;

/**
 * Created by Liyeyu on 2016/8/22.
 */
public class DownLoadHelper {
    public static void init(Context mApp){
        FileDownloader.init(mApp);
    }

    public static void download(String url,String path,DownLoadListener listener){
        FileDownloader
                .getImpl()
                .create(url)
                .setPath(path)
                .setListener(listener)
                .start();
    }
}
