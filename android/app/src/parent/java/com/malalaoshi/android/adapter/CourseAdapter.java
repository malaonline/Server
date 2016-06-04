package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.util.CalendarUtils;

import java.util.Calendar;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/6/4.
 */
public class CourseAdapter extends MalaBaseAdapter<Course>{

    Context mContext;
    public CourseAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.schedule_list_item, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    public void updateData(List<Course> courseList){
        clear();
        if (courseList!=null){
            addAll(courseList);
        }
        notifyDataSetChanged();
    }

    @Override
    protected void fillView(int position, View convertView, Course data) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        //已上过的课程
        Resources resources = mContext.getResources();
        Calendar start = CalendarUtils.timestampToCalendar(data.getStart());
        Calendar end = CalendarUtils.timestampToCalendar(data.getEnd());
        if (data.is_passed()) {
            holder.tvDay.setTextColor(resources.getColor(R.color.text_color_dlg));
            holder.tvWeek.setTextColor(resources.getColor(R.color.text_color_dlg));
            holder.rlSchedule.setBackgroundColor(resources.getColor(R.color.item_color_past_bg));
        } else {
            if(CalendarUtils.compareCurrentDate(start)==0){
                holder.tvDay.setTextColor(resources.getColor(R.color.item_color_bg));
                holder.tvWeek.setTextColor(resources.getColor(R.color.item_color_bg));
            }else{
                holder.tvDay.setTextColor(resources.getColor(R.color.text_color_darkgray));
                holder.tvWeek.setTextColor(resources.getColor(R.color.text_color_darkgray));
            }
            holder.rlSchedule.setBackgroundColor(resources.getColor(R.color.item_color_bg));

        }
        holder.tvGradeCourse.setText(data.getGrade()+" "+data.getSubject()+getList().size());
        holder.tvTeacherName.setText(data.getTeacher().getName());
        holder.tvClassPosition.setText(data.getSchool());
        if (start!=null&&end!=null){
            holder.tvDay.setText(start.get(Calendar.DAY_OF_MONTH)+"");
            holder.tvWeek.setText(CalendarUtils.getWeekBytimestamp(data.getStart()));
            holder.tvClassTime.setText(CalendarUtils.formatTime(start)+"-"+CalendarUtils.formatTime(end));
        }else{
            holder.tvDay.setText("");
            holder.tvWeek.setText("");
            holder.tvClassTime.setText("");
        }
    }

    class ViewHolder {
        @Bind(R.id.tv_day)
        TextView tvDay;
        @Bind(R.id.tv_week)
        TextView tvWeek;
        @Bind(R.id.rl_schedule)
        RelativeLayout rlSchedule;
        @Bind(R.id.tv_grade_course)
        TextView tvGradeCourse;
        @Bind(R.id.tv_teacher_name)
        TextView tvTeacherName;
        @Bind(R.id.tv_class_time)
        TextView tvClassTime;
        @Bind(R.id.tv_class_position)
        TextView tvClassPosition;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }
}
