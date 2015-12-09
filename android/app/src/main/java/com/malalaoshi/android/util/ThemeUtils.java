package com.malalaoshi.android.util;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zl on 15/12/8.
 */
public class ThemeUtils{
    public static int getStatusBarHeight(Context context){
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        return resourceId > 0 ? context.getResources().getDimensionPixelSize(resourceId) : 0;
    }
    public static void setMargins(View v, int l, int t, int r, int b){
        if(v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams){
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams)v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
