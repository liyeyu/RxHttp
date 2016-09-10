package com.liyeyu.rxhttp;


import java.util.Map;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PartMap;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public  interface ApiService{
    @POST
    Call<ResponseBody> post(@Url String url, @QueryMap Map<String, String> params);
    @Multipart
    @POST
    Call<ResponseBody> postPart(@Url String url, @PartMap Map<String, RequestBody> map, @Part MultipartBody.Part part);
    @Multipart
    @POST
    Call<ResponseBody> postPart(@Url String url,@Part  MultipartBody.Part part);
    @POST
    Call<ResponseBody> post(@Url String url,@Body MultipartBody body);
    @POST
    Call<ResponseBody> post(@Url String url, @Body Object body);
    @POST
    Call<ResponseBody> post(@Url String url);
    @GET
    Call<ResponseBody> get(@Url String url,@QueryMap Map<String, String> params);
    @GET
    Call<ResponseBody> get(@Url String url);
}
