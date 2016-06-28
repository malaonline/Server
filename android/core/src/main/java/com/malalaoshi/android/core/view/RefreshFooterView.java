package com.malalaoshi.android.core.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrUIHandler;
import com.chanven.lib.cptr.indicator.PtrIndicator;
import com.malalaoshi.android.core.R;

/**
 * 刷新的头部
 * Created by tianwei on 16-6-12.
 */
public class RefreshFooterView extends LinearLayout implements PtrUIHandler {

    enum Status {
        RESET,
        REFRESH_PREPARE,
        REFRESHING,
        FINISHED,
    }

    private TextView tipView;
    private AnimationDrawable animation;
    private Status status;

    public RefreshFooterView(Context context) {
        super(context);
        initView();
    }

    public RefreshFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        LayoutInflater.from(getContext()).inflate(R.layout.core__view_refresh_header, this, true);
        tipView = (TextView) findViewById(R.id.tv_tip);
        ImageView imageView = (ImageView) findViewById(R.id.iv_img);
        animation = (AnimationDrawable) imageView.getBackground();
        animation.selectDrawable(3);
    }

    private void setLayout(Status status) {
        this.status = status;
        switch (status) {
            case RESET:
                setText("下拉刷新...");
                break;
            case REFRESH_PREPARE:
                setText("下拉刷新...");
                break;
            case REFRESHING:
                animation.start();
                setText("正在加载...");
                break;
            case FINISHED:
                animation.stop();
                animation.selectDrawable(3);
                setText("更新完成");
                break;
        }
    }

    @Override
    public void onUIReset(PtrFrameLayout frame) {
        setLayout(Status.RESET);
    }

    @Override
    public void onUIRefreshPrepare(PtrFrameLayout frame) {
        setLayout(Status.REFRESH_PREPARE);
    }

    @Override
    public void onUIRefreshBegin(PtrFrameLayout frame) {
        setLayout(Status.REFRESHING);
    }

    @Override
    public void onUIRefreshComplete(PtrFrameLayout frame) {
        setLayout(Status.FINISHED);
    }

    private void setText(String txt) {
        if (!txt.equals(tipView.getText().toString())) {
            tipView.setText(txt);
        }
    }

    @Override
    public void onUIPositionChange(PtrFrameLayout frame, boolean isUnderTouch, byte status, PtrIndicator ptrIndicator) {
        if (ptrIndicator.getCurrentPercent() >= 1 && this.status == Status.REFRESH_PREPARE) {
            setText("释放更新...");
        } else if (this.status == Status.REFRESH_PREPARE) {
            setText("下拉刷新...");
        }
    }


}
