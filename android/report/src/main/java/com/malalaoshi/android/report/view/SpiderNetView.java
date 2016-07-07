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
import com.malalaoshi.android.report.Utils;
import com.malalaoshi.android.report.entity.AxisModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 波形图
 * Created by tianwei on 5/22/16.
 */
public class SpiderNetView extends View {

    private static final class Pair {
        private float x;
        private float y;

        public Pair(double x, double y) {
            this.x = (float) x;
            this.y = (float) y;
        }
    }

    private static final int SIZE = MiscUtil.dp2px(230);

    private static final double FPI = Math.PI / 5;

    private static List<Pair> ANGLE_LIST;

    static {
        ANGLE_LIST = new ArrayList<>();
        ANGLE_LIST.add(new Pair(Math.sin(7 * FPI), Math.cos(7 * FPI)));
        ANGLE_LIST.add(new Pair(Math.sin(9 * FPI), Math.cos(9 * FPI)));
        ANGLE_LIST.add(new Pair(Math.sin(FPI), Math.cos(FPI)));
        ANGLE_LIST.add(new Pair(Math.sin(3 * FPI), Math.cos(3 * FPI)));
        ANGLE_LIST.add(new Pair(Math.sin(5 * FPI), Math.cos(5 * FPI)));
    }

    private static final int BACK_COLOR = Color.parseColor("#C9E4F8");
    private static final int FRONT_COLOR = Color.parseColor("#F9877c");
    private static final int TXT_COLOR = Color.parseColor("#5E5E5E");
    private static final int TXT_OFFSET = MiscUtil.dp2px(3);
    private static final int MAX = 100;
    //环线数
    private static final int SPIDER_RINGS = 7;
    //边缘点点的半径
    private static final int EDGE_RADIUS = MiscUtil.dp2px(2);


    private Paint paint;

    //可画图区域宽度
    private float width;
    //可画图区域高度
    private float height;

    private Path path;

    private List<AxisModel> list;

    public SpiderNetView(Context context) {
        super(context);
        init();
    }

    public SpiderNetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        path = new Path();
        list = new ArrayList<>();
    }

    public void setList(List<AxisModel> list) {
        this.list = list;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }
        intSize();
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        canvas.translate(width / 2, height / 2);
        drawNet(canvas);
        drawData(canvas);
        drawDataText(canvas);
    }

    private void intSize() {
        width = getWidth();
        height = getHeight();
    }


    private void drawData(Canvas canvas) {
        if (EmptyUtils.isEmpty(list) || list.size() < 5) {
            return;
        }
        //背景
        path.reset();
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < ANGLE_LIST.size(); i++) {
            AxisModel model = list.get(i);
            float value = model.getyValue() > 0 ? model.getyValue() : 2;
            value = value * SIZE / 2 / MAX;
            Pair pair = ANGLE_LIST.get(ANGLE_LIST.size() - i - 1);
            if (i == 0) {
                path.moveTo(value * pair.x, value * pair.y);
            } else {
                path.lineTo(value * pair.x, value * pair.y);
            }
        }
        path.close();
        paint.setColor(FRONT_COLOR);
        paint.setAlpha(230);
        canvas.drawPath(path, paint);
        paint.setAlpha(255);
    }


    private void drawDataText(Canvas canvas) {
        float x = 0;
        float y = 0;
        paint.setTextSize(MiscUtil.dp2px(10));
        paint.setColor(TXT_COLOR);
        for (int i = 0; i < ANGLE_LIST.size(); i++) {
            AxisModel model = list.get(i);
            Pair pair = ANGLE_LIST.get(ANGLE_LIST.size() - i - 1);
            Rect rect = Utils.getTextBounds(paint, model.getxValue());
            if (i == 2 || i == 3) {
                x = SIZE / 2 * pair.x - rect.width() / 2;
                y = SIZE / 2 * pair.y + rect.height() + TXT_OFFSET;
            } else if (i == 1) {
                x = SIZE / 2 * pair.x + TXT_OFFSET;
                y = SIZE / 2 * pair.y + rect.height() / 2;
            } else if (i == 0) {
                x = SIZE / 2 * pair.x - rect.width() / 2;
                y = SIZE / 2 * pair.y - rect.height() / 2;
            } else if (i == 4) {
                x = SIZE / 2 * pair.x - rect.width() - TXT_OFFSET;
                y = SIZE / 2 * pair.y + rect.height() / 2;
            }
            canvas.drawText(model.getxValue(), x, y, paint);
        }
    }


    /**
     * 网内有4跟环线。半径比例 4:5:6:7
     */
    private void drawNet(Canvas canvas) {
        int unit = SIZE / SPIDER_RINGS / 2;
        //中心

        //背景
        path.reset();
        paint.setColor(BACK_COLOR);
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < ANGLE_LIST.size(); i++) {
            Pair pair = ANGLE_LIST.get(i);
            if (i == 0) {
                path.moveTo(SIZE / 2 * pair.x, SIZE / 2 * pair.y);
            } else {
                path.lineTo(SIZE / 2 * pair.x, SIZE / 2 * pair.y);
            }
        }
        path.close();
        canvas.drawPath(path, paint);

        //网线
        paint.setStrokeWidth(MiscUtil.dp2px(1));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.WHITE);
        for (int i = 2; i < SPIDER_RINGS; i++) {
            drawNetRing(unit * i, canvas);
        }

        //画辐射线
        paint.setStyle(Paint.Style.FILL);
        float len = unit * (SPIDER_RINGS - 0.5f);
        for (Pair pair : ANGLE_LIST) {
            canvas.drawLine(0, 0, len * pair.x, len * pair.y, paint);
            canvas.drawCircle(len * pair.x, len * pair.y, EDGE_RADIUS, paint);
        }
    }

    /**
     * 画蛛网环线
     */
    private void drawNetRing(float radius, Canvas canvas) {
        path.reset();
        for (int i = 0; i < ANGLE_LIST.size(); i++) {
            Pair pair = ANGLE_LIST.get(i);
            if (i == 0) {
                path.moveTo(radius * pair.x, radius * pair.y);
            } else {
                path.lineTo(radius * pair.x, radius * pair.y);
            }
        }
        path.close();
        canvas.drawPath(path, paint);
    }
}
