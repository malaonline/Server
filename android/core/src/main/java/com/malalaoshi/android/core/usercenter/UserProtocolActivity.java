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
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.api.UserProtocolApi;
import com.malalaoshi.android.core.usercenter.entity.UserPolicy;
import com.malalaoshi.android.core.view.TitleBarView;

/**
 * Open protocol of user by webview
 * Created by tianwei on 12/27/15.
 */
public class UserProtocolActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    private static final String CONTENT_TYPE = "text/html";
    private static final String UTF8 = "utf-8";
    private static final String CACHE_NAME = "user_protocol_cache";
    private static final String CACHE_KEY = "content";

    protected WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.core__activity_user_protocol);
        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new MLWebViewClient());
        TitleBarView titleBarView = (TitleBarView) findViewById(R.id.title_bar_view);
        if (titleBarView != null) {
            titleBarView.setOnTitleBarClickListener(this);
        }
        //用户协议
        ApiExecutor.exec(new FetchProtocolRequest(this));
    }


    @Override
    public void onTitleLeftClick() {
        finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    private void onFetchSuccess(@NonNull UserPolicy userPolicy) {
        if (userPolicy.getContent() != null) {
            loadData(userPolicy.getContent());
            saveCache(userPolicy.getContent());
        } else {
            loadCache();
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
        webView.loadDataWithBaseURL(null, content, CONTENT_TYPE, UTF8, null);
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

    private static final class FetchProtocolRequest extends BaseApiContext<UserProtocolActivity, UserPolicy> {

        public FetchProtocolRequest(UserProtocolActivity userProtocolActivity) {
            super(userProtocolActivity);
        }

        @Override
        public UserPolicy request() throws Exception {
            return new UserProtocolApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull UserPolicy userPolicy) {
            get().onFetchSuccess(userPolicy);
        }
    }
}
