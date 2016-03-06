package com.malalaoshi.android.course;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.malalaoshi.android.R;

/**
 * BorderText view
 * Created by tianwei on 3/6/16.
 */
public class BorderTextView extends TextView {

    private float rightBorder;
    private float bottomBorder;
    private float topBorder;
    private float leftBorder;
    private Paint paint;

    public BorderTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(attrs);
    }

    public BorderTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(attrs);
    }

    private void initView(AttributeSet attrs) {
        TypedArray array = this.getContext().obtainStyledAttributes(attrs, R.styleable.BorderTextView);
        rightBorder = array.getDimensionPixelOffset(R.styleable.BorderTextView_RightBorder, 0);
        bottomBorder = array.getDimensionPixelOffset(R.styleable.BorderTextView_BottomBorder, 0);
        leftBorder = array.getDimensionPixelOffset(R.styleable.BorderTextView_LeftBorder, 0);
        topBorder = array.getDimensionPixelOffset(R.styleable.BorderTextView_TopBorder, 0);
        int borderColor = array.getColor(R.styleable.BorderTextView_BorderColor, Color.TRANSPARENT);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(borderColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (rightBorder > 0) {
            canvas.drawRect(getWidth() - rightBorder, 0, getWidth(), getHeight(), paint);
        }
        if (bottomBorder > 0) {
            canvas.drawRect(0, getHeight() - bottomBorder, getWidth(), getHeight(), paint);
        }
        if (topBorder > 0) {
            canvas.drawRect(0, 0, getWidth(), topBorder, paint);
        }
        if (leftBorder > 0) {
            canvas.drawRect(0, 0, leftBorder, getHeight(), paint);
        }
    }
}
