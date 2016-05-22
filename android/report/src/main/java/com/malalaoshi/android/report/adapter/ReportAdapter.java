package com.malalaoshi.android.report.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.core.utils.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 报告样本
 * Created by tianwei on 5/21/16.
 */
public class ReportAdapter extends PagerAdapter {

    private List<View> list;

    public ReportAdapter(Context context) {
        list = new ArrayList<>();
    }

    public void setList(List<View> list) {
        if (EmptyUtils.isNotEmpty(list)) {
            this.list.clear();
            this.list.addAll(list);
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(list.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        (container).addView(list.get(position));
        return list.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }
}
