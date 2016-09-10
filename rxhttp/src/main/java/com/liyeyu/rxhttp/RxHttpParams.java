package com.liyeyu.rxhttp;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

/**
 * Created by Liyeyu on 2016/9/8.
 */
public final class RxHttpParams {

    private String url;
    private Object paramsBody;
    private HttpMethod method = HttpMethod.GET;
    private Map<String, String> queryParams = new HashMap<>();
    private Map<String, RequestBody> partParams = new HashMap<>();
    private MultipartBody.Part mPart;
    private MultipartBody mMultipartBody;

    private RxHttpParams() {
    }
    public void addQuery(String key,String value){
        queryParams.put(key,value);
    }
    public void addPart(String key,String value){
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), value);
        partParams.put(key,requestBody);
    }
    public void addPart(String key,String type,File file){
        RequestBody requestBody = RequestBody.create(MediaType.parse(type), file);
        mPart = MultipartBody.Part.createFormData(key,file.getName(),requestBody);
    }
    public void addMultipartBody(String key,String[] types,File[] files){
        if(types==null||files==null){
            return;
        }
        if(types.length!=files.length){
            return;
        }
        MultipartBody.Builder builder = new MultipartBody.Builder();
        for (int i = 0; i < files.length; i++) {
            RequestBody requestBody = RequestBody.create(MediaType.parse(types[i]), files[i]);
            builder.addFormDataPart(key,files[i].getName(),requestBody);
        }
        builder.setType(MultipartBody.FORM);
        mMultipartBody = builder.build();
    }
    public Map<String, String> getQueryParams() {
        return queryParams;
    }

    public Map<String, RequestBody> getPartParams() {
        return partParams;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }

    public Object getParamsBody() {
        return paramsBody;
    }

    public void setParamsBody(Object paramsBody) {
        this.paramsBody = paramsBody;
    }

    public MultipartBody.Part getPart() {
        return mPart;
    }

    public void setPart(MultipartBody.Part part) {
        mPart = part;
    }

    public MultipartBody getMultipartBody() {
        return mMultipartBody;
    }

    public void setMultipartBody(MultipartBody multipartBody) {
        mMultipartBody = multipartBody;
    }


    public static class Build{
        private RxHttpParams params;
        public Build() {
             params = new RxHttpParams();
        }
        public Build url(String url){
            params.url = url;
            return this;
        }
        public Build method(HttpMethod method){
            params.method = method;
            return this;
        }
        public Build addQuery(String key,String value){
            params.addQuery(key,value);
            return this;
        }
        public Build addPart(String key,String value){
            params.addPart(key,value);
            return this;
        }
        public Build addPart(String key,String type,File value){
            params.addPart(key,type,value);
            return this;
        }
        public Build addMultipartBody(String key,String[] types,File[] files){
            params.addMultipartBody(key,types,files);
            return this;
        }
        public Build paramsBody(Object paramsBody){
            params.paramsBody = paramsBody;
            return this;
        }
        public RxHttpParams build(){
            return params;
        }
    }

    public enum HttpMethod{
        POST,GET
    }

}
