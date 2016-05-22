package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
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
public class LineView extends View {

    private static final List<Integer> COLOR_LIST;

    static {
        COLOR_LIST = new ArrayList<>(7);
        COLOR_LIST.add(Color.parseColor("#F8DB6B"));
        COLOR_LIST.add(Color.parseColor("#6DC9CE"));
        COLOR_LIST.add(Color.parseColor("#F9877C"));
        COLOR_LIST.add(Color.parseColor("#75CC97"));
        COLOR_LIST.add(Color.parseColor("#88BCDE"));
        COLOR_LIST.add(Color.parseColor("#F7AF63"));
        COLOR_LIST.add(Color.parseColor("#BA9CDA"));
    }

    private static final int PADDING = MiscUtil.dp2px(24);

    private static final int AXIS_COLOR = Color.parseColor("#939393");
    private static final int AXIS_TXT_SIZE = MiscUtil.dp2px(9);
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

    public LineView(Context context) {
        super(context);
        init();
    }

    public LineView(Context context, AttributeSet attrs) {
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
        drawLineChart(canvas);
    }

    private void drawLineChart(Canvas canvas) {
        canvas.save();
        canvas.translate(PADDING, getHeight() - PADDING);
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        float cellWidth = (width - AXIS_OFFSET) / (list.size() + 1);
        paint.setStyle(Paint.Style.FILL);
        float unitHeight = height / max;
        for (int i = 0; i < list.size(); i++) {
            paint.setColor(COLOR_LIST.get(i));
            AxisModel model = list.get(i);
            canvas.drawRect(cellWidth * (1.0f / 4 + i), -unitHeight * model.getyValue(),
                    cellWidth * (i + 1), 0, paint);
            drawText(canvas, model.getxValue(), cellWidth, i);
        }
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
        width = getWidth() - 2 * PADDING;
        height = getHeight() - 2 * PADDING - AXIS_OFFSET;
    }

    /**
     * 画坐标轴
     */
    private void drawAxis(Canvas canvas) {
        canvas.save();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(AXIS_COLOR);
        paint.setStrokeWidth(AXIS_WIDTH);
        canvas.translate(PADDING, getHeight() - PADDING + 1);

        //X轴
        canvas.drawLine(0, 0, 0, -height, paint);
        Path path = new Path();
        path.moveTo(-MiscUtil.dp2px(6), MiscUtil.dp2px(8) - height);
        path.lineTo(0, -height);
        path.lineTo(MiscUtil.dp2px(6), MiscUtil.dp2px(8) - height);
        canvas.drawPath(path, paint);

        //Y轴
        int space = PADDING / 2;
        path.reset();
        canvas.drawLine(0, 0, width - space, 0, paint);
        path.moveTo(width - space - MiscUtil.dp2px(8), -MiscUtil.dp2px(6));
        path.lineTo(width - space, 0);
        path.lineTo(width - space - MiscUtil.dp2px(8), MiscUtil.dp2px(6));
        canvas.drawPath(path, paint);
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(AXIS_TXT_SIZE);
        Rect rect = getTextBounds(paint, "分数");
        canvas.drawText("数量", 0 - rect.width() / 2, -height - rect.height(), paint);
        canvas.drawText("知识点", width + rect.height() / 2 - space, rect.height() / 4, paint);

        canvas.restore();
    }
}
