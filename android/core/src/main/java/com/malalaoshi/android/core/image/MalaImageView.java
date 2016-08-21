package com.malalaoshi.android.core.image;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.malalaoshi.android.core.image.glide.GlideUtils;

/**
 * 麻辣ImageView
 * Created by tianwei on 8/21/16.
 */
public class MalaImageView extends ImageView {

    public MalaImageView(Context context) {
        super(context);
    }

    public MalaImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MalaImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * 普通加载
     */
    public void loadImage(String url) {
        GlideUtils.loadImage(getContext(), url, this);
    }

    /**
     * 普通加载
     */
    public void loadImage(String url, int defImage) {
        GlideUtils.loadImage(getContext(), url, this, defImage);
    }

    /**
     * 圆图
     */
    public void loadCircleImage(String url, int defImage) {
        GlideUtils.loadCircleImage(getContext(), url, this, defImage);
    }

    public void loadBlurImage(String url, int defImage) {
        GlideUtils.loadBlurImage(getContext(), url, this, defImage);
    }
}
