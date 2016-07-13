package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
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
 * 波形图
 * Created by tianwei on 5/22/16.
 */
public class LineView extends View {

    private static final int DOTTED_COLOR = Color.parseColor("#AA9B9B9B");
    private static final int PATH_COLOR = Color.parseColor("#AA82C9F9"); //曲线颜色
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
    private static final int AXIS_OFFSET = MiscUtil.dp2px(15);
    private static final int AXIS_WIDTH = MiscUtil.dp2px(1);
    //最大柱宽
    private static final int MAX_LINE_WIDTH = MiscUtil.dp2px(18);

    private Paint paint;

    //画坐标轴可换行文字
    private TextPaint textPaint;

    //可画图区域宽度
    private float width;
    //可画图区域高度
    private float height;

    //Y轴最大值
    private int max;

    //Y轴左边文字的宽度
    private float textLeftLen;
    //文字高度
    private float textHeight;
    //X轴右侧文字长度
    private float textRightLen;

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
        textPaint = new TextPaint();
        textPaint.setTextSize(AXIS_TXT_SIZE);
        textPaint.setColor(AXIS_COLOR);
        textPaint.setAntiAlias(true);
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
            max = 100;
        }
        Rect size = Utils.getTextBounds(paint, String.valueOf(max + "%"));
        textLeftLen = size.width();//文字左右各3dp留白
        textHeight = size.height();

        size = Utils.getTextBounds(paint, "知识点");
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
        canvas.translate(textLeftLen + MiscUtil.dp2px(16), getHeight() - textHeight - MiscUtil.dp2px(20));
        drawAxis(canvas);
        drawNet(canvas);
        drawLineChart(canvas);
    }

    private void intSize() {
        width = getWidth() - textLeftLen - textRightLen - MiscUtil.dp2px(16); //16dp留白
        height = getHeight() - 2 * (textHeight) - MiscUtil.dp2px(36);
    }

    private void drawLineChart(Canvas canvas) {
        canvas.save();
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        float cellWidth = (width - AXIS_OFFSET) / (list.size() + 2f / 3);
        float unitHeight = (height - AXIS_OFFSET) / max;
        float startX;
        float x;
        float y;
        float lineWidth;
        float lineSpace;
        lineWidth = cellWidth * 2f / 3;
        if (lineWidth > MAX_LINE_WIDTH) {
            lineWidth = MAX_LINE_WIDTH;
        }
        lineSpace = (cellWidth - lineWidth) / 2;
        List<Point> points = new ArrayList<>();
        paint.setStyle(Paint.Style.FILL);
        for (int i = 0; i < list.size(); i++) {
            paint.setColor(COLOR_LIST.get(i % COLOR_LIST.size()));
            AxisModel model = list.get(i);
            startX = AXIS_OFFSET + cellWidth * i;
            canvas.drawRect(startX + lineSpace, -unitHeight * model.getyValue(), startX + lineWidth + lineSpace, 0,
                    paint);
            drawText(canvas, model.getxValue(), cellWidth, i);
            x = startX + cellWidth / 2f;
            y = -unitHeight * model.getY2Value();
            if (i == 0) {
                float yy = unitHeight * model.getY2Value() - AXIS_OFFSET / 2;
                yy = yy > 0 ? yy : 0;
                points.add(new Point(AXIS_OFFSET / 2, (int) -yy));
            }
            points.add(new Point((int) x, (int) y));
            if (i == list.size() - 1) {
                float yy = unitHeight * model.getY2Value() - AXIS_OFFSET / 2;
                yy = yy > 0 ? yy : 0;
                points.add(new Point((int) (startX + cellWidth), (int) -yy));
            }
        }
        paint.reset();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(MiscUtil.dp2px(1));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(PATH_COLOR);
        drawPointCurvePath(canvas, points);
        //画平均线圈圈
        float yy;
        paint.setStrokeWidth(MiscUtil.dp2px(1f));
        for (int i = 0; i < list.size(); i++) {
            startX = AXIS_OFFSET + cellWidth * i + cellWidth / 2;
            yy = -list.get(i).getY2Value() * unitHeight;
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(PATH_COLOR);
            canvas.drawCircle(startX, yy, MiscUtil.dp2px(4), paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.WHITE);
            canvas.drawCircle(startX, yy, MiscUtil.dp2px(3.5f), paint);
        }
    }

    private void drawText(Canvas canvas, String txt, float cellWidth, int position) {
        StaticLayout layout = new StaticLayout(txt, textPaint, (int) cellWidth, Layout.Alignment.ALIGN_CENTER, 1f, 1f,
                true);
        canvas.save();
        canvas.translate(AXIS_OFFSET + cellWidth * position, MiscUtil.dp2px(5));
        layout.draw(canvas);
        canvas.restore();
    }

    private void drawPointCurvePath(Canvas canvas, List<Point> points) {
        Point startP;
        Point endP;
        Point p3 = new Point();
        Point p4 = new Point();
        Path path = new Path();

        if (null == points || 0 == points.size()) {
            return;
        }

        startP = points.get(0);
        path.moveTo(startP.x, startP.y);
        int xCenter;

        for (int i = 0; i < points.size() - 1; i++) {
            startP = points.get(i);
            endP = points.get(i + 1);
            xCenter = (startP.x + endP.x) / 2;
            p3.y = startP.y;
            p3.x = xCenter;
            p4.y = endP.y;
            p4.x = xCenter;
            path.cubicTo(p3.x, p3.y, p4.x, p4.y, endP.x, endP.y);
        }
        canvas.drawPath(path, paint);
    }

    /**
     * 画坐标轴
     */
    private void drawAxis(Canvas canvas) {
        canvas.save();
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(AXIS_COLOR);
        paint.setStrokeWidth(AXIS_WIDTH);

        //Y轴
        canvas.drawLine(0, 0, 0, -height, paint);
        Path path = new Path();
        path.moveTo(-MiscUtil.dp2px(3), MiscUtil.dp2px(8) - height);
        path.lineTo(0, -height);
        path.lineTo(MiscUtil.dp2px(3), MiscUtil.dp2px(8) - height);
        canvas.drawPath(path, paint);

        //X轴
        int space = PADDING / 2;
        path.reset();
        canvas.drawLine(0, 1, width - space, 1, paint);
        path.moveTo(width - space - MiscUtil.dp2px(8), -MiscUtil.dp2px(3));
        path.lineTo(width - space, 0);
        path.lineTo(width - space - MiscUtil.dp2px(8), MiscUtil.dp2px(3));
        canvas.drawPath(path, paint);
        canvas.drawPath(path, paint);

        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(AXIS_TXT_SIZE);
        Rect rect = Utils.getTextBounds(paint, "分数");
        canvas.drawText("数量", 0 - rect.width() / 2, -height - rect.height(), paint);
        canvas.drawText("知识点", width + rect.height() / 2 - space, rect.height() / 4, paint);
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
        float len = (height - AXIS_OFFSET) / 4;
        for (int i = 1; i <= 4; i++) {
            path.reset();
            path.moveTo(0, -len * i);
            path.lineTo(width, -len * i);
            canvas.drawPath(path, paint);
        }
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        drawNetText(canvas);
        paint.setPathEffect(null);
    }

    /**
     * 写Y坐标文字
     */
    private void drawNetText(Canvas canvas) {
        paint.setColor(AXIS_COLOR);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(AXIS_TXT_SIZE);
        float len = (height - AXIS_OFFSET) / 4;
        for (int i = 1; i <= 4; i++) {
            canvas.drawText((max / 4 * i) + "%", -textLeftLen - MiscUtil.dp2px(3), -len * i + textHeight * 5 / 8,
                    paint);
        }
    }

    /**
     * 测试用
     */
    public void updateTestData(boolean add) {
        if (EmptyUtils.isEmpty(list)) {
            return;
        }
        if (!add && list.size() < 2) {
            return;
        }
        if (add) {
            list.add(list.get(new Random().nextInt(list.size())));
        } else {
            list.remove(list.size() - 1);
        }
        invalidate();
    }
}
