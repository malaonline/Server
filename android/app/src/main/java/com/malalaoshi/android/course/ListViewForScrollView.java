package com.malalaoshi.android.course;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * ListView for scrollview
 * Created by tianwei on 3/6/16.
 */
public class ListViewForScrollView extends ListView {

    public ListViewForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE>>2,MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
