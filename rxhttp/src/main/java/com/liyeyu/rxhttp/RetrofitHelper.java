package com.liyeyu.rxhttp;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    public static String BASE_URL;
    private static Retrofit mRetrofit;
    public static void init() {
//        OkHttpClient okHttpClient = new OkHttpClient();
//        okHttpClient.networkInterceptors().add(new Interceptor() {
//            @Override
//            public Response intercept(Chain chain) throws IOException {
//                Response response = chain.proceed(chain.request());
//                return response;
//            }
//        });
        mRetrofit = new Retrofit
                .Builder()
                .baseUrl(BASE_URL)
                .client(new OkHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    private static <API extends ApiService> API getRequest(Class<API> clz){
        return  mRetrofit.create(clz);
    }

    public static <T,API extends ApiService> void request(Class<API> clz,final HttpCallBack<T,API> callBack){
        if(callBack!=null){
            callBack.request(getRequest(clz)).enqueue(new Callback() {
                @Override
                public void onResponse(Call call, retrofit2.Response response) {
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
