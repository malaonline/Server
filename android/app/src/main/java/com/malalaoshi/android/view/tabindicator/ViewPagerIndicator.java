package com.malalaoshi.android.view.tabindicator;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.R;


/**
 * Created by kang on 16/5/16.
 */
public class ViewPagerIndicator extends ViewGroup {

    private ViewPager mViewPager;
    private OnTabClickListener mTabClickListener;
    private OnPageChangeListener pageChangeListener;
    private int tabVisiableCount = 4;

    private int tabTextSize;
    private int tabTextColor;
    private int tabTextFocusColor;
    private int tabFocusPos;

    public ViewPagerIndicator(Context context) {
        this(context,null);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ViewPagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, 0);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ViewPagerIndicator);
        if (null != typedArray) {
            Resources resources = context.getResources();
            tabTextSize = typedArray.getDimensionPixelSize(R.styleable.ViewPagerIndicator_vpTabTextSize, resources.getDimensionPixelSize(R.dimen.text_size_large));
            tabTextColor = typedArray.getColor(R.styleable.ViewPagerIndicator_vpTabTextColor, resources.getColor(R.color.color_black_6c6c6c));
            tabTextFocusColor = typedArray.getColor(R.styleable.ViewPagerIndicator_vpTabTextFocusColor, resources.getColor(R.color.color_blue_82b4d9));
            tabFocusPos = typedArray.getInt(R.styleable.ViewPagerIndicator_vpTabFocusPos, 0);
            tabVisiableCount = typedArray.getInt(R.styleable.ViewPagerIndicator_vpTabVisiableCount, 4);
            typedArray.recycle();
        }
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        //设置子View
        int childCount = getChildCount();
        for (int i=0;i<childCount;i++){
            View childView = getChildAt(i);
            if (childView instanceof TabView){
                ((TabView)childView).setTabTextSize(tabTextSize);
                ((TabView)childView).setTabTextColor(tabTextColor);
                ((TabView)childView).setTabTextFocusColor(tabTextFocusColor);
            }
            MarginLayoutParams lp = (MarginLayoutParams) childView
                    .getLayoutParams();
            if (lp.width== LayoutParams.MATCH_PARENT){
                lp.width = LayoutParams.WRAP_CONTENT;
            }
            if (lp.height== LayoutParams.MATCH_PARENT){
                lp.height = LayoutParams.WRAP_CONTENT;
            }
            childView.setLayoutParams(lp);
        }
        setFocusPosition(tabFocusPos);
        setClickTabEvent();
    }

    public void setTitles(String[] titles, int focusPos){
        if (titles==null&&titles.length>0){
            return;
        }
        this.removeAllViews();
        for (int i=0;i<titles.length;i++){
            View view = null;
            if (i==0){
                view = GenerateFirstTab(titles[i]);
            }else if (i==titles.length-1){
                view = GenerateLastTab(titles[i]);
            }else{
                view = GenerateMidTab(titles[i]);
            }
            if (view instanceof TabView){
                ((TabView)view).setTabTextSize(tabTextSize);
                ((TabView)view).setTabTextColor(tabTextColor);
                ((TabView)view).setTabTextFocusColor(tabTextFocusColor);
            }
            this.addView(view);
        }
        setFocusPosition(focusPos);
        setClickTabEvent();
    }

    public void setTitles(String[] titles){
        setTitles(titles, tabFocusPos);
    }

    private TabView GenerateFirstTab(String title){
        TabView tab = new TabView(getContext(),null);
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tab.setTabTitle(title);
        tab.setLayoutParams(params);
        return tab;
    }

    private TabView GenerateLastTab(String title){
        TabView tab = new TabView(getContext(),null);
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tab.setTabTitle(title);
        tab.setLayoutParams(params);
        return tab;
    }

    private TabView GenerateMidTab(String title){
        TabView tab = new TabView(getContext(),null);
        MarginLayoutParams params = new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        tab.setTabTitle(title);
        tab.setLayoutParams(params);
        return tab;
    }

    public void setTabClickListener(OnTabClickListener tabClickListener) {
        this.mTabClickListener = mTabClickListener;
    }

    public void setPageChangeListener(OnPageChangeListener pageChangeListener) {
        this.pageChangeListener = pageChangeListener;
    }

    public void setViewPager(ViewPager viewPager){
        mViewPager = viewPager;
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (pageChangeListener!=null){
                    pageChangeListener.onPageScrolled(position,positionOffset,positionOffsetPixels);
                }
            }

            @Override
            public void onPageSelected(int position) {
                if (pageChangeListener!=null){
                    pageChangeListener.onPageSelected(position);
                }
                setFocusPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (pageChangeListener!=null){
                    pageChangeListener.onPageScrollStateChanged(state);
                }
            }
        });
    }

    void setClickTabEvent(){
        int childCpunt = getChildCount();
        for (int i=0;i<childCpunt;i++){
            View childView = getChildAt(i);
            final int finalI = i;
            childView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setFocusPosition(finalI);
                    if (mViewPager!=null){
                        mViewPager.setCurrentItem(finalI);
                        if (mTabClickListener!=null){
                            mTabClickListener.OnTabClickListener((TabView) v, finalI);
                        }
                    }
                }
            });
        }
    }

    //取消所有tab高亮
    private void resetFocusPosition(){
        int childCpunt = getChildCount();
        for (int i=0;i<childCpunt;i++){
            View childView = getChildAt(i);
           //取消选中
            if (childView instanceof TabView){
                ((TabView) childView).resetHeightLight();
            }
        }
    }

    //设置Tab高亮
    public void setFocusPosition(int position) {
        resetFocusPosition();
        View childView = getChildAt(position);
        //设置选中
        if (childView instanceof TabView){
            ((TabView) childView).setHeightLight();
        }
    }

    /**
     * 设置红点时候可见
     * @param position
     * @param visibility  取值 View.VISIBLE或View.INVISIBLE
     */
    public void setTabIndicatorVisibility(int position,int visibility){
        View childView = getChildAt(position);
        //设置选中
        if (childView instanceof TabView){
            ((TabView) childView).setTabIndicatorVisibility(visibility);
        }
    }

    interface OnTabClickListener{
        void OnTabClickListener(TabView view, int position);
    }

    public interface OnPageChangeListener {

        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

        public void onPageSelected(int position);

        public void onPageScrollStateChanged(int state);
    }


    /**
     * 计算控件的大小
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = measureWidth(widthMeasureSpec);
        int measureHeight = measureHeight(heightMeasureSpec);
        // 计算自定义的ViewGroup中所有子控件的大小
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        // 设置自定义的控件MyViewGroup的大小
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int measureWidth(int pWidthMeasureSpec) {
        int result = 0;
        int widthMode = MeasureSpec.getMode(pWidthMeasureSpec);// 得到模式
        int widthSize = MeasureSpec.getSize(pWidthMeasureSpec);// 得到尺寸

        switch (widthMode) {
            /**
             * mode共有三种情况，取值分别为MeasureSpec.UNSPECIFIED, MeasureSpec.EXACTLY,
             * MeasureSpec.AT_MOST。
             *
             *
             * MeasureSpec.EXACTLY是精确尺寸，
             * 当我们将控件的layout_width或layout_height指定为具体数值时如andorid
             * :layout_width="50dip"，或者为FILL_PARENT是，都是控件大小已经确定的情况，都是精确尺寸。
             *
             *
             * MeasureSpec.AT_MOST是最大尺寸，
             * 当控件的layout_width或layout_height指定为WRAP_CONTENT时
             * ，控件大小一般随着控件的子空间或内容进行变化，此时控件尺寸只要不超过父控件允许的最大尺寸即可
             * 。因此，此时的mode是AT_MOST，size给出了父控件允许的最大尺寸。
             *
             *
             * MeasureSpec.UNSPECIFIED是未指定尺寸，这种情况不多，一般都是父控件是AdapterView，
             * 通过measure方法传入的模式。
             */
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = widthSize;
                break;
        }
        return result;
    }

    private int measureHeight(int pHeightMeasureSpec) {
        int result = 0;

        int heightMode = MeasureSpec.getMode(pHeightMeasureSpec);
        int heightSize = MeasureSpec.getSize(pHeightMeasureSpec);

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = heightSize;
                break;
        }
        return result;
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b)
    {
        // 当前ViewGroup的宽度
        int width = getWidth();
        //所有tab占据的宽度
        int tabsWidth = 0;
        int childCount = getChildCount();
        for (int i=0;i<childCount;i++){
            View child = getChildAt(i);
            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();
            int childWidth = child.getMeasuredWidth();
            tabsWidth += childWidth + lp.leftMargin + lp.rightMargin;
        }
        int tabSpacing = 0;
        if (tabVisiableCount<=1){
            tabSpacing = (width - tabsWidth);
        }else{
            tabSpacing = (width - tabsWidth)/(tabVisiableCount-1);
        }

        // 设置子View的位置
        int left = getPaddingLeft();
        int top = getPaddingTop();

        for (int i = 0; i < childCount; i++)
        {
            View child = getChildAt(i);
            // 判断child的状态
            if (child.getVisibility() == View.GONE)
            {
                continue;
            }

            MarginLayoutParams lp = (MarginLayoutParams) child
                    .getLayoutParams();

            int lc = left + lp.leftMargin;
            int tc = top + lp.topMargin;
            int rc = lc + child.getMeasuredWidth();
            int bc = tc + child.getMeasuredHeight();

            // 为子View进行布局int l, int t, int r, int b
            child.layout(lc, tc, rc, bc);
            left += child.getMeasuredWidth() + lp.leftMargin
                    + lp.rightMargin + tabSpacing;
        }
    }

    /**
     * 与当前ViewGroup对应的LayoutParams
     */
    @Override
    public MarginLayoutParams generateLayoutParams(AttributeSet attrs)
    {
        return new MarginLayoutParams(getContext(), attrs);
    }

}
