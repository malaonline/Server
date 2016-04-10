package com.malalaoshi.android.core.usercenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.network.UIResultCallback;
import com.malalaoshi.android.core.usercenter.api.UserProtocolApi;
import com.malalaoshi.android.core.usercenter.entity.UserPolicy;
import com.malalaoshi.android.core.view.TitleBarView;

/**
 * Open protocol of user by webview
 * Created by tianwei on 12/27/15.
 */
public class UserProtocolActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    private static final String CONTENT_TYPE = "text/html";
    private static final String UTF8 = "UTF-8";
    private static final String CACHE_NAME = "user_protocol_cache";
    private static final String CACHE_KEY = "content";

    protected WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core__activity_user_protocol);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MLWebViewClient());
        UserProtocolApi protocolApi = new UserProtocolApi();
        protocolApi.get(new FetchProtocolCallback(this));
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.title_bar_view);
        titleBarView.setOnTitleBarClickListener(this);
    }

    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    private static final class FetchProtocolCallback extends UIResultCallback<UserProtocolActivity, UserPolicy> {
        public FetchProtocolCallback(UserProtocolActivity userProtocolActivity) {
            super(userProtocolActivity);
        }

        @Override
        public void onResult(@NonNull UserProtocolActivity activity, UserPolicy userPolicy) {
            if (userPolicy != null && userPolicy.getContent() != null) {
                activity.loadData(userPolicy.getContent());
                activity.saveCache(userPolicy.getContent());
            } else {
                activity.loadCache();
            }
        }
    }

    private void loadCache() {
        SharedPreferences preferences = getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        String content = preferences.getString(CACHE_KEY, "");
        if (!TextUtils.isEmpty(content)) {
            loadData(content);
        }
    }

    private void saveCache(String content) {
        SharedPreferences preferences = getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(CACHE_KEY, content);
        editor.apply();
    }

    private void loadData(String content) {
        //webView.loadData(content, CONTENT_TYPE, UTF8);
        webView.loadDataWithBaseURL(null, content, "text/html", "utf-8", null);
    }

    private class MLWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return false;
        }
    }

    @Override
    protected String getStatName() {
        return "用户协议";
    }
}
