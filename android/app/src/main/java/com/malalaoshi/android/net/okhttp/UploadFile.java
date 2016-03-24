package com.malalaoshi.android.net.okhttp;



import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.util.MalaContext;
import com.malalaoshi.android.util.UserManager;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.BufferedSink;


/**
 * Created by kang on 16/3/11.
 */
public class UploadFile {

    public static void uploadImg(String filePath, String url, Map<String, String> headers, final NetworkListener networkListener){

        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS).readTimeout(20, TimeUnit.SECONDS).writeTimeout(20,TimeUnit.SECONDS).build();
        //构造上传请求，类似web表单
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("avatar", file.getName(), fileBody/*RequestBody.create(null, file)*/)
                .build();

        Request request = new Request.Builder()
                .header(Constants.AUTH, headers.get(Constants.AUTH))
                .url(MalaApplication.getInstance().getMalaHost()+url)
                .patch(requestBody)
                .build();
        
        Call call = mOkHttpClient.newCall(request);

        call.enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e) {
                MalaContext.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        // OKHTTP
                        if (networkListener != null) {
                            networkListener.onFailed(new VolleyError());
                        }
                    }
                });

            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                if (response.code()==200){
                    MalaContext.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            // OKHTTP
                            if (networkListener!=null){
                                try{
                                    networkListener.onSucceed(response.body()!=null?response.body().string():null);
                                }catch (IOException e){
                                    networkListener.onFailed(new VolleyError());
                                }
                            }
                        }
                    });
                }else{
                    MalaContext.postOnMainThread(new Runnable() {
                        @Override
                        public void run() {
                            // OKHTTP
                            if (networkListener != null)
                                networkListener.onFailed(new VolleyError(new NetworkResponse(response.code(),null,null,false)));
                            }
                        }
                    );
                }

            }
        });
    }
}
