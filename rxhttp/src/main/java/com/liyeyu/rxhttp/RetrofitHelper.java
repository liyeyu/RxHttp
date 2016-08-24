package com.liyeyu.rxhttp;

import android.text.TextUtils;
import android.util.Log;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    public static String BASE_URL = "";
    public static String TAG = "RetrofitHelper";
    private static Retrofit mRetrofit;
    public static boolean isDebug = false;

    public static void init(String baseUrl) {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.networkInterceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Response response = chain.proceed(chain.request());
//                return response;
//            }
//        });
        BASE_URL = baseUrl;
        mRetrofit = new Retrofit
                .Builder()
                .client(new OkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl).build();
    }
    public static void setBaseUrl(String baseUrl) {
        if(!TextUtils.isEmpty(BASE_URL)){
           init(baseUrl);
        }
    }
    private static <API> API getRequest(Class<API> clz){
        return  mRetrofit.create(clz);
    }

    public static <T,API> void request(Class<API> clz,final HttpCallBack<T,API> callBack){
        if(callBack!=null){
            callBack.request(getRequest(clz)).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, retrofit2.Response response) {
                    if(isDebug){
                        Log.w(TAG+"","----------- isSuccessful:"+response.isSuccessful()+" ------------");
                        Log.i(TAG+":header",response.headers().toString());
                        Log.i(TAG+":url",response.raw().networkResponse().request().url().toString());
                        Log.w(TAG+"","------------------------------------------");

                    }
                    T body = (T) response.body();
                    callBack.onCompleted(body);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    callBack.onError(t.getMessage());
                }
            });
        }
    }

    public interface HttpCallBack<T,API>{
        Call<T> request(API request);
        void onCompleted(T t);
        void onError(String message);
    }
}
