package com.malalaoshi.android.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by kang on 16/6/20.
 */
public class SlideViewPager extends ViewPager {
    private boolean canSlide = true;
    public SlideViewPager(Context context) {
        super(context);
    }

    public SlideViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!canSlide) {
            return true;
        }
        return super.onTouchEvent(ev);
    }

    public boolean isCanSlide() {
        return canSlide;
    }

    public void setCanSlide(boolean canSlide) {
        this.canSlide = canSlide;
    }
}
