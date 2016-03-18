package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.net.Constants;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;

import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Open protocol of user by webview
 * Created by tianwei on 12/27/15.
 */
public class UserProtocolView extends LinearLayout {
    private static final String CONTENT_TYPE = "text/html";
    private static final String UTF8 = "UTF-8";
    private static final String CACHE_NAME = "user_protocol_cache";
    private static final String CACHE_KEY = "content";
    private Context context;

    @Bind(R.id.webview)
    protected WebView webView;

    public UserProtocolView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.view_webview, this);
        setOrientation(VERTICAL);
        ButterKnife.bind(this, rootView);
        webView.setWebViewClient(new MLWebViewClient());
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        NetworkSender.getUserProtocol(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    JSONObject jo = new JSONObject(json.toString());
                    String content = jo.optString(Constants.CONTENT);
                    if (!TextUtils.isEmpty(content)) {
                        loadData(content);
                        saveCache(content);
                    }
                } catch (Exception e) {
                    loadCache();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                loadCache();
            }
        });
    }

    private void loadCache() {
        SharedPreferences preferences =
                context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
        String content = preferences.getString(CACHE_KEY, "");
        if (!TextUtils.isEmpty(content)) {
            loadData(content);
        }
    }

    private void saveCache(String content) {
        SharedPreferences preferences =
                context.getSharedPreferences(CACHE_NAME, Context.MODE_PRIVATE);
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
}
