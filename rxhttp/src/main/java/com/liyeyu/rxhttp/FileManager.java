package com.liyeyu.rxhttp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.ResponseBody;
import rx.Subscriber;

/**
 * Created by Liyeyu on 2016/10/25.
 */

public class FileManager {

    public static boolean  writeResponseBodyToDisk(String path, ResponseBody body, Subscriber<? super ProgressInfo> subscriber) {
//        String type = body.contentType().toString();
//        String suffix  = MimeTypeMap.getSingleton().getExtensionFromMimeType(type);
        try {
            File futureStudioIconFile = new File(path);
            InputStream inputStream = null;
            OutputStream outputStream = null;
            ProgressInfo progressInfo = new ProgressInfo();
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    Thread.sleep(100);
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    progressInfo.writtenLen = fileSizeDownloaded;
                    progressInfo.totalLen = fileSize;
                    progressInfo.hasFinish = (fileSizeDownloaded == fileSize);
                    if(subscriber!=null){
                        subscriber.onNext(progressInfo);
                    }
                }
                outputStream.flush();
                if(subscriber!=null){
                    subscriber.onCompleted();
                }
                return true;
            } catch (IOException e) {
                if(subscriber!=null){
                    subscriber.onError(e);
                }
                return false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return false;
            }finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static byte[] read(InputStream inputStream){
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
}
