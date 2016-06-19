package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.report.entity.PieModel;

import java.util.ArrayList;
import java.util.List;

/**
 * 饼图
 * Created by tianwei on 5/21/16.
 */
public class PieView extends View {

    private static final int MID_TXT_COLOR = Color.parseColor("#939393");
    private static final int MID_TXT_SIZE = MiscUtil.dp2px(12); //px
    private static final int ARC_TXT_SIZE = MiscUtil.dp2px(9); //px

    private List<PieModel> list;
    //外环
    private RectF big;
    //中环
    private RectF mid;
    //内圆
    private RectF inner;
    //整个图形宽度等分20份（圆形从内到外半径 2:2:1)
    private float len;
    private float center;
    private Paint paint;
    private Path path;
    private String centerText;

    public PieView(Context context) {
        super(context);
        init();
    }

    public void setData(List<PieModel> dataList) {
        if (EmptyUtils.isNotEmpty(dataList)) {
            list.addAll(dataList);
        }
    }

    public PieView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        list = new ArrayList<>();
        big = new RectF(0, 0, 0, 0);
        inner = new RectF(0, 0, 0, 0);
        mid = new RectF(0, 0, 0, 0);
        paint = new Paint();
        paint.setAntiAlias(true);
        path = new Path();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        initSize();
        if (EmptyUtils.isEmpty(list)) {
            super.onDraw(canvas);
            return;
        }
        drawCenterCircle(canvas);
        drawCenterText(canvas);
        paint.setTextSize(ARC_TXT_SIZE);
        for (PieModel item : list) {

            paint.setColor(item.getColor());
            paint.setStyle(Paint.Style.STROKE);

            //中环形图
            path.reset();
            paint.setAlpha(255);
            path.arcTo(mid, item.getBeginAngle(), item.getSwapAngle());
            paint.setStrokeWidth(len * 4);
            canvas.drawPath(path, paint);

            //外环形图
            path.reset();
            paint.setAlpha(50);
            paint.setStrokeWidth(len * 2);
            path.arcTo(big, item.getBeginAngle(), item.getSwapAngle());
            canvas.drawPath(path, paint);

            drawArcText(canvas, item);

        }
    }

    /**
     * 绘制圆弧上的文字
     */
    private void drawArcText(Canvas canvas, PieModel item) {
        paint.setAlpha(255);
        paint.setStyle(Paint.Style.FILL);
        String txt = item.getNum() + "%";
        Rect rect = getTextBounds(paint, txt);
        float angle = item.getBeginAngle() + item.getSwapAngle() / 2;
        float x = (float) (center + 9 * len * Math.cos(angle * Math.PI / 180));
        float y = (float) (center + 9 * len * Math.sin(angle * Math.PI / 180));
        canvas.drawText(txt, x - rect.width() / 2, y + rect.height() / 8, paint);
    }

    /**
     * 中心文字
     *
     * @param canvas 画布
     */
    private void drawCenterText(Canvas canvas) {
        if (EmptyUtils.isEmpty(centerText)) {
            return;
        }

        paint.setColor(MID_TXT_COLOR);
        paint.setTextSize(MID_TXT_SIZE);
        Rect rect = getTextBounds(paint, centerText);
        canvas.drawText(centerText, center - rect.width() / 2, center + rect.width() / 8, paint);
    }

    private Rect getTextBounds(Paint paint, String txt) {
        Rect rect = new Rect();
        if (EmptyUtils.isNotEmpty(txt)) {
            paint.getTextBounds(txt, 0, txt.length(), rect);
        }
        return rect;
    }

    /**
     * 中心圆
     *
     * @param canvas 画布
     */
    private void drawCenterCircle(Canvas canvas) {
        paint.setColor(Color.WHITE);
        canvas.drawCircle(center, center, len * 10, paint);
    }

    /**
     * 宽度等分成20份。
     */
    private void initSize() {
        float width = getWidth();
        float height = getHeight();
        len = width / 20;
        center = width / 2;
        big.set(len, len, width - len, height - len);
        mid.set(len * 4, len * 4, len * 16, len * 16);
        inner.set(len * 6, len * 6, len * 14, len * 14);
    }

    public void setCenterText(String txt) {
        centerText = txt;
    }

}
