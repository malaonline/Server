package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.report.entity.AxisModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 波形图
 * Created by tianwei on 5/22/16.
 */
public class WaveView extends View {

    private static final int padding = MiscUtil.dp2px(24);

    private static final int DOTTED_COLOR = Color.parseColor("#9B9B9B");
    private static final int AXIS_COLOR = Color.parseColor("#939393");
    private static final int Y_COLOR = Color.parseColor("#BBDDF6");
    private static final int Y2_COLOR = Color.parseColor("#75cc97");
    private static final int AXIS_TXT_SIZE = MiscUtil.dp2px(9);
    private static final int X_AXIS_TXT_SIZE = MiscUtil.dp2px(10);
    //坐标轴保留长度
    private static final int AXIS_OFFSET = MiscUtil.dp2px(17);
    private static final int AXIS_WIDTH = MiscUtil.dp2px(1);

    private Paint paint;

    //可画图区域宽度
    private float width;
    //可画图区域高度
    private float height;

    //Y轴最大值
    private int max;

    private List<AxisModel> list;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        list = new ArrayList<>();
    }

    public void setList(List<AxisModel> list) {
        this.list = list;
    }

    public void setMax(int max) {
        this.max = max;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }
        intSize();
        drawAxis(canvas);
        drawWave(canvas);
    }

    /**
     * 画波形
     */
    private void drawWave(Canvas canvas) {
        canvas.save();
        canvas.translate(padding, getHeight() - padding);
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        Path path = new Path();
        Path path2 = new Path();
        float cellWidth = (width - AXIS_OFFSET) / (list.size() + 1);
        path.moveTo(0, 0);
        path2.moveTo(0, 0);
        paint.setTextSize(X_AXIS_TXT_SIZE);
        paint.setColor(AXIS_COLOR);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < list.size(); i++) {
            path.lineTo(cellWidth * (i + 1), -list.get(i).getyValue() * (height / max));
            path2.lineTo(cellWidth * (i + 1), -list.get(i).getY2Value() * (height / max));
            if (i == list.size() - 1) {
                path.lineTo(cellWidth * (i + 2), 0);
                path2.lineTo(cellWidth * (i + 2), 0);
            }
            drawText(canvas, list.get(i).getxValue(), cellWidth, i);
        }
        path.close();
        path2.close();
        paint.setColor(Y2_COLOR);
        canvas.drawPath(path2, paint);
        paint.setColor(Y_COLOR);
        canvas.drawPath(path, paint);
        canvas.restore();
    }

    private void drawText(Canvas canvas, String txt, float cellWidth, int position) {
        Rect rect = getTextBounds(paint, txt);
        float x = (float) (cellWidth * (position + 0.5) - rect.width() / 2);
        canvas.drawText(txt, x, rect.height() * 2, paint);
    }

    private Rect getTextBounds(Paint paint, String txt) {
        Rect rect = new Rect();
        if (EmptyUtils.isNotEmpty(txt)) {
            paint.getTextBounds(txt, 0, txt.length(), rect);
        }
        return rect;
    }

    private void intSize() {
        width = getWidth() - 2 * padding;
        height = getHeight() - 2 * padding - AXIS_OFFSET;
    }

    /**
     * 画坐标轴
     */
    private void drawAxis(Canvas canvas) {
        canvas.save();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(AXIS_COLOR);
        paint.setStrokeWidth(AXIS_WIDTH);
        canvas.translate(padding, getHeight() - padding + 1);

        //X轴
        canvas.drawLine(0, 0, 0, -height, paint);
        Path path = new Path();
        path.moveTo(-MiscUtil.dp2px(6), MiscUtil.dp2px(8) - height);
        path.lineTo(0, -height);
        path.lineTo(MiscUtil.dp2px(6), MiscUtil.dp2px(8) - height);
        canvas.drawPath(path, paint);

        //Y轴
        path.reset();
        canvas.drawLine(0, 0, width, 0, paint);
        path.moveTo(width - MiscUtil.dp2px(8), -MiscUtil.dp2px(6));
        path.lineTo(width, 0);
        path.lineTo(width - MiscUtil.dp2px(8), MiscUtil.dp2px(6));
        canvas.drawPath(path, paint);
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(AXIS_TXT_SIZE);
        Rect rect = getTextBounds(paint, "数量");
        canvas.drawText("数量", 0 - rect.width() / 2, -height - rect.height(), paint);
        canvas.drawText("时间", width + rect.height() / 2, rect.height() / 4, paint);

        //网格
        drawNet(canvas);

        canvas.restore();
    }

    /**
     * 画坐标轴网格
     */
    private void drawNet(Canvas canvas) {

        paint.setColor(DOTTED_COLOR);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MiscUtil.dp2px(0.5f));
        paint.setPathEffect(new DashPathEffect(new float[]{3, 2}, 0));
        float len = (height - AXIS_OFFSET) / 3;
        for (int i = 1; i <= 3; i++) {
            Log.i("AABB", len * i + " " + width + " ");
            canvas.drawLine(0, -len * i, width, -len * i, paint);
        }
    }
}
