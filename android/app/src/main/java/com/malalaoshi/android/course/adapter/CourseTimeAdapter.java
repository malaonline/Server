package com.malalaoshi.android.course.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.course.model.CourseTimeModel;

/**
 * 上课时间
 * Created by tianwei on 5/15/16.
 */
public class CourseTimeAdapter extends MalaBaseAdapter<CourseTimeModel> {
    public CourseTimeAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = View.inflate(context, R.layout.view_course_selected_times, null);
        int height = context.getResources().getDimensionPixelOffset(R.dimen.course_time_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(params);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void fillView(int position, View convertView, CourseTimeModel data) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (position == getList().size() - 1) {
            holder.lineView.setVisibility(View.GONE);
        } else {
            holder.lineView.setVisibility(View.VISIBLE);
        }
        holder.contentView.setText(data.getCourseTimes());
        holder.weekView.setText(data.getWeek());
        holder.dateView.setText(data.getDate());
    }

    private static final class ViewHolder {
        private View lineView;
        private TextView contentView;
        private TextView weekView;
        private TextView dateView;

        public ViewHolder(View view) {
            lineView = view.findViewById(R.id.iv_time_line);
            contentView = (TextView) view.findViewById(R.id.tv_content);
            weekView = (TextView) view.findViewById(R.id.tv_week);
            dateView = (TextView) view.findViewById(R.id.tv_date);
        }
    }
}
