package com.malalaoshi.android.report;

import android.graphics.Paint;
import android.graphics.Rect;

import com.malalaoshi.android.core.utils.EmptyUtils;

/**
 * utils
 * Created by tianwei on 5/22/16.
 */
public class Utils {

    public static Rect getTextBounds(Paint paint, String txt) {
        Rect rect = new Rect();
        if (EmptyUtils.isNotEmpty(txt)) {
            paint.getTextBounds(txt, 0, txt.length(), rect);
        }
        return rect;
    }
}
