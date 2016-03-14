package com.malalaoshi.android.net.okhttp;



import com.android.volley.VolleyError;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.util.MalaContext;
import com.malalaoshi.android.util.UserManager;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


/**
 * Created by kang on 16/3/11.
 */
public class UploadFile {

    public static void uploadImg(String filePath, String url, Map<String, String> headers, final NetworkListener networkListener){
        File file = new File(filePath);
        RequestBody fileBody = RequestBody.create(MediaType.parse("image/png"), file);
        //创建okHttpClient对象
        OkHttpClient mOkHttpClient = new OkHttpClient();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"avatar\""), fileBody)
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
                MalaContext.postOnMainThread(new Runnable() {
                    @Override
                    public void run() {
                        // OKHTTP
                        if (response.code()==200){
                            if (networkListener!=null){
                                networkListener.onSucceed(response.body()!=null?response.body().toString():null);
                            }
                        }else{
                            if (networkListener!=null){
                                networkListener.onFailed(new VolleyError());
                            }
                        }
                    }
                });
            }
        });




/*        RequestBody requestBody = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"title\""),
                        RequestBody.create(null, "Square Logo"))
                .addPart(
                        Headers.of("Content-Disposition", "form-data; name=\"image\""),
                        RequestBody.create(MediaType.parse(""), new File("website/static/logo-square.png")))
                .build();*/

       /* Request request = new Request.Builder()
                .header("Authorization", "token")
                .url("https://api.imgur.com/3/image")
                .post(requestBody)
                .build();*/
     /*   .addPart(Headers.of(
                        "Content-Disposition",
                        "form-data; name=\"avatar\""),
                RequestBody.create(null, filePath))*/
    }
}
