package com.liyeyu.rxhttp;


import com.liyeyu.rxhttp.bean.BaseBean;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public  interface ApiService{
    @POST
    Call<BaseBean> post(@Url String url, @QueryMap Map<String, Object> params);
    @POST
    Call<BaseBean> post(@Url String url, RequestBody body);
    @GET
    Call<BaseBean> get(@Url String url, @QueryMap Map<String, Object> params);
    @GET
    Call<BaseBean> get(@Url String url);
}
