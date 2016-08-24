package com.liyeyu.rxhttp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

/**
 * Created by Liyeyu on 2016/8/23.
 */
public interface DemoApi {
    @GET
    Call<LRCInfo> get(@Url String url);
}
