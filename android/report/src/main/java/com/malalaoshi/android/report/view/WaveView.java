package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.report.Utils;
import com.malalaoshi.android.report.entity.AxisModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 波形图
 * Created by tianwei on 5/22/16.
 */
public class WaveView extends View {

    private static final int DOTTED_COLOR = Color.parseColor("#AA9B9B9B");
    private static final int AXIS_COLOR = Color.parseColor("#939393");
    private static final int Y2_COLOR = Color.parseColor("#BBDDF6");
    private static final int Y_COLOR = Color.parseColor("#75cc97");
    private static final int AXIS_TXT_SIZE = MiscUtil.dp2px(9);
    private static final int X_AXIS_TXT_SIZE = MiscUtil.dp2px(10);
    //波形图在坐标轴中留白长度
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

    //Y轴左边文字占用长度
    private float textLeftLen;

    //文字高度
    private float textHeight;

    //X轴右边文字高度
    private float textRightLen;

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
        initTextSize();
    }

    /**
     * 计算坐标轴文字占用的空间
     */
    private void initTextSize() {
        //y轴左边文字的计算
        paint.setTextSize(AXIS_TXT_SIZE);
        if (max == 0) {
            max = 150;
        }
        Rect size = Utils.getTextBounds(paint, String.valueOf(max));
        textLeftLen = size.width();//文字左右各3dp留白

        //Y轴上放文字高度
        textHeight = size.height();

        //X轴文字宽度
        size = Utils.getTextBounds(paint, "时间");
        textRightLen = size.width();

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
        canvas.translate(textLeftLen + MiscUtil.dp2px(6), getHeight() - textHeight - MiscUtil.dp2px(16));
        drawAxis(canvas);
        drawWave(canvas);
        drawNet(canvas);
        drawNetText(canvas);
    }


    private void intSize() {
        width = getWidth() - (textLeftLen + textRightLen + MiscUtil.dp2px(20)); //文字6dp的留白
        height = getHeight() - (2 * textHeight + MiscUtil.dp2px(30));//文字16dp的留白
    }

    /**
     * 画坐标轴
     */
    private void drawAxis(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(AXIS_COLOR);
        paint.setStrokeWidth(AXIS_WIDTH);
        Path path = new Path();

        //X轴
        canvas.drawLine(0, 0, width, 0, paint);
        //X轴箭头
        path.moveTo(width - MiscUtil.dp2px(8), -MiscUtil.dp2px(3));
        path.lineTo(width, 0);
        path.lineTo(width - MiscUtil.dp2px(8), MiscUtil.dp2px(3));
        canvas.drawPath(path, paint);

        //Y轴
        canvas.drawLine(0, 0, 0, -height, paint);
        path.reset();
        //Y轴箭头
        path.moveTo(-MiscUtil.dp2px(3), MiscUtil.dp2px(8) - height);
        path.lineTo(0, -height);
        path.lineTo(MiscUtil.dp2px(3), MiscUtil.dp2px(8) - height);
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(AXIS_TXT_SIZE);
        Rect rect = Utils.getTextBounds(paint, "数量");
        canvas.drawText("数量", 0 - rect.width() / 2, -height - rect.height(), paint);
        canvas.drawText("时间", width + rect.height() / 2, rect.height() / 4, paint);
    }

    /**
     * 画波形
     */
    private void drawWave(Canvas canvas) {
        canvas.save();
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
        float maxHeight = height - AXIS_OFFSET;
        for (int i = 0; i < list.size(); i++) {
            path.lineTo(cellWidth * (i + 1), -list.get(i).getyValue() * (maxHeight / max));
            path2.lineTo(cellWidth * (i + 1), -list.get(i).getY2Value() * (maxHeight / max));
            if (i == list.size() - 1) {
                path.lineTo(cellWidth * (i + 2), 0);
                path2.lineTo(cellWidth * (i + 2), 0);
            }
        }
        path.close();
        path2.close();
        paint.setColor(Y2_COLOR);
        canvas.drawPath(path2, paint);
        paint.setColor(Y_COLOR);
        canvas.drawPath(path, paint);
    }

    /**
     * 写网格上的坐标文字
     */
    private void drawNetText(Canvas canvas) {
        paint.setColor(AXIS_COLOR);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(AXIS_TXT_SIZE);
        float len = (height - AXIS_OFFSET) / 3;
        for (int i = 1; i <= 3; i++) {
            canvas.drawText((max / 3 * i) + "", -textLeftLen - MiscUtil.dp2px(3), -len * i + textHeight * 5 / 8, paint);
        }

        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        Rect rect;
        float cellWidth = width / (list.size() + 1);
        float x;
        for (int i = 0; i < list.size(); i++) {
            AxisModel model = list.get(i);
            rect = Utils.getTextBounds(paint, model.getxValue());
            x = cellWidth * (i + 1) - rect.width() / 2;
            canvas.drawText(model.getxValue()==null?"":model.getxValue(), x, rect.height() + MiscUtil.dp2px(7), paint);
        }
    }

    /**
     * 画坐标轴网格
     */
    private void drawNet(Canvas canvas) {
        Path path = new Path();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(MiscUtil.dp2px(0.5f));
        paint.setPathEffect(new DashPathEffect(new float[]{5, 5, 5, 5}, 1));
        paint.setColor(DOTTED_COLOR);

        //X轴方向
        float len = (height - AXIS_OFFSET) / 3;
        for (int i = 1; i <= 3; i++) {
            path.reset();
            path.moveTo(0, -len * i);
            path.lineTo(width, -len * i);
            canvas.drawPath(path, paint);
        }
        if (EmptyUtils.isEmpty(list)) {
            return;
        }

        //Y轴方向
        len = width / (list.size() + 1);
        for (int i = 1; i <= list.size(); i++) {
            path.reset();
            path.moveTo(len * i, 0);
            path.lineTo(len * i, -height);
            canvas.drawPath(path, paint);
        }
    }
}
