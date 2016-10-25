package com.liyeyu.rxhttp;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.liyeyu.rxhttp.demo.R;

import java.io.File;

import liyeyu.support.utils.utils.ImageUtil;
import liyeyu.support.utils.utils.ImageUtils;
import liyeyu.support.utils.utils.LogUtil;

public class MainActivity extends AppCompatActivity {

    public static final String OUTPUT_PATH = Environment.getExternalStorageDirectory()+"/liyeyu/pic/";
    private TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxHttpConfig.init(this,"http://geci.me/api/lyric/");
        RetrofitHelper.isDebug = true;
        mTextView = (TextView) findViewById(R.id.tv);
    }

    public void parse(View view){
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
                if(lrcInfo!=null){
                    mTextView.setText(lrcInfo.toString());
                }
            }
        });
    }
    public void download(View view){

//        File file = new File(Environment.getExternalStorageDirectory() + "/" + "123.txt");
//        RxHttpParams uploadParams = new RxHttpParams.Build()
//                .url("www.163.com")
//                .addPart("file",file)
//                .build();
//        uploadParams.setProgressListener(new UploadProgressListener() {
//            @Override
//            public void onProgress(int progress) {
//
//            }
//        });
//        RetrofitHelper.upload(uploadParams, BaseInfo.class, file.getPath(), new RetrofitHelper.HttpCallBackImpl<BaseInfo>() {
//            @Override
//            public void onCompleted(BaseInfo baseInfo) {
//
//            }
//        });
        //http://img3.duitang.com/uploads/item/201608/29/20160829173906_neXUL.thumb.700_0.jpeg
        RetrofitHelper.setBaseUrl("http://img3.duitang.com/");
        RxHttpParams downloadParams = new RxHttpParams.Build()
                .url("uploads/item/201608/29/20160829173906_neXUL.thumb.700_0.jpeg")
                .build();
        downloadParams.setProgressListener(new DefaultProgressListener() {
            @Override
            public void onProgress(int progress) {
                LogUtil.i("progress:"+progress);
                mTextView.setText("progress:"+progress);
            }
        });
        RetrofitHelper.download(downloadParams, Environment.getExternalStorageDirectory().getPath(), "100.jpg", new RetrofitHelper.HttpCallBackImpl<String>() {
            @Override
            public void onCompleted(String s) {
                LogUtil.i("onCompleted:"+s);
                mTextView.setText("onCompleted:"+s);
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
