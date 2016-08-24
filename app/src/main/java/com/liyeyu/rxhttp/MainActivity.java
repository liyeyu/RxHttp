package com.liyeyu.rxhttp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.liyeyu.rxhttp.demo.R;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RxHttpConfig.init(this,"http://geci.me/api/lyric/");
        RetrofitHelper.isDebug = true;
    }

    public void click(View view){
        RetrofitHelper.request(DemoApi.class, new RetrofitHelper.HttpCallBack<LRCInfo, DemoApi>() {
            @Override
            public Call<LRCInfo> request(DemoApi request) {
                return request.get("海阔天空/Beyond");
            }

            @Override
            public void onCompleted(LRCInfo baseBean) {
                Log.i("baseBean",baseBean+"");
            }

            @Override
            public void onError(String message) {

            }
        });
    }
}
