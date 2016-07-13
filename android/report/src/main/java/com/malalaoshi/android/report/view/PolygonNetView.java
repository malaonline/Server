package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.report.Utils;
import com.malalaoshi.android.report.entity.AxisModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 这个多变形的网支持的范围是点的个数>3
 * Created by tianwei on 7/10/16.
 * 中心网由圆(半径 r)组成。
 * 外面的字的显示在圆的外围。字的长度是w,高度是h。显示的字中心由三角函数方程:
 * x = r*cos(angle); y = r*sin(angle);
 * 数字的完美最大长度是四个汉字
 */
public class PolygonNetView extends View {

    private static final class Point {
        private float x;
        private float y;

        public Point(double x, double y) {
            this.x = (float) x;
            this.y = (float) y;
        }
    }

    private static final int BACK_COLOR = Color.parseColor("#C9E4F8");
    private static final int FRONT_COLOR = Color.parseColor("#F9877c");
    private static final int TXT_COLOR = Color.parseColor("#5E5E5E");
    private static final int TXT_OFFSET = MiscUtil.dp2px(5);
    private static final int TXT_SIZE = MiscUtil.dp2px(10);
    private static final int SIZE = MiscUtil.dp2px(200);
    private static final int R = SIZE / 2;//半径
    //边缘点点的半径
    private static final int EDGE_RADIUS = MiscUtil.dp2px(2);
    //环线数
    private static final int SPIDER_RINGS = 7;
    private static final int MAX = 100;
    private static final String STD_TXT = "标准数据";

    private Paint paint;
    private TextPaint textPaint;
    private List<AxisModel> list;
    private List<Point> points; //顶点
    private List<Point> txtPoints;//文字的中心点
    private Rect txtBounds;
    //可画图区域宽度
    private float width;
    //可画图区域高度
    private float height;
    private Path path;

    public PolygonNetView(Context context) {
        super(context);
        initView();
    }

    public PolygonNetView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        paint = new Paint();
        paint.setAntiAlias(true);
        textPaint = new TextPaint();
        textPaint.setTextSize(TXT_SIZE);
        textPaint.setColor(TXT_COLOR);
        textPaint.setAntiAlias(true);
        textPaint.setTextAlign(Paint.Align.CENTER);
        list = new ArrayList<>();
        points = new ArrayList<>();
        txtPoints = new ArrayList<>();
        path = new Path();
    }

    public void setData(List<AxisModel> list) {
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        this.list.addAll(list);
        initPoints();
    }

    private void initPoints() {
        points.clear();
        txtPoints.clear();
        txtBounds = Utils.getTextBounds(textPaint, STD_TXT);
        double angle = 2 * Math.PI / list.size();
        for (int i = 0; i < list.size(); i++) {
            Point p = new Point(Math.cos(angle * i - Math.PI / 2), Math.sin(angle * i - Math.PI / 2));
            points.add(p);
            float x = p.x * (txtBounds.width() / 2 + R + TXT_OFFSET);
            float y = p.y * (txtBounds.height() / 2 + R + TXT_OFFSET);
            Point tp = new Point(x, y);
            txtPoints.add(tp);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isInEditMode()) {
            super.onDraw(canvas);
            return;
        }
        initSize();
        if (list == null || list.size() < 3) {
            return;
        }
        canvas.translate(width / 2, height / 2);
        drawNet(canvas);
        drawData(canvas);
        drawDataText(canvas);
    }

    private void initSize() {
        width = getWidth();
        height = getHeight();
    }

    /**
     * 仅仅测试用
     */
    public void updateData(boolean add) {
        if (EmptyUtils.isEmpty(list) || list.size() <= 3 && !add) {
            return;
        }
        if (add) {
            list.add(list.get(new Random().nextInt(list.size())));
        } else {
            list.remove(list.size() - 1);
        }
        initPoints();
        invalidate();
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

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (i == 0) {
                path.moveTo(R * p.x, R * p.y);
            } else {
                path.lineTo(R * p.x, R * p.y);
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
        for (Point p : points) {
            canvas.drawLine(0, 0, len * p.x, len * p.y, paint);
            canvas.drawCircle(len * p.x, len * p.y, EDGE_RADIUS, paint);
        }
    }

    /**
     * 画蛛网环线
     */
    private void drawNetRing(float radius, Canvas canvas) {
        path.reset();
        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (i == 0) {
                path.moveTo(radius * p.x, radius * p.y);
            } else {
                path.lineTo(radius * p.x, radius * p.y);
            }
        }
        path.close();
        canvas.drawPath(path, paint);
    }

    private void drawData(Canvas canvas) {
        //背景
        path.reset();
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < list.size(); i++) {
            AxisModel model = list.get(i);
            float value = model.getyValue() > 0 ? model.getyValue() : 2;//默认最小值是2
            value = value * R / MAX;
            Point pair = points.get(i);
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
        float x;
        float y;
        RectF target;
        Paint.FontMetricsInt fontMetrics = textPaint.getFontMetricsInt();
        for (int i = 0; i < list.size(); i++) {
            AxisModel model = list.get(i);
            Point center = txtPoints.get(i);
            x = center.x - txtBounds.width() / 2;
            y = center.y - txtBounds.height() / 2;
            target = new RectF(x, y, x + txtBounds.width(), y + txtBounds.height());
            int baseLine = (int) ((target.bottom + target.top - fontMetrics.bottom - fontMetrics.top) / 2);
            canvas.drawText(model.getxValue(), center.x, baseLine, textPaint);
        }
    }


}
