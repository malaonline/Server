package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
 * 波形图
 * Created by tianwei on 5/22/16.
 */
public class HorizontalLineView extends View {

    /**
     * 文字留白
     */
    private static final int TXT_MARGIN = MiscUtil.dp2px(5);

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

    private static final int LINE_BK_COLOR = Color.parseColor("#F2F2F2");
    private static final int LINE_MAX_HEIGHT = MiscUtil.dp2px(15);
    private static final int AXIS_TXT_SIZE = MiscUtil.dp2px(10);
    private static final int Y_AXIS_TXT_COLOR = Color.parseColor("#5E5E5E");
    private static final int Y_AXIS_RIGHT_TXT_COLOR = Color.parseColor("#97A8BB");

    private Paint paint;
    private TextPaint textPaint;
    private Paint.FontMetricsInt fontMetrics;

    //可画图区域宽度
    private float width;
    //可画图区域高度
    private float height;

    private List<AxisModel> list;

    public HorizontalLineView(Context context) {
        super(context);
        init();
    }

    public HorizontalLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(AXIS_TXT_SIZE);
        fontMetrics = textPaint.getFontMetricsInt();
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
        drawLine(canvas);
    }

    private void drawLine(Canvas canvas) {
        float maxLeftTextWidth = 0;
        float maxRightTextWidth = 0;
        float txtHeight = 0;
        String percent;
        Rect rect;
        paint.setTextSize(AXIS_TXT_SIZE);

        float cellHeight = height / list.size();

        int baseLine;
        //先求文字的留白大小
        for (int i = 0; i < list.size(); i++) {
            AxisModel model = list.get(i);
            percent = (model.getyValue() * 100 / model.getY2Value()) + "%";
            rect = Utils.getTextBounds(paint, percent);
            if (txtHeight == 0) {
                txtHeight = rect.height();
            }
            if (rect.width() > maxRightTextWidth) {
                maxRightTextWidth = rect.width();
            }
            baseLine = (int) (cellHeight * i + (cellHeight - fontMetrics.bottom - fontMetrics.top) / 2);
            //右边文字
            paint.setColor(Y_AXIS_RIGHT_TXT_COLOR);
            canvas.drawText(percent, width - TXT_MARGIN - rect.width(), baseLine, paint);

            rect = Utils.getTextBounds(paint, model.getxValue());
            if (rect.width() > maxLeftTextWidth) {
                maxLeftTextWidth = rect.width();
            }
            //左边文字
            paint.setColor(Y_AXIS_TXT_COLOR);
            canvas.drawText(model.getxValue(), TXT_MARGIN, baseLine, paint);

        }

        float backWidth = width - TXT_MARGIN * 4 - maxLeftTextWidth - maxRightTextWidth;
        float backBegin = maxLeftTextWidth + TXT_MARGIN * 2;
        float frontWidth;
        float lineHeight = cellHeight * 0.7f;
        if (lineHeight > LINE_MAX_HEIGHT) {
            lineHeight = LINE_MAX_HEIGHT;
        }
        float lineSpace = (cellHeight - lineHeight) / 2;
        float txtBegin;
        String frontTxt;
        RectF rectF;
        Rect frontTxtRect;
        for (int i = 0; i < list.size(); i++) {
            AxisModel item = list.get(i);

            //背景
            rectF = new RectF(backBegin, cellHeight * i + lineSpace, backBegin + backWidth,
                    cellHeight * i + lineHeight + lineSpace);
            paint.setColor(LINE_BK_COLOR);
            canvas.drawRoundRect(rectF, lineHeight / 2, lineHeight / 2, paint);

            //前景
            frontWidth = backWidth * (item.getyValue() * 1f / item.getY2Value());
            rectF.right = backBegin + frontWidth;
            paint.setColor(COLOR_LIST.get(i % COLOR_LIST.size()));
            canvas.drawRoundRect(rectF, lineHeight / 2, lineHeight / 2, paint);

            //前景文字
            textPaint.setColor(Color.WHITE);
            frontTxt = item.getyValue() + "/" + item.getY2Value();
            frontTxtRect = Utils.getTextBounds(paint, frontTxt);
            baseLine = (int) ((rectF.bottom + rectF.top - fontMetrics.bottom - fontMetrics.top) / 2);
            txtBegin = backBegin + frontWidth - frontTxtRect.width() - MiscUtil.dp2px(8);
            txtBegin = txtBegin < backBegin ? backBegin : txtBegin;
            canvas.drawText(frontTxt, txtBegin, baseLine, textPaint);
        }
    }

    private void intSize() {
        width = getWidth();
        height = getHeight();
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
