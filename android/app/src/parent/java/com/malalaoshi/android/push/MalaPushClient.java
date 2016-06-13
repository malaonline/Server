package com.malalaoshi.android.push;

import android.content.Context;
import android.util.Log;

import com.malalaoshi.android.core.MalaContext;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

/**
 * Created by kang on 16/6/13.
 */
public class MalaPushClient {
    public static String TAG = "MalaPushClient";
    private static MalaPushClient malaPushClient = null;
    public void init(){
        JPushInterface.requestPermission(MalaContext.getContext());   // 请求权限
        JPushInterface.setDebugMode(true);                            // 设置开启日志,发布时请关闭日志
        JPushInterface.init(MalaContext.getContext());                // 初始化 JPush
    }

    public static MalaPushClient getInstance()
    {
        if ( malaPushClient==null )
        {
            malaPushClient = new MalaPushClient();
            //malaPushClient.init();
        }
        return malaPushClient;
    }

    public void setAliasAndTags(String alias, Set<String> tags){
        //设置tag和别名(在登录和登出处需要添加设置别名)
        JPushInterface.setAliasAndTags(MalaContext.getContext(), alias, tags, new TagAliasCallback() {
            @Override
            public void gotResult(int i, String s, Set<String> set) {
                Log.d(TAG, "status code:" + i + " alias:" + s);
            }
        });
    }

    public void onResume(Context context){
        JPushInterface.onResume(context);
    }

    public void onPause(Context context){
        JPushInterface.onPause(context);
    }

    public void reportNotificationOpened(String msgId){
        JPushInterface.reportNotificationOpened(MalaContext.getContext(),msgId);
    }
}
