package com.malalaoshi.android.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by kang on 15/12/18.
 */
public class DensityUtil {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    public static int getScreemWidth(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;

       /* WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();*/
    }

    public static int getScreemHeight(Context context){
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.heightPixels;

        /*WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();*/
    }
}
