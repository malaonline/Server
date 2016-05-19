package com.malalaoshi.android.view.tabindicator;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.malalaoshi.android.R;

import butterknife.ButterKnife;


/**
 * Created by kang on 16/5/17.
 */
public class TabView extends LinearLayout {

    private TextView tvTabTitle;
    private ImageView ivTabIndicator;
    private View viewTabIndicator;

    private int tabTextColor ;
    private int tabTextFocusColor ;

    public TabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = LayoutInflater.from(context).inflate(R.layout.view_indicator_tab,null);
        tvTabTitle = (TextView) view.findViewById(R.id.tv_tab_title);
        ivTabIndicator = (ImageView) view.findViewById(R.id.iv_tab_indicator);
        viewTabIndicator = view.findViewById(R.id.view_tab_indicator);
        addView(view);
        ButterKnife.bind(this, view);
    }

    public void setTabTitle(String string){
        tvTabTitle.setText(string);
    }

    public void  setHeightLight(){
        tvTabTitle.setTextColor(tabTextFocusColor);
        viewTabIndicator.setSelected(true);
    }

    public void resetHeightLight(){
        tvTabTitle.setTextColor(tabTextColor);
        viewTabIndicator.setSelected(false);
    }

    public void  setTabIndicatorVisibility(int visibility){
        ivTabIndicator.setVisibility(visibility);
    }


    public void setTabTextSize(int tabTextSize) {
        tvTabTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,tabTextSize);
    }

    public void setTabTextColor(int tabTextColor) {
        this.tabTextColor = tabTextColor;
    }

    public void setTabTextFocusColor(int tabTextFocusColor) {
        this.tabTextFocusColor = tabTextFocusColor;
    }
}
