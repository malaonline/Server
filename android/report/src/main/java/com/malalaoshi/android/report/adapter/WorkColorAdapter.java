package com.malalaoshi.android.report.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.core.utils.MiscUtil;
import com.malalaoshi.android.report.R;
import com.malalaoshi.android.report.entity.WorkColorModel;
import com.malalaoshi.android.report.view.OvalView;

/**
 * 作业颜色列表
 * Created by tianwei on 5/21/16.
 */
public class WorkColorAdapter extends MalaBaseAdapter<WorkColorModel> {

    private int itemHeight;

    public WorkColorAdapter(Context context) {
        super(context);
        itemHeight = MiscUtil.dp2px(30);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(
                R.layout.report__page_work_color_list_view, parent, false);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, itemHeight);
        view.setLayoutParams(params);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void fillView(int position, View convertView, WorkColorModel data) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.iconView.setColor(data.getColor());
        holder.contentView.setText(data.getContent());
    }

    private static class ViewHolder {
        public ViewHolder(View view) {
            contentView = (TextView) view.findViewById(R.id.tv_content);
            iconView = (OvalView) view.findViewById(R.id.iv_color);
        }

        private TextView contentView;
        private OvalView iconView;
    }
}
