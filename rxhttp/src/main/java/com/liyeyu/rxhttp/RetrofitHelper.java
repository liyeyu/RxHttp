package com.liyeyu.rxhttp;

import android.text.TextUtils;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
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
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
            call = apiService.post(params.getHeads(),url,params.getParamsBody());
        }else if(params.getPartParams().isEmpty() && params.getQueryParams().isEmpty()
                && params.getPart()==null){
            if(params.getMethod()==RxHttpParams.HttpMethod.POST){
                call = apiService.post(params.getHeads(),url);
            }else{
                call = apiService.get(params.getHeads(),url);
            }
        }else if(params.getPartParams().isEmpty() && params.getPart()==null){
            if(params.getMethod()==RxHttpParams.HttpMethod.POST){
                call = apiService.post(params.getHeads(),url,params.getQueryParams());
            }else{
                call = apiService.get(params.getHeads(),url,params.getQueryParams());
            }
        }else  if(params.getQueryParams().isEmpty()){
            if(params.getPartParams().isEmpty()){
                call = apiService.postPart(params.getHeads(),url,params.getPart());
            }else{
                call = apiService.postPart(params.getHeads(),url,params.getPartParams(),params.getPart());
            }
        }else{
            call = apiService.post(params.getHeads(),url,params.getMultipartBody());
        }
        requestCall(call,clz,callBack);
    }

    public static <T> void requestCall(Call call,final Class<T> clz,final HttpCallBackImpl<T> callBack){
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, retrofit2.Response response) {
                ResponseBody body = (ResponseBody) response.body();
                if(body!=null && body.byteStream()!=null){
                    String s = new String(FileManager.read(body.byteStream()),Charset.forName("UTF-8"));
                    T t = gson.fromJson(s, clz);
                    if(callBack!=null){
                        callBack.onCompleted(t);
                    }
                    Log.i("ResponseBody",s);
                }else{
                    if(callBack!=null){
                        callBack.onError("onResponse error");
                    }
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

    public static <T> void upload(RxHttpParams params,Class<T> clz
            , String pathList, final HttpCallBackImpl<T> callBack) {
        if (!TextUtils.isEmpty(pathList)) {
            if(params==null){
                throw new NullPointerException("params is null");
            }
            String[] split = pathList.split("\\.");
            String suffix = split[split.length - 1];
            params.addPart("file", MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix),new File(pathList));
            request(params, clz, callBack);
        }

    }

    public static void download(final RxHttpParams params
            ,final String path,final String name,final HttpCallBackImpl<String> callBack) {
        if (!TextUtils.isEmpty(path)) {
            if(params==null){
                throw new NullPointerException("params is null");
            }
            final String filePath = path + File.separator + name;
            ApiService apiService = mRetrofit.create(ApiService.class);
            Call<ResponseBody> download = apiService.download(params.getHeads(), params.getUrl(), params.getQueryParams());
            download.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    final ResponseBody body = response.body();
                    if(body!=null && body.byteStream()!=null){
                        Observable.create(new Observable.OnSubscribe<ProgressInfo>() {
                            @Override
                            public void call(Subscriber<? super ProgressInfo> subscriber) {
                                FileManager.writeResponseBodyToDisk(filePath,body,subscriber);
                            }
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Subscriber<ProgressInfo>() {
                            @Override
                            public void onCompleted() {
                                if(callBack!=null){
                                    callBack.onCompleted(filePath);
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                if(callBack!=null){
                                    callBack.onError(e.toString());
                                }
                            }

                            @Override
                            public void onNext(ProgressInfo info) {
                                OnProgressListener mProgressListener = params.getProgressListener();
                                if(mProgressListener!=null){
                                    mProgressListener.onProgress(info.writtenLen,info.totalLen,info.hasFinish);
                                }
                            }
                        });
                    }else{
                        if(callBack!=null){
                            callBack.onError("onResponse error");
                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if(callBack!=null){
                        callBack.onError(t.getMessage());
                    }
                }
            });
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
