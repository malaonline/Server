package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.entity.ScheduleCourse;
import com.malalaoshi.android.entity.ScheduleDate;
import com.malalaoshi.android.entity.ScheduleItem;
import com.malalaoshi.android.util.CalendarUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ScheduleItem> mScheduleData = new ArrayList<>();

    private static final int TYPE_ITEM_COURSE = 3;
    private static final int TYPE_ITEM_DATE = 2;
    private static final int TYPE_FOOTER = 1;

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //没有更多数据,到底了
    public static final int NODATA_LOADING = 2;
    //没有更多数据,到底了
    public static final int GONE_LOADING = 3;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;

    private int startIndex = 0;

    public ScheduleAdapter(Context context){
    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            if (mScheduleData.get(position).getType()==ScheduleItem.TYPE_COURSE){
                return TYPE_ITEM_COURSE;
            }else{
                return TYPE_ITEM_DATE;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM_COURSE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_course_item, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_ITEM_DATE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_date_item, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new ItemDateViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footerview, null);
            int height = parent.getContext().getResources().getDimensionPixelSize(R.dimen.list_footer_height);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, height));
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type==TYPE_FOOTER){
            FooterViewHolder footViewHolder=(FooterViewHolder)holder;
            if (position<=0){
                ((FooterViewHolder) holder).view.setVisibility(View.GONE);
            }else{
                ((FooterViewHolder) holder).view.setVisibility(View.VISIBLE);
            }

            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footViewHolder.textView.setText("上拉加载更多...");
                    footViewHolder.progressBar.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footViewHolder.textView.setText("加载中...");
                    footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    break;
                case NODATA_LOADING:
                    footViewHolder.textView.setText("到底了,没有更多数据了!");
                    footViewHolder.progressBar.setVisibility(View.GONE);
                    break;
                case GONE_LOADING:
                    ((FooterViewHolder) holder).view.setVisibility(View.GONE);
                    break;
            }
        } else if (type==TYPE_ITEM_DATE){
            ScheduleDate scheduleDate = (ScheduleDate) mScheduleData.get(position);
            ((ItemDateViewHolder)holder).update(position,scheduleDate);
        } else{
            ScheduleCourse scheduleCourse = (ScheduleCourse) mScheduleData.get(position);
            ((ItemViewHolder)holder).update(position,scheduleCourse);
        }
    }

    //添加数据
    public void addItem(List<Course> newDatas) {
        Collections.sort(newDatas);
        if (newDatas!=null&&newDatas.size()>0){
            List<ScheduleItem> tempDatas = new ArrayList<>();
            Calendar currentCalendar = null;
            boolean flag = true;
            for (int i = 0; i < newDatas.size(); i++) {
                Course course = newDatas.get(i);
                if (currentCalendar==null){
                    currentCalendar =  CalendarUtils.timestampToCalendar(course.getStart());
                    tempDatas.add(new ScheduleDate(course.getStart()));
                    if (course.is_passed()){
                        startIndex++;
                    }else{
                        if (flag){
                            flag = false;
                            startIndex++;
                        }
                    }
                }else{
                    Calendar start =  CalendarUtils.timestampToCalendar(course.getStart());
                    if (currentCalendar.get(Calendar.YEAR)!=start.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=start.get(Calendar.MONTH)){
                        currentCalendar = start;
                        tempDatas.add(new ScheduleDate(course.getStart()));
                        if (course.is_passed()){
                            startIndex++;
                        }else{
                            if (flag){
                                flag = false;
                                startIndex++;
                            }
                        }
                    }
                }
                if (course.is_passed()){
                    startIndex++;
                }
                tempDatas.add(new ScheduleCourse(course));
            }
            startIndex++;
            if (mScheduleData.size()>0){
                ScheduleItem scheduleItem = mScheduleData.get(0);
                if (scheduleItem.getType()==ScheduleItem.TYPE_DATE){
                    Calendar firstCalendar =  CalendarUtils.timestampToCalendar(((ScheduleDate)scheduleItem).getTimestamp());
                    if (currentCalendar.get(Calendar.YEAR)==firstCalendar.get(Calendar.YEAR)&&currentCalendar.get(Calendar.MONTH)==firstCalendar.get(Calendar.MONTH)){
                        mScheduleData.remove(0);
                    }
                }
            }
            mScheduleData.addAll(0,tempDatas);
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
                    if (mScheduleData.size()>0){
                        ScheduleItem scheduleItem = mScheduleData.get(0);
                        if (scheduleItem.getType()==ScheduleItem.TYPE_COURSE){
                            Calendar lastCalendar =  CalendarUtils.timestampToCalendar(((ScheduleCourse)scheduleItem).getCourse().getStart());
                            if (currentCalendar.get(Calendar.YEAR)!=lastCalendar.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=lastCalendar.get(Calendar.MONTH)){
                                mScheduleData.add(new ScheduleDate(course.getStart()));
                            }
                        }else{
                            mScheduleData.add(new ScheduleDate(course.getStart()));
                        }
                    }else{
                        mScheduleData.add(new ScheduleDate(course.getStart()));
                    }
                }else{
                    Calendar start =  CalendarUtils.timestampToCalendar(course.getStart());
                    if (currentCalendar.get(Calendar.YEAR)!=start.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=start.get(Calendar.MONTH)){
                        currentCalendar = start;
                        mScheduleData.add(new ScheduleDate(course.getStart()));
                    }
                }
                mScheduleData.add(new ScheduleCourse(course));
            }
            notifyDataSetChanged();
        }
    }

    public int getStartIndex(){
        return startIndex;
    }

    public void setMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    public int getMoreStatus(){
        return load_more_status;
    }


    @Override
    public int getItemCount() {
        return mScheduleData.size() + 1;
    }

    public void clear() {
        mScheduleData.clear();
        startIndex = 0;
        notifyDataSetChanged();
    }

    abstract class ParentViewHolder extends RecyclerView.ViewHolder{

        public ParentViewHolder(View itemView) {
            super(itemView);
        }

        public abstract void update(int position, ScheduleItem scheduleItem);
    }

    class FooterViewHolder extends ParentViewHolder {

        View view;
        TextView textView;
        ContentLoadingProgressBar progressBar;
        public FooterViewHolder(View view) {
            super(view);
            this.view = view;
            textView = (TextView) view.findViewById(R.id.load_textview);
            progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progressbar);
        }

        @Override
        public void update(int position, ScheduleItem scheduleItem) {
        }
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
                rlSchedule.setBackgroundColor(resources.getColor(R.color.item_color_past_bg));
            } else {
                if(CalendarUtils.compareCurrentDate(start)==0){
                    tvDay.setTextColor(resources.getColor(R.color.item_color_bg));
                    tvWeek.setTextColor(resources.getColor(R.color.item_color_bg));
                }else{
                    tvDay.setTextColor(resources.getColor(R.color.text_color_darkgray));
                    tvWeek.setTextColor(resources.getColor(R.color.text_color_darkgray));
                }
                rlSchedule.setBackgroundColor(resources.getColor(R.color.item_color_bg));

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
                        data = String.format("%02d月",calendar.get(Calendar.MONTH));
                    }else{
                        data = CalendarUtils.formatDate(calendar);
                    }
                }
            }
            tvData.setText(data);
        }

    }

}
