package com.liyeyu.rxhttp;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitHelper {
    public static String BASE_URL = "";
    public static String TAG = "RetrofitHelper";
    private static Retrofit mRetrofit;
    public static boolean isDebug = true;
    private static Gson gson;

    public static void init(String baseUrl) {
        gson = new Gson();
        BASE_URL = baseUrl;
        mRetrofit = new Retrofit
                .Builder()
                .client(okHttpClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl).build();
    }
    public static void setBaseUrl(String baseUrl) {
        if(!TextUtils.isEmpty(BASE_URL)){
           init(baseUrl);
        }
    }

    public static OkHttpClient okHttpClient() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request request = chain.request()
                                .newBuilder()
                                .build();
                        return chain.proceed(request);
                    }

                })
                .addInterceptor(new HttpLoggingInterceptor()
                        .setLevel(isDebug?HttpLoggingInterceptor.Level.BODY:HttpLoggingInterceptor.Level.NONE))
                .build();
        return okHttpClient;
    }

    private static <API> API getRequest(Class<API> clz){
        return  mRetrofit.create(clz);
    }

    public static  <T,API> void request(Class<API> clz,final HttpCallBack<T,API> callBack){
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

    public static <T> void request(RxHttpParams params,final Class<T> clz,final HttpCallBackImpl<T> callBack){
        if(params==null || clz==null){
            return;
        }
        ApiService apiService = mRetrofit.create(ApiService.class);
        String url = params.getUrl();
        Call<ResponseBody> call;

        if(callBack!=null && callBack.request(apiService)!=null){
            call = callBack.request(apiService);
        }else if(params.getParamsBody()!=null){
            call = apiService.post(url,params.getParamsBody());
        }else if(params.getPartParams().isEmpty() && params.getQueryParams().isEmpty()
                && params.getPart()==null){
            if(params.getMethod()==RxHttpParams.HttpMethod.POST){
                call = apiService.post(url);
            }else{
                call = apiService.get(url);
            }
        }else if(params.getPartParams().isEmpty() && params.getPart()==null){
            if(params.getMethod()==RxHttpParams.HttpMethod.POST){
                call = apiService.post(url,params.getQueryParams());
            }else{
                call = apiService.get(url,params.getQueryParams());
            }
        }else  if(params.getQueryParams().isEmpty()){
            if(params.getPartParams().isEmpty()){
                call = apiService.postPart(url,params.getPart());
            }else{
                call = apiService.postPart(url,params.getPartParams(),params.getPart());
            }
        }else{
            call = apiService.post(url,params.getMultipartBody());
        }
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                ResponseBody body = (ResponseBody) response.body();
                if(body!=null && body.byteStream()!=null){
                    String s = new String(read(body.byteStream()),Charset.forName("UTF-8"));
                    T t = gson.fromJson(s, clz);
                    if(callBack!=null){
                        callBack.onCompleted(t);
                    }
                    Log.i("ResponseBody",s);
                }
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                if(callBack!=null){
                    callBack.onError(t.getMessage());
                }
            }
        });
    }

    private static byte[] read(InputStream inputStream){
        byte[] buffer = new byte[1024];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output.write(buffer, 0, bytesRead);
            }
            return output.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> void upload(RxHttpParams params,Class<T> clz
            , String pathList, final HttpCallBackImpl<T> callBack) {
        if (!TextUtils.isEmpty(pathList)) {
            if(params==null){
                params = new RxHttpParams.Build().build();
            }
            String[] split = pathList.split("\\.");
            String suffix = split[split.length - 1];
            params.addPart("suffix",suffix);
            request(params, clz, callBack);
        }

    }

    public  interface HttpCallBack<T,API> extends IHttpCallBack<T>{
        Call<T> request(API request);
    }
    public interface IHttpCallBack<T>{
        void onCompleted(T t);
        void onError(String message);
    }
    public static abstract class HttpCallBackImpl<T>{
        public abstract void onCompleted(T t);

        public void onError(String message) {

        }
        public Call<ResponseBody> request(ApiService request) {
            return null;
        }
    }
}
