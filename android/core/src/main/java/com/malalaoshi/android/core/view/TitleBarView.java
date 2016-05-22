package com.malalaoshi.android.core.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.core.R;

/**
 * Title bar view
 * Created by tianwei on 1/24/16.
 */
public class TitleBarView extends LinearLayout implements View.OnClickListener {

    public interface OnTitleBarClickListener {
        void onTitleLeftClick();

        void onTitleRightClick();
    }

    private OnTitleBarClickListener listener;

    protected ImageView leftView;
    protected TextView rightView;
    protected TextView titleView;

    public TitleBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.core__title_bar, this);
        leftView = (ImageView) view.findViewById(R.id.iv_left);
        rightView = (TextView) view.findViewById(R.id.tv_right);
        titleView = (TextView) view.findViewById(R.id.tv_title);
        leftView.setOnClickListener(this);
        rightView.setOnClickListener(this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomTitleBar);
        Drawable leftBackground = typedArray.getDrawable(R.styleable.CustomTitleBar_cusLeftBackgroud);
        boolean leftShow = typedArray.getBoolean(R.styleable.CustomTitleBar_cusLeftShow, false);

        int rightTextColor = typedArray.getColor(R.styleable.CustomTitleBar_cusRightTextColor, 0x82b4d9);
        Drawable rightBackground = typedArray.getDrawable(R.styleable.CustomTitleBar_cusRightBackgroud);
        String rightText = typedArray.getString(R.styleable.CustomTitleBar_cusRightText);
        float rightTextSize = typedArray.getDimension(R.styleable.CustomTitleBar_cusRightTextSize, 36);
        boolean rightShow = typedArray.getBoolean(R.styleable.CustomTitleBar_cusRightShow, true);

        float titleTextSize = typedArray.getDimension(R.styleable.CustomTitleBar_cusTitleTextSize, 48);
        int titleTextColor = typedArray.getColor(R.styleable.CustomTitleBar_cusTitleTextColor, 0x333333);
        String title = typedArray.getString(R.styleable.CustomTitleBar_cusTitleText);
        typedArray.recycle();

        leftView.setImageDrawable(leftBackground);

        rightView.setTextColor(rightTextColor);
        rightView.setBackgroundDrawable(rightBackground);
        rightView.setText(rightText);
        rightView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rightTextSize);

        titleView.setText(title);
        titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        titleView.setTextColor(titleTextColor);

        leftView.setVisibility(leftShow ? View.VISIBLE : View.GONE);
        rightView.setVisibility(rightShow ? View.VISIBLE : View.GONE);

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_left) {
            if (listener != null) {
                listener.onTitleLeftClick();
            }
        } else if (v.getId() == R.id.tv_right) {
            if (listener != null) {
                listener.onTitleRightClick();
            }
        }
    }

    public void setLeftImageDrawable(Drawable leftBackground) {
        leftView.setImageDrawable(leftBackground);
    }

    public void setRightVisibility(int visible) {
        rightView.setVisibility(visible);
    }

    public void setOnTitleBarClickListener(OnTitleBarClickListener listener) {
        this.listener = listener;
    }

    public void setTitle(int rid) {
        titleView.setText(rid);
    }

    public void setTitle(String title) {
        titleView.setText(title);
    }


}
