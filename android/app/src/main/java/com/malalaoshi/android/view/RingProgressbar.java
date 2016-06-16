package com.malalaoshi.android.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.malalaoshi.android.R;

/**
 * Created by kang on 16/6/16.
 */
public class RingProgressbar extends View {

    private float ringRadius = 0;
    private float strokeWidth;
    private Paint ringBgPaint;
    private Paint ringPaint;
    private float progress = 0;
    private float progressMax = 100;

    public RingProgressbar(Context context) {
        this(context,null);
    }

    public RingProgressbar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RingProgressbar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        int ringBgColor = context.getResources().getColor(R.color.background);
        int ringColor = context.getResources().getColor(R.color.seniority_color);

        TypedArray typeArray = context.obtainStyledAttributes(attrs, R.styleable.RingProgressbar, defStyleAttr, 0);
        if (typeArray!=null){
            ringBgColor = typeArray.getColor(R.styleable.RingProgressbar_progress_backgroud_color,context.getResources().getColor(R.color.background));
            ringColor = typeArray.getColor(R.styleable.RingProgressbar_progress_color,context.getResources().getColor(R.color.seniority_color));
            ringRadius = typeArray.getDimensionPixelSize(R.styleable.RingProgressbar_progress_ring_radius,100);
            strokeWidth = typeArray.getDimensionPixelSize(R.styleable.RingProgressbar_progress_stroke_width,10);
            ringRadius -= strokeWidth;
            progress = typeArray.getFloat(R.styleable.RingProgressbar_progress_value,0);
            progressMax = typeArray.getFloat(R.styleable.RingProgressbar_progress_max_value,100);
        }
        ringBgPaint = new Paint();
        ringBgPaint.setAntiAlias(true);                       //设置画笔为无锯齿
        ringBgPaint.setColor(ringBgColor);                    //设置画笔颜色
        //ringBgPaint.drawColor(Color.WHITE);                   //白色背景
        ringBgPaint.setStrokeWidth(strokeWidth);             //线宽
        ringBgPaint.setStyle(Paint.Style.STROKE);

        ringPaint = new Paint();
        ringPaint.setAntiAlias(true);                       //设置画笔为无锯齿
        ringPaint.setColor(ringColor);                    //设置画笔颜色
        //ringPaint.drawColor(Color.WHITE);                   //白色背景
        ringPaint.setStrokeWidth(strokeWidth);             //线宽
        ringPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //将画布中心设为原点(0,0), 方便后面计算坐标
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(-90, 0, 0);
        ringBgPaint.setStrokeWidth(strokeWidth);     //设置内圆的厚度

        RectF oval=new RectF();                     //RectF对象
        oval.left=-ringRadius;                              //左边
        oval.top=-ringRadius;                                   //上边
        oval.right=ringRadius;                             //右边
        oval.bottom=ringRadius;                                //下边
        canvas.drawArc(oval, 0, 360, false, ringBgPaint);    //绘制圆弧

        float sweepAngle = 360*progress/progressMax;
        canvas.drawArc(oval,0, sweepAngle, false, ringPaint);    //绘制圆弧

        //float xCenter = (float) ((ringRadius + strokeWidth / 2)*Math.cos(sweepAngle));
        //float yCenter = (float) ((ringRadius + strokeWidth / 2)*Math.sin(sweepAngle));
        //canvas.drawCircle(xCenter, yCenter,strokeWidth , ringPaint);
    }

    public float getRingRadius() {
        return ringRadius;
    }

    public void setRingRadius(float ringRadius) {
        this.ringRadius = ringRadius;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }

    public float getProgressMax() {
        return progressMax;
    }

    public void setProgressMax(float progressMax) {
        this.progressMax = progressMax;
        invalidate();
    }
}
