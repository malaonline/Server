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


   /* //添加数据
    public void addItem(List<Course> newDatas) {
        Collections.sort(newDatas);

        int count = 0;
        int childCount = 0;
        int index = 0;
        if (newDatas!=null&&newDatas.size()>0){
            List<ScheduleItem> tempDatas = new ArrayList<>();
            Calendar currentCalendar = null;
            boolean flag = true;
            for (int i = 0; i < newDatas.size(); i++) {
                Course course = newDatas.get(i);
                if (currentCalendar==null){
                    currentCalendar =  CalendarUtils.timestampToCalendar(course.getStart());
                    tempDatas.add(new ScheduleDate(course.getStart()));
                    count++;
                    childCount = 0;
                }else{
                    Calendar start =  CalendarUtils.timestampToCalendar(course.getStart());
                    if (currentCalendar.get(Calendar.YEAR)!=start.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=start.get(Calendar.MONTH)){
                        currentCalendar = start;
                        tempDatas.add(new ScheduleDate(course.getStart()));
                        count++;
                        childCount = 0;
                    }
                }

                if (course.is_passed()){
                    childCount++;
                    startIndex++;
                }else{
                    if (flag){
                        flag = false;
                        index = count - childCount - 1;
                    }
                }
                count++;
                if (course.is_passed()&&i==newDatas.size()-1){
                    if (flag){
                        flag = false;
                        index = count - childCount - 1;
                    }
                }

                tempDatas.add(new ScheduleCourse(course));
            }
            //startIndex++;
            if (getItemCount()>0){
                ScheduleItem scheduleItem = (ScheduleItem) getItem(0);
                if (scheduleItem.getType()==ScheduleItem.TYPE_DATE){
                    Calendar firstCalendar =  CalendarUtils.timestampToCalendar(((ScheduleDate)scheduleItem).getTimestamp());
                    if (currentCalendar.get(Calendar.YEAR)==firstCalendar.get(Calendar.YEAR)&&currentCalendar.get(Calendar.MONTH)==firstCalendar.get(Calendar.MONTH)){
                        getDataList().remove(0);
                        count--;
                    }
                }
            }
            if (getItemCount()<=0){
                startIndex = index;
            } else {
                startIndex += count;
            }
            getDataList().addAll(0,tempDatas);
            notifyDataSetChanged();
        }
    }

    public void addMoreItem(List<Course> newDatas) {
        Collections.sort(newDatas);
        if (newDatas!=null&&newDatas.size()>0){
            Calendar currentCalendar = null;
            for (int i = 0; i < newDatas.size(); i++) {
                Course course = newDatas.get(i);
                if (currentCalendar==null){
                    currentCalendar =  CalendarUtils.timestampToCalendar(course.getStart());
                    if (getItemCount()>0){
                        ScheduleItem scheduleItem = (ScheduleItem) getItem(0);
                        if (scheduleItem.getType()==ScheduleItem.TYPE_COURSE){
                            Calendar lastCalendar =  CalendarUtils.timestampToCalendar(((ScheduleCourse)scheduleItem).getCourse().getStart());
                            if (currentCalendar.get(Calendar.YEAR)!=lastCalendar.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=lastCalendar.get(Calendar.MONTH)){
                                getDataList().add(new ScheduleDate(course.getStart()));
                            }
                        }else{
                            getDataList().add(new ScheduleDate(course.getStart()));
                        }
                    }else{
                        getDataList().add(new ScheduleDate(course.getStart()));
                    }
                }else{
                    Calendar start =  CalendarUtils.timestampToCalendar(course.getStart());
                    if (currentCalendar.get(Calendar.YEAR)!=start.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=start.get(Calendar.MONTH)){
                        currentCalendar = start;
                        getDataList().add(new ScheduleDate(course.getStart()));
                    }
                }
                getDataList().add(new ScheduleCourse(course));
            }
            notifyDataSetChanged();
        }
    }*/

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
                tvDay.setTextColor(resources.getColor(R.color.text_color_dlg));
                tvWeek.setTextColor(resources.getColor(R.color.text_color_dlg));
                rlSchedule.setBackgroundResource(R.drawable.bg_corner_normal);
            } else {
                if(CalendarUtils.compareCurrentDate(start)==0){
                    tvDay.setTextColor(resources.getColor(R.color.item_color_bg));
                    tvWeek.setTextColor(resources.getColor(R.color.item_color_bg));
                }else{
                    tvDay.setTextColor(resources.getColor(R.color.text_color_darkgray));
                    tvWeek.setTextColor(resources.getColor(R.color.text_color_darkgray));
                }
                rlSchedule.setBackgroundResource(R.drawable.bg_corner_blue);

            }
            tvGradeCourse.setText(data.getGrade()+" "+data.getSubject());
            tvTeacherName.setText(data.getTeacher().getName());
            tvClassPosition.setText(data.getSchool());
            if (start!=null&&end!=null){
                tvDay.setText(start.get(Calendar.DAY_OF_MONTH)+"");
                tvWeek.setText(CalendarUtils.getWeekBytimestamp(data.getStart()));
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
