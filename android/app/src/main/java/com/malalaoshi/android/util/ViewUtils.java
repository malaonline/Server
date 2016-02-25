package com.malalaoshi.android.util;

import android.graphics.Paint;

/**
 * Created by kang on 16/2/18.
 */
public class ViewUtils {
    public static double getTextHeight(Paint paint) {
        Paint.FontMetrics fm = paint.getFontMetrics();
        return Math.ceil(fm.descent - fm.ascent);
    }
}
