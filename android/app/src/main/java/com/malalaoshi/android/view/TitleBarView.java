package com.malalaoshi.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Title bar view
 * Created by tianwei on 1/24/16.
 */
public class TitleBarView extends LinearLayout {

    public interface OnTitleBarClickListener {
        void onTitleLeftClick();

        void onTitleRightClick();
    }

    private OnTitleBarClickListener listener;

    @Bind(R.id.iv_left)
    protected ImageView leftView;
    @Bind(R.id.iv_right)
    protected ImageView rightView;

    @Bind(R.id.tv_title)
    protected TextView titleView;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        ButterKnife.bind(this, view);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        this.listener = listener;
    }

    @OnClick(R.id.iv_left)
    protected void onLeftClick(View view) {
        if (listener != null) {
            listener.onTitleLeftClick();
        }
    }

    @OnClick(R.id.iv_right)
    protected void onRightClick(View view) {
        if (listener != null) {
            listener.onTitleRightClick();
        }
    }

    public void setTitle(int rid) {
        titleView.setText(rid);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }


}
