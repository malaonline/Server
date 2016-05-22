package com.malalaoshi.android.report.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private static final int AXIS_TXT_SIZE = MiscUtil.dp2px(10);
    private static final int Y_AXIS_TXT_COLOR = Color.parseColor("#5E5E5E");
    private static final int Y_AXIS_RIGHT_TXT_COLOR = Color.parseColor("#97A8BB");

    private Paint paint;

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
        float txtAxisY;
        String percent;
        Rect rect;
        paint.setTextSize(AXIS_TXT_SIZE);

        float cellHeight = height / list.size();

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
            //右边文字
            paint.setColor(Y_AXIS_RIGHT_TXT_COLOR);
            txtAxisY = cellHeight * (i + 0.5f) - txtHeight / 8;
            canvas.drawText(percent, width - TXT_MARGIN - rect.width(), txtAxisY, paint);

            rect = Utils.getTextBounds(paint, model.getxValue());
            if (rect.width() > maxLeftTextWidth) {
                maxLeftTextWidth = rect.width();
            }
            //左边文字
            paint.setColor(Y_AXIS_TXT_COLOR);
            canvas.drawText(model.getxValue(), TXT_MARGIN, txtAxisY, paint);

        }

        float backWidth = width - TXT_MARGIN * 4 - maxLeftTextWidth - maxRightTextWidth;
        float backBegin = maxLeftTextWidth + TXT_MARGIN * 2;
        float frontWidth;
        float lineHeight = cellHeight * 0.7f;
        String frontTxt;
        RectF rectF;
        Rect frontTxtRect;
        for (int i = 0; i < list.size(); i++) {
            AxisModel item = list.get(i);

            //背景
            rectF = new RectF(backBegin, cellHeight * i, backBegin + backWidth, cellHeight * i + lineHeight);
            paint.setColor(LINE_BK_COLOR);
            canvas.drawRoundRect(rectF, lineHeight / 2, lineHeight / 2, paint);

            //前景
            frontWidth = backWidth * (item.getyValue() * 1f / item.getY2Value());
            rectF = new RectF(backBegin, cellHeight * i, backBegin + frontWidth, cellHeight * i + lineHeight);
            paint.setColor(COLOR_LIST.get(i));
            canvas.drawRoundRect(rectF, lineHeight / 2, lineHeight / 2, paint);

            //前景文字
            paint.setColor(Color.WHITE);
            frontTxt = item.getyValue() + "/" + item.getY2Value();
            frontTxtRect = Utils.getTextBounds(paint, frontTxt);
            canvas.drawText(frontTxt, backBegin + frontWidth - frontTxtRect.width() - TXT_MARGIN,
                    cellHeight * (i + 0.5f) - frontTxtRect.height() / 8, paint);

        }
    }

    private void intSize() {
        width = getWidth();
        height = getHeight();
    }
}
