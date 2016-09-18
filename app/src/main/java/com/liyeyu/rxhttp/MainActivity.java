package com.liyeyu.rxhttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.liyeyu.rxhttp.demo.R;

import java.io.File;

import liyeyu.support.utils.utils.ImageUtil;
import liyeyu.support.utils.utils.ImageUtils;

public class MainActivity extends AppCompatActivity {

    public static final String OUTPUT_PATH = Environment.getExternalStorageDirectory()+"/liyeyu/pic/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxHttpConfig.init(this,"http://geci.me/api/lyric/");
        RetrofitHelper.isDebug = true;
    }

    public void click(View view){
//        RetrofitHelper.request(DemoApi.class, new RetrofitHelper.HttpCallBack<LRCInfo, DemoApi>() {
//            @Override
//            public Call<LRCInfo> request(DemoApi request) {
//                return request.get("海阔天空/Beyond");
//            }
//
//            @Override
//            public void onCompleted(LRCInfo baseBean) {
//                Log.i("baseBean",baseBean+"");
//            }
//
//            @Override
//            public void onError(String message) {
//
//            }
//        });
        RxHttpParams rxHttpParams = new RxHttpParams.Build()
                .url("海阔天空/Beyond")
                .paramsBody(new BaseInfo(123))
                .method(RxHttpParams.HttpMethod.GET)
                .build();
        RetrofitHelper.request(rxHttpParams,LRCInfo.class, new RetrofitHelper.HttpCallBackImpl<LRCInfo>() {
            @Override
            public void onCompleted(LRCInfo lrcInfo) {

            }
        });
    }
    public static void uploadImg(String path){
        final File file = ImageUtils.getInstance().getCompressedImageFile(path, OUTPUT_PATH + new File(path).getName());
        RxHttpParams params = new RxHttpParams.Build()
                .url("upload url")
                .addPart("file", ImageUtil.getImageType(file),file)
                .build();
        RetrofitHelper.upload(params, String.class, file.getPath(), new RetrofitHelper.HttpCallBackImpl<String>() {
            @Override
            public void onCompleted(String s) {

            }
        });
    }
}
