package com.malalaoshi.android.view;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

/**
 * Mala clickable span
 * Created by tianwei on 1/9/16.
 */
public class MaClickableSpan extends ClickableSpan {

    public interface OnLinkClickListener {
        void onLinkClick(View view);
    }

    private int color;
    private OnLinkClickListener listener;

    public MaClickableSpan() {
        color = MalaApplication.getInstance().getResources().getColor(R.color.color_blue_8dbedf);
    }

    public void setOnLinkClickListener(OnLinkClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onClick(View view) {
        if (listener != null) {
            listener.onLinkClick(view);
        }
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setColor(color);
        ds.setUnderlineText(false);
        ds.bgColor = Color.TRANSPARENT;
    }
}
