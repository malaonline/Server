package com.malalaoshi.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
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
    @Bind(R.id.tv_right)
    protected TextView rightView;

    @Bind(R.id.tv_title)
    protected TextView titleView;

    private Drawable leftBackground;
    private boolean leftShow;

    private int rightTextColor;
    private Drawable  rightBackground;
    private String  rightText;
    private float  rightTextSize;
    private boolean rightShow;

    private float titleTextSize;
    private int titleTextColor;
    private String title;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.title_bar, this);
        ButterKnife.bind(this, view);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBar);
        leftBackground = typedArray.getDrawable(R.styleable.CustomTitleBar_cusLeftBackgroud);
        leftShow = typedArray.getBoolean(R.styleable.CustomTitleBar_cusLeftShow, false);

        rightTextColor = typedArray.getColor(R.styleable.CustomTitleBar_cusRightTextColor, 0x82b4d9);
        rightBackground = typedArray.getDrawable(R.styleable.CustomTitleBar_cusRightBackgroud);
        rightText = typedArray.getString(R.styleable.CustomTitleBar_cusRightText);
        rightTextSize = typedArray.getDimension(R.styleable.CustomTitleBar_cusRightTextSize, 36);
        rightShow = typedArray.getBoolean(R.styleable.CustomTitleBar_cusRightShow, true);

        titleTextSize = typedArray.getDimension(R.styleable.CustomTitleBar_cusTitleTextSize, 48);
        titleTextColor = typedArray.getColor(R.styleable.CustomTitleBar_cusTitleTextColor, 0x333333);
        title = typedArray.getString(R.styleable.CustomTitleBar_cusTitleText);
        typedArray.recycle();

        leftView.setImageDrawable(leftBackground);

        rightView.setTextColor(rightTextColor);
        rightView.setBackground(rightBackground);
        rightView.setText(rightText);
        rightView.setTextSize(TypedValue.COMPLEX_UNIT_PX,rightTextSize);

        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX,titleTextSize);
        titleView.setTextColor(titleTextColor);

        leftView.setVisibility(leftShow ? View.VISIBLE : View.GONE);
        rightView.setVisibility(rightShow ? View.VISIBLE : View.GONE);

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

    @OnClick(R.id.tv_right)
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
