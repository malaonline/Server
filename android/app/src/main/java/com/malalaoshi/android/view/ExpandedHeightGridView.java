package com.malalaoshi.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * 自定义gridview，解决ScrollView中嵌套GridView显示不正常的问题（1行半）
 * Created by liumengjun on 12/15/15.
 */
public class ExpandedHeightGridView extends GridView {
    public ExpandedHeightGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExpandedHeightGridView(Context context) {
        super(context);
    }

    public ExpandedHeightGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
