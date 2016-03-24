package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.SimpleMonthAdapter;
import com.malalaoshi.android.dialog.CommentDialog;
import com.malalaoshi.android.dialog.CourseDetailDialog;
import com.malalaoshi.android.entity.Cource;
import com.malalaoshi.android.event.BusEvent;
import com.malalaoshi.android.listener.DatePickerController;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.result.CourseListResult;
import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.UserManager;
import com.malalaoshi.android.view.calendar.DayPickerView;
import com.malalaoshi.android.view.calendar.SimpleMonthView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/1/28.
 */
public class UserTimetableFragment extends Fragment implements DatePickerController {
    private DayPickerView calendarView;
    private TextView tvOffDate;
    private LinearLayout llWeek;

    private CourseListResult courses;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_usertimetable,container,false);

        calendarView = (DayPickerView) v.findViewById(R.id.calendar_view);
        calendarView.setController(this);

        llWeek = (LinearLayout)v.findViewById(R.id.ll_week);

        calendarView.setOnListScrollListener(new DayPickerView.OnListScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy, Calendar calendar) {
                int month = calendar.get(Calendar.MONTH) + 1;
                tvOffDate.setText(calendar.get(Calendar.YEAR)+"年"+month+"月");
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState==RecyclerView.SCROLL_STATE_DRAGGING){
                    tvOffDate.setVisibility(View.VISIBLE);
                    llWeek.setVisibility(View.INVISIBLE);
                }else if (newState==RecyclerView.SCROLL_STATE_IDLE){
                    tvOffDate.setVisibility(View.INVISIBLE);
                    llWeek.setVisibility(View.VISIBLE);
                }

            }
        });

        tvOffDate = (TextView)v.findViewById(R.id.tv_off_date);
        tvOffDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollToToday();
            }
        });

        //下载数据
        initDatas();
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()){
            case BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA:
                loadDatas();
                break;
        }
    }


    private void initDatas() {
        loadDatas();
    }


    public void scrollToTady(){
        calendarView.scrollToToday();
    }

    @Override
    public int getMaxYear() {
        return 0;
    }

    @Override
    public void onDayOfMonthSelected(int year, int month, int day) {


    }

    @Override
    public void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay, List<Cource> courses) {

        if (courses!=null&&courses.size()>0){
            CourseDetailDialog courseDetailDialog = CourseDetailDialog.newInstance((ArrayList<Cource>) courses);
            courseDetailDialog.show(getFragmentManager(), CourseDetailDialog.class.getName());
        }
    }


    //加载数据
    public void loadDatas(){
        if (!UserManager.getInstance().isLogin()){
            return;
        }
        NetworkSender.getTimetable(new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                courses = JsonUtil.parseStringData(json.toString(), CourseListResult.class);
                //courses = JsonUtil.parseData(R.raw.courses, CourseListResult.class, getContext());
                if (courses != null) {
                    updateDatas(courses);
                } else {
                    //数据请求失败
                    loadTimeTableFailed();
                }
            }

            @Override
            public void onFailed(VolleyError error) {
                //dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                loadTimeTableFailed();
            }
        });
    }

    private void loadTimeTableFailed() {
        MiscUtil.toast(R.string.load_timetable_info_failed);
    }

    private void updateDatas(CourseListResult courses) {
        List<Cource> listCource = courses.getResults();
        if (null!=listCource){

            HashMap<String, List<Cource>> mapCourse = new HashMap<>();
            for (int i=0;i<listCource.size();i++){
                Cource cource = listCource.get(i);
                SimpleMonthAdapter.CalendarDay calendarDay = CalendarUtils.timestampToCalendarDay(cource.getEnd());
                //指定月的课程信息
                List<Cource> tempCourses = mapCourse.get(calendarDay.getYear() +""+ calendarDay.getMonth());
                if (tempCourses==null){
                    tempCourses = new ArrayList<>();
                    mapCourse.put(calendarDay.getYear()+ "" + calendarDay.getMonth() , tempCourses);
                }
                tempCourses.add(cource);
            }
            calendarView.setCourses(mapCourse);
        }
    }
}
