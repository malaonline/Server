package com.malalaoshi.android.view.calendar;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.text.format.DateUtils;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;


import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.SimpleMonthAdapter;
import com.malalaoshi.android.entity.Cource;
import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.ViewUtils;

import java.security.InvalidParameterException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SimpleMonthView extends View {

    public static final String VIEW_PARAMS_MONTH = "month";
    public static final String VIEW_PARAMS_YEAR = "year";
    public static final String VIEW_PARAMS_WEEK_START = "week_start";

    private static final int NORMAL_ALPHA = 255;
    protected static final int DEFAULT_NUM_ROWS = 6;


    protected int topPadding;
    protected int bottomPadding;
    protected int leftPadding;
    protected int rightPadding;

    protected int monthTopPadding;
    protected int monthBottomPadding;
    protected int monthTextSize;

    protected int dayTopPadding;
    protected int dayBottomPadding;
    protected int daySpacing;
    protected int dayTextSize;
    protected int dayLabelTextSize;

    protected int circleRadius;

    protected int circleSmallRadius;
    protected int circleSmallDis;

    //分隔线条属性:颜色/宽度
    protected int splitLineColor;
    protected int splitLineStrokeHeight;

    protected Paint splitLinePaint;
    protected Paint monthTextPaint;
    protected Paint dayLabelTextPaint;
    protected Paint dayTextPaint;
    protected Paint circlePaint;
    protected Paint arcPaint;

    protected int selectColor;
    protected int textNormalColor;
    protected int textSelectColor;
    protected int previousSelectColor;

    //计算出来
    protected int monthTextHeight;
    protected int dayTextHeight;
    protected int dayLabelTextHeight;

    private final StringBuilder mStringBuilder;

    protected boolean mHasToday = false;
    protected boolean mIsPrev = false;



    protected int mToday = -1;
    protected int mWeekStart = 1;
    protected int mNumDays = 7;
    protected int mNumCells = mNumDays;
    private int mDayOfWeekStart = 0;
    protected int mMonth;
    protected int mWidth;
    protected int mYear;
    final Time today;

    public Calendar getmCalendar() {
        return mCalendar;
    }

    private final Calendar mCalendar;

    private final Boolean isPrevDayEnabled;

    private boolean shouldShowMonthInfo = false;
    private long alphaStartTime = -1;
    private final int FRAMES_PER_SECOND = 60;
    private final long ALPHA_DURATION = 400;
    private int currentDraggingAlpha;
    private int currentNormalAlpha;

    private int mNumRows = DEFAULT_NUM_ROWS;

    private Map<Integer, Integer> eventSymbols = new HashMap<>();

    private OnDayClickListener mOnDayClickListener;

    //当月课程信息,key:day,值:当日课程数组
    private Map<Integer, List<Cource>> courses = new HashMap<>();


    public SimpleMonthView(Context context, TypedArray typedArray) {
        super(context);
        Resources resources = context.getResources();

        mCalendar = Calendar.getInstance();
        today = new Time(Time.getCurrentTimezone());
        today.setToNow();

        mStringBuilder = new StringBuilder(50);

        isPrevDayEnabled = typedArray.getBoolean(R.styleable.DayPickerView_enablePreviousDay, true);
        //参数
        topPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_topPadding, resources.getDimensionPixelSize(R.dimen.calendar_toppadding));
        bottomPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_bottomPadding, resources.getDimensionPixelSize(R.dimen.calendar_bottompadding));
        leftPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_leftPadding, resources.getDimensionPixelSize(R.dimen.calendar_leftpadding));
        rightPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_rightPadding, resources.getDimensionPixelSize(R.dimen.calendar_rightpadding));

        monthTopPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_monthTopPadding, resources.getDimensionPixelSize(R.dimen.calendar_month_toppadding));
        monthBottomPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_monthBottomPadding, resources.getDimensionPixelSize(R.dimen.calendar_month_bottompadding));
        monthTextSize = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_monthTextSize, resources.getDimensionPixelSize(R.dimen.calendar_month_text_size));

        dayTopPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_dayTopPadding, resources.getDimensionPixelSize(R.dimen.calendar_day_toppadding));
        dayBottomPadding = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_dayBottomPadding, resources.getDimensionPixelSize(R.dimen.calendar_day_bottompadding));
        daySpacing = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_daySpacing, resources.getDimensionPixelSize(R.dimen.calendar_day_spacing));
        dayTextSize = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_dayTextSize, resources.getDimensionPixelSize(R.dimen.calendar_day_text_size));
        dayLabelTextSize = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_dayLabelTextSize, resources.getDimensionPixelSize(R.dimen.calendar_day_label_text_size));

        //分隔线条属性:颜色/宽度
        splitLineColor = typedArray.getColor(R.styleable.DayPickerView_splitLineColor, resources.getColor(R.color.calendar_split_line_color));
        splitLineStrokeHeight = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_splitLineStrokeHeight, resources.getDimensionPixelSize(R.dimen.calendar_split_line_strokeheight));

        circleRadius = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_circleRadius, resources.getDimensionPixelSize(R.dimen.calendar_circle_radius));
        circleSmallRadius = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_circleSmallRadius, resources.getDimensionPixelSize(R.dimen.calendar_small_circle_radius));
        circleSmallDis = typedArray.getDimensionPixelSize(R.styleable.DayPickerView_circleSmallDis, resources.getDimensionPixelSize(R.dimen.calendar_small_circle_dis));

        textSelectColor = typedArray.getColor(R.styleable.DayPickerView_textSelectColor, resources.getColor(R.color.calendar_text_select_color));
        selectColor = typedArray.getColor(R.styleable.DayPickerView_selectColor, resources.getColor(R.color.calendar_select_color));
        textNormalColor = typedArray.getColor(R.styleable.DayPickerView_textNormalColor, resources.getColor(R.color.calendar_text_normal_color));
        previousSelectColor = typedArray.getColor(R.styleable.DayPickerView_previousSelectColor, resources.getColor(R.color.calendar_previous_select_color));

        //testData();
        initView();

    }

    public void setCourses(Map<Integer, List<Cource>> courses) {
        this.courses = courses;
    }

    public void testData() {
        courses = new HashMap<>();
        List<Cource> daycourses = new ArrayList<>();
        Cource daycourse = new Cource();
        daycourse.setSubject("语文");
        daycourse.setId(1);
        daycourse.setIs_passed(false);
        daycourses.add(daycourse);
        daycourse = new Cource();
        daycourse.setSubject("数学");
        daycourse.setId(2);
        daycourse.setIs_passed(true);
        daycourses.add(daycourse);
        daycourse = new Cource();
        daycourse.setSubject("英语");
        daycourse.setId(3);
        daycourse.setIs_passed(false);
        daycourses.add(daycourse);
        courses.put(1, daycourses);
        daycourses = new ArrayList<>();
        daycourse = new Cource();
        daycourse.setSubject("化学");
        daycourse.setId(1);
        daycourse.setIs_passed(true);
        daycourses.add(daycourse);
        courses.put(2, daycourses);
        daycourses = new ArrayList<>();
        daycourse = new Cource();
        daycourse.setSubject("化学");
        daycourse.setId(1);
        daycourse.setIs_passed(true);
        daycourses.add(daycourse);
        courses.put(23, daycourses);
    }

    private int calculateNumRows() {
        int offset = findDayOffset();
        int dividend = (offset + mNumCells) / mNumDays;
        int remainder = (offset + mNumCells) % mNumDays;
        return (dividend + (remainder > 0 ? 1 : 0));
    }

    private int findDayOffset() {
        return (mDayOfWeekStart < mWeekStart ? (mDayOfWeekStart + mNumDays) : mDayOfWeekStart)
                - mWeekStart;
    }

    private String getMonthAndYearString() {
        int flags = DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_NO_MONTH_DAY;
        mStringBuilder.setLength(0);
        long millis = mCalendar.getTimeInMillis();
        return DateUtils.formatDateRange(getContext(), millis, millis, flags);
    }

    private String getMonthString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("M");
        String monthTitleText = dateFormat.format(mCalendar.getTime());
        return monthTitleText + "月";
    }

    private void onDayClick(SimpleMonthAdapter.CalendarDay calendarDay) {
        if (mOnDayClickListener != null && (isPrevDayEnabled || !((calendarDay.month == today.month) && (calendarDay.year == today.year) && calendarDay.day < today.monthDay))) {
            List<Cource> listCource = null;
            if (courses!=null){
                listCource = courses.get(calendarDay.day);
            }
            mOnDayClickListener.onDayClick(this, calendarDay, listCource);
        }
    }

    private boolean sameDay(int monthDay, Time time) {
        return (mYear == time.year) && (mMonth == time.month) && (monthDay == time.monthDay);
    }

    private boolean prevDay(int monthDay, Time time) {
        return ((mYear < time.year)) || (mYear == time.year && mMonth < time.month) || (mMonth == time.month && monthDay < time.monthDay);
    }

    public SimpleMonthAdapter.CalendarDay getDayFromLocation(float x, float y) {
        if ((x < leftPadding) || (x > mWidth - rightPadding)||(y<topPadding + monthTextHeight + monthTopPadding + monthBottomPadding)) {
            return null;
        }

        int yDay = (int) (y - topPadding - monthTextHeight - monthTopPadding - monthBottomPadding) / (splitLineStrokeHeight+dayTopPadding+dayTextHeight+daySpacing+dayLabelTextHeight+ dayBottomPadding);
        int day = 1 + ((int) ((x - leftPadding) * mNumDays / (mWidth - leftPadding - rightPadding)) - findDayOffset()) + yDay * mNumDays;

        if (mMonth > 11 || mMonth < 0 || CalendarUtils.getDaysInMonth(mMonth, mYear) < day || day < 1)
            return null;

        return new SimpleMonthAdapter.CalendarDay(mYear, mMonth+1, day);
    }

    protected void initView() {

        splitLinePaint = new Paint();
        splitLinePaint.setAntiAlias(true);
        splitLinePaint.setStyle(Style.FILL);
        splitLinePaint.setColor(splitLineColor);
        splitLinePaint.setStrokeWidth((float) splitLineStrokeHeight);

        monthTextPaint = new Paint();
        monthTextPaint.setAntiAlias(true);
        monthTextPaint.setTextSize(monthTextSize);
        monthTextPaint.setColor(selectColor);
        monthTextPaint.setTextAlign(Align.CENTER);
        monthTextPaint.setStyle(Style.FILL);

        dayLabelTextPaint = new Paint();
        dayLabelTextPaint.setAntiAlias(true);
        dayLabelTextPaint.setTextSize(dayLabelTextSize);
        dayLabelTextPaint.setColor(textNormalColor);
        dayLabelTextPaint.setTextAlign(Align.CENTER);
        dayLabelTextPaint.setStyle(Style.FILL);

        dayTextPaint = new Paint();
        dayTextPaint.setAntiAlias(true);
        dayTextPaint.setTextSize(dayTextSize);
        dayTextPaint.setColor(textNormalColor);
        dayTextPaint.setTextAlign(Align.CENTER);
        dayTextPaint.setStyle(Style.FILL);

        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setColor(selectColor);
        circlePaint.setTextAlign(Align.CENTER);
        circlePaint.setStyle(Style.FILL);

        arcPaint = new Paint();
        arcPaint.setStrokeWidth(2);
        arcPaint.setStyle(Style.STROKE);
        arcPaint.setFakeBoldText(true);
        arcPaint.setAntiAlias(true);
        arcPaint.setColor(selectColor);
        arcPaint.setTextAlign(Align.CENTER);

        //计算文字高度
        monthTextHeight = (int) ViewUtils.getTextHeight(monthTextPaint);
        dayTextHeight = (int) ViewUtils.getTextHeight(dayTextPaint);
        dayLabelTextHeight = (int) ViewUtils.getTextHeight(dayLabelTextPaint);
    }

    protected void onDraw(Canvas canvas) {
        drawMonthContent(canvas);
        //绘制数字
        //calculateAlpha();
    }

    private void drawMonthContent(Canvas canvas) {
        int y = 0;

        //绘制月份
        int monthX;
        int monthY;
        int halfSpacing = (mWidth - leftPadding - rightPadding) / (2 * mNumDays);
        //文字中心x坐标
        monthX = halfSpacing * (findDayOffset() * 2 + 1) + leftPadding;
        //文字基线Y坐标
        monthY = y + monthTopPadding + monthTextHeight + topPadding;
        //画文字
        StringBuilder stringBuilder = new StringBuilder(getMonthString().toLowerCase());
        stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
        canvas.drawText(stringBuilder.toString(), monthX, monthY, monthTextPaint);
        //计算月份条目高度
        y += monthY + monthBottomPadding;

        int day = 1;
        int dayOffset = findDayOffset();
        float lineStartX = (mWidth - leftPadding - rightPadding) * dayOffset / mNumDays;
        float lineStartY = y+1;
        while (day <= mNumCells) {
            //绘制数字
            int dayX = (2 * dayOffset + 1) * halfSpacing + leftPadding;
            int dayY = y + splitLineStrokeHeight + dayTopPadding + dayTextHeight;
            //判断当天有没有课程
            List<Cource> dayCourses = getCourses(day);
            //当天有课程
            if (dayCourses != null && dayCourses.size() > 0) {
                String dayLabelText = null;
                Cource cource = dayCourses.get(0);

                if (mHasToday && (mToday == day)) {
                    dayTextPaint.setColor(selectColor);
                    dayLabelTextPaint.setColor(selectColor);
                    //画圆
                    RectF rect = new RectF(dayX - circleRadius, dayY - dayTextHeight / 3 - circleRadius, dayX
                            + circleRadius, dayY - dayTextHeight / 3 + circleRadius);
                    canvas.drawArc(rect, 0, 360, false, arcPaint);
                }else{
                    if (cource.is_passed()) {
                        dayTextPaint.setColor(textSelectColor);
                        dayLabelTextPaint.setColor(previousSelectColor);
                        circlePaint.setColor(previousSelectColor);
                        canvas.drawCircle(dayX, dayY - dayTextHeight / 3, circleRadius, circlePaint);
                    } else {
                        dayTextPaint.setColor(textSelectColor);
                        dayLabelTextPaint.setColor(selectColor);
                        circlePaint.setColor(selectColor);
                        canvas.drawCircle(dayX, dayY - dayTextHeight / 3, circleRadius, circlePaint);
                    }
                }

                if (dayCourses.size() == 1) {
                    dayLabelText = cource.getSubject();
                    canvas.drawText(String.format("%d", day), dayX, dayY, dayTextPaint);
                    dayY += daySpacing + dayLabelTextHeight;
                    canvas.drawText(dayLabelText, dayX, dayY, dayLabelTextPaint);
                } else {
                    canvas.drawText(String.format("%d", day), dayX, dayY, dayTextPaint);
                    dayY += daySpacing + dayLabelTextHeight/2;

                    canvas.drawCircle(dayX, dayY , circleSmallRadius, circlePaint);
                    canvas.drawCircle(dayX - circleSmallDis - circleSmallRadius, dayY , circleSmallRadius, circlePaint);
                    canvas.drawCircle(dayX + circleSmallDis + circleSmallRadius, dayY , circleSmallRadius, circlePaint);
                }
            } else {

                if (mHasToday && (mToday == day)) {
                    dayTextPaint.setColor(selectColor);
                    dayLabelTextPaint.setColor(selectColor);
                    //画圆,更改字体颜色
                    RectF rect = new RectF(dayX - circleRadius, dayY - dayTextHeight / 3 - circleRadius, dayX
                            + circleRadius, dayY - dayTextHeight / 3 + circleRadius);
                    canvas.drawArc(rect, 0, 360, false, arcPaint);
                    String dayLabelText = "今天";
                    int dayLabelY = dayY + daySpacing + dayLabelTextHeight;
                    canvas.drawText(dayLabelText, dayX, dayLabelY, dayLabelTextPaint);
                } else {
                    //更改字体颜色
                    dayTextPaint.setColor(textNormalColor);
                }
                canvas.drawText(String.format("%d", day), dayX, dayY, dayTextPaint);
            }
            //绘制线条
            dayOffset++;
            if (dayOffset == mNumDays) {
                canvas.drawLine(lineStartX, lineStartY, lineStartX + getWidth(), lineStartY, splitLinePaint);
                dayOffset = 0;
                y += splitLineStrokeHeight + dayTopPadding + dayBottomPadding + daySpacing + dayTextHeight + dayLabelTextHeight;
                lineStartX = leftPadding;
                lineStartY = y + 1;
            }
            day++;
        }
        canvas.drawLine(lineStartX, lineStartY, lineStartX + getWidth(), lineStartY, splitLinePaint);
    }

    private List<Cource> getCourses(int day) {
        List<Cource> dayCourses = null;
        if (courses != null) {
            dayCourses = courses.get(day);
        }
        return dayCourses;
    }


    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                (dayTopPadding + dayBottomPadding + dayLabelTextHeight + dayTextHeight + daySpacing) * mNumRows + monthTopPadding + monthBottomPadding + monthTextHeight + topPadding + bottomPadding);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mWidth = w;
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            SimpleMonthAdapter.CalendarDay calendarDay = getDayFromLocation(event.getX(), event.getY());
            if (calendarDay != null) {
                onDayClick(calendarDay);
            }
        }
        return true;
    }

    public void reuse() {
        mNumRows = DEFAULT_NUM_ROWS;
        eventSymbols.clear();
        requestLayout();
    }

    public void setMonthParams(HashMap<String, Integer> params) {
        if (!params.containsKey(VIEW_PARAMS_MONTH) && !params.containsKey(VIEW_PARAMS_YEAR)) {
            throw new InvalidParameterException("You must specify month and year for this view");
        }
        setTag(params);

        mMonth = params.get(VIEW_PARAMS_MONTH);
        mYear = params.get(VIEW_PARAMS_YEAR);

        mHasToday = false;
        mToday = -1;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (params.containsKey(VIEW_PARAMS_WEEK_START)) {
            mWeekStart = params.get(VIEW_PARAMS_WEEK_START);
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mNumCells = CalendarUtils.getDaysInMonth(mMonth, mYear);
        for (int i = 0; i < mNumCells; i++) {
            final int day = i + 1;
            if (sameDay(day, today)) {
                mHasToday = true;
                mToday = day;
            }

            mIsPrev = prevDay(day, today);
        }

        mNumRows = calculateNumRows();
    }

    public void showMothInfo(boolean show) {
        if (shouldShowMonthInfo != show) {
            shouldShowMonthInfo = show;
            alphaStartTime = System.currentTimeMillis();
        }
    }

    private void calculateAlpha() {
        long elapsedTime = System.currentTimeMillis() - alphaStartTime;
        int alphaChange = (int) ((NORMAL_ALPHA - 0) * elapsedTime / ALPHA_DURATION);

        currentDraggingAlpha = NORMAL_ALPHA - alphaChange;
        if (currentDraggingAlpha < 0 || alphaStartTime == -1) {
            currentDraggingAlpha = 0;
        }
        currentNormalAlpha = alphaChange;
        if (currentNormalAlpha > NORMAL_ALPHA) {
            currentNormalAlpha = NORMAL_ALPHA;
        }
        if (shouldShowMonthInfo) {
           /* mMonthInfoPaint.setAlpha(currentNormalAlpha);
            mMonthTitlePaint.setAlpha(DRAGING_ALPHA);
            mSelectedCirclePaint.setAlpha(DRAGING_ALPHA);
            mCurrentCirclePaint.setAlpha(DRAGING_ALPHA);
            mMonthDayLabelPaint.setAlpha(DRAGING_ALPHA);*/
        } else {
            /*mMonthInfoPaint.setAlpha(currentDraggingAlpha);
            mMonthTitlePaint.setAlpha(NORMAL_ALPHA);
            mSelectedCirclePaint.setAlpha(NORMAL_ALPHA);
            mCurrentCirclePaint.setAlpha(NORMAL_ALPHA);
            mMonthDayLabelPaint.setAlpha(NORMAL_ALPHA);*/
        }

        if (elapsedTime < ALPHA_DURATION) {
            this.postInvalidateDelayed(1000 / FRAMES_PER_SECOND);
        }
    }

    public void setEventSymbols(HashMap<SimpleMonthAdapter.CalendarDay, Integer> symbols) {
        eventSymbols.clear();
        for (HashMap.Entry<SimpleMonthAdapter.CalendarDay, Integer> entry : symbols.entrySet()) {
            eventSymbols.put(entry.getKey().getDay(), entry.getValue());
        }
    }

    public void setOnDayClickListener(OnDayClickListener onDayClickListener) {
        mOnDayClickListener = onDayClickListener;
    }

    public static abstract interface OnDayClickListener {
        public abstract void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay, List<Cource> courses);
    }
}