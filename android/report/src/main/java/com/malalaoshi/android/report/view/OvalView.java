package com.malalaoshi.android.report.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.malalaoshi.android.report.R;

/**
 * 画实心圆
 * Created by tianwei on 5/21/16.
 */
public class OvalView extends View {

    private int color;
    private Paint paint;

    public OvalView(Context context) {
        super(context);
        initView(null);
    }

    public OvalView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    public void setColor(int color) {
        this.color = color;
    }

    private void initView(AttributeSet attrs) {
        paint = new Paint();
        if (attrs != null) {
            TypedArray array = getContext().obtainStyledAttributes(attrs, R.styleable.OvalView);
            color = array.getColor(R.styleable.OvalView_ovalColor, color);
            array.recycle();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(color);
        int x = getWidth() / 2;
        int y = getHeight() / 2;
        canvas.drawCircle(x, y, x, paint);
    }
}
