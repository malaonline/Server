package com.malalaoshi.android.usercenter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Open protocol of user by webview
 * Created by tianwei on 12/27/15.
 */
public class UserProtocolview extends WebView {
    private Context context;

    @Bind(R.id.webview)
    protected WebView webView;

    public UserProtocolview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        initLayout();
    }

    private void initLayout() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View rootView = inflater.inflate(R.layout.view_verify_student_name, null);
        ButterKnife.bind(rootView);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        loadUrl("http://j.malalaoshi.com/");
    }

    protected void loadurl(String url) {
        webView.loadUrl(url);
    }
}
