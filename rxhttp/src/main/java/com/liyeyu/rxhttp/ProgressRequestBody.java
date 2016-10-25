package com.liyeyu.rxhttp;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by Liyeyu on 2016/10/25.
 */

public class ProgressRequestBody extends RequestBody {

    private RequestBody mRequestBody;
    private OnProgressListener mProgressListener;
    private BufferedSink bufferedSink;


    public ProgressRequestBody(File file , OnProgressListener OnProgressListener) {
        this.mRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file) ;
        this.mProgressListener = OnProgressListener ;
    }

    public ProgressRequestBody(RequestBody requestBody, OnProgressListener OnProgressListener) {
        this.mRequestBody = requestBody;
        this.mProgressListener = OnProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mRequestBody.contentType();
    }

    @Override
    public long contentLength() throws IOException {
        return mRequestBody.contentLength();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        mRequestBody.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                if(mProgressListener!=null){
                    Observable.just("onProgress")
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Action1<String>() {
                                @Override
                                public void call(String s) {
                                    mProgressListener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                                }
                            });
                }
            }
        };
    }
}
