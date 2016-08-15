package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.entity.ScheduleCourse;
import com.malalaoshi.android.entity.ScheduleDate;
import com.malalaoshi.android.entity.ScheduleItem;
import com.malalaoshi.android.util.CalendarUtils;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/6/29.
 */
public class ScheduleAdapter extends BaseRecycleAdapter<ScheduleAdapter.ParentViewHolder,ScheduleItem> {

    private static final int TYPE_ITEM_COURSE = 3;
    private static final int TYPE_ITEM_DATE = 2;

    public ScheduleAdapter(Context context) {
        super(context);
    }

    @Override
    public ParentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_ITEM_COURSE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_course_item, null);
            //view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_ITEM_DATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_date_item, null);
           // view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            return new ItemDateViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(ParentViewHolder holder, int position) {
        int type = getItemViewType(position);
         if (type==TYPE_ITEM_DATE){
            ScheduleDate scheduleDate = (ScheduleDate) getItem(position);
            ((ItemDateViewHolder)holder).update(position,scheduleDate);
        } else{
            ScheduleCourse scheduleCourse = (ScheduleCourse) getItem(position);
            ((ItemViewHolder)holder).update(position,scheduleCourse);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (getItem(position).getType()==ScheduleItem.TYPE_COURSE){
            return TYPE_ITEM_COURSE;
        }else {
            return TYPE_ITEM_DATE;
        }
    }


    abstract class ParentViewHolder extends RecyclerView.ViewHolder{

        public ParentViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void update(int position, ScheduleItem scheduleItem);
    }

    class ItemViewHolder extends ParentViewHolder{
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
        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mView = itemView;
        }

        @Override
        public void update(int position, ScheduleItem scheduleItem) {
            Course data = ((ScheduleCourse)scheduleItem).getCourse();
            Resources resources = mView.getResources();
            Calendar start = CalendarUtils.timestampToCalendar(data.getStart());
            Calendar end = CalendarUtils.timestampToCalendar(data.getEnd());
            if (data.is_passed()) {
                tvDay.setTextColor(resources.getColor(R.color.color_black_939393));
                tvWeek.setTextColor(resources.getColor(R.color.color_black_939393));
                rlSchedule.setBackgroundResource(R.drawable.bg_corner_normal);
            } else {
                if(CalendarUtils.compareCurrentDate(start)==0){
                    tvDay.setTextColor(resources.getColor(R.color.color_blue_6bd2e5));
                    tvWeek.setTextColor(resources.getColor(R.color.color_blue_6bd2e5));
                }else{
                    tvDay.setTextColor(resources.getColor(R.color.color_black_333333));
                    tvWeek.setTextColor(resources.getColor(R.color.color_black_333333));
                }
                rlSchedule.setBackgroundResource(R.drawable.bg_corner_blue);

            }
            tvGradeCourse.setText(data.getGrade()+" "+data.getSubject());
            tvTeacherName.setText(data.getTeacher().getName());
            tvClassPosition.setText(data.getSchool());
            if (start!=null&&end!=null){
                if (((ScheduleCourse)scheduleItem).isFirstCourseOfDay()){
                    tvDay.setText(start.get(Calendar.DAY_OF_MONTH)+"");
                    tvWeek.setText(CalendarUtils.getWeekBytimestamp(data.getStart()));
                }else{
                    tvDay.setText("");
                    tvWeek.setText("");
                }
                tvClassTime.setText(CalendarUtils.formatTime(start)+"-"+CalendarUtils.formatTime(end));
            }else{
                tvDay.setText("");
                tvWeek.setText("");
                tvClassTime.setText("");
            }
        }
    }

    class ItemDateViewHolder extends ParentViewHolder{
        TextView tvData;
        View mView;
        public ItemDateViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvData = (TextView) mView.findViewById(R.id.tv_date);
        }

        @Override
        public void update(int position, ScheduleItem scheduleItem) {
            Long timestamp =((ScheduleDate)scheduleItem).getTimestamp();
            String data = "";
            if (timestamp!=null){
                Calendar calendar = CalendarUtils.timestampToCalendar(timestamp);
                if (calendar!=null){
                    if (CalendarUtils.compareCurrentYear(calendar)==0){
                        data = String.format("%d月",calendar.get(Calendar.MONTH)+1);
                    }else{
                        data = CalendarUtils.formatDate(calendar);
                    }
                }
            }
            tvData.setText(data);
        }

    }

    public int getFirstUnpassMonth(){
        int count = getItemCount();
        int index = 0;
        int childCount = 0;
        for (int i=0;i<count;i++){
            ScheduleItem item = getItem(i);
            if (item.getType()==ScheduleItem.TYPE_DATE){
                if (i==0){
                    index += childCount;
                }else{
                    index += (childCount+1);
                }
                childCount = 0;
            }else{
                if (!((ScheduleCourse)item).getCourse().is_passed()){
                    break;
                }
                childCount++;
            }
        }
        return index;
    }

}
