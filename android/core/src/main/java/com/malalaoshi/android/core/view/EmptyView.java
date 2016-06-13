package com.malalaoshi.android.core.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.core.R;
import com.malalaoshi.android.core.utils.ViewUtils;

/**
 * 空view
 * Created by tianwei on 16-6-13.
 */
public class EmptyView extends LinearLayout {

    private TextView txtView;
    private ImageView imageView;

    public EmptyView(Context context) {
        super(context);
    }

    public EmptyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public static EmptyView newInstance(ViewGroup parent) {
        return (EmptyView) ViewUtils.newInstance(parent, R.layout.core__empty_view);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        initView();
    }

    private void initView() {
        txtView = (TextView) findViewById(R.id.tv_empty_txt);
        imageView = (ImageView) findViewById(R.id.iv_empty_img);
    }

    public void setText(int rid) {
        txtView.setText(rid);
    }

    public void setText(String text) {
        txtView.setText(text);
    }

    public void setImage(int rid) {
        imageView.setImageResource(rid);
    }
}
