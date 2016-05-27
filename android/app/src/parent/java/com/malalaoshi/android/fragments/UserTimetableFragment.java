package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.SimpleMonthAdapter;
import com.malalaoshi.android.api.TimeTableApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.dialog.CourseDetailDialog;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.listener.DatePickerController;
import com.malalaoshi.android.result.CourseListResult;
import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.MiscUtil;
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
public class UserTimetableFragment extends BaseFragment implements DatePickerController {
    private DayPickerView calendarView;
    private TextView tvOffDate;
    private LinearLayout llWeek;

    private CourseListResult courses;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_usertimetable, container, false);

        calendarView = (DayPickerView) v.findViewById(R.id.calendar_view);
        calendarView.setController(this);

        llWeek = (LinearLayout) v.findViewById(R.id.ll_week);

        calendarView.setOnListScrollListener(new DayPickerView.OnListScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy, Calendar calendar) {
                int month = calendar.get(Calendar.MONTH) + 1;
                tvOffDate.setText(calendar.get(Calendar.YEAR) + "年" + month + "月");
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    tvOffDate.setVisibility(View.VISIBLE);
                    llWeek.setVisibility(View.INVISIBLE);
                } else if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    tvOffDate.setVisibility(View.INVISIBLE);
                    llWeek.setVisibility(View.VISIBLE);
                }

            }
        });

        tvOffDate = (TextView) v.findViewById(R.id.tv_off_date);
        tvOffDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendarView.scrollToToday();
            }
        });

        //下载数据
        initData();
        EventBus.getDefault().register(this);
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
                setCourses(null);
                break;
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
            case BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA:
                loadData();
                break;

        }
    }

    private void initData() {
        loadData();
    }


    public void scrollToToday() {
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
    public void onDayClick(SimpleMonthView simpleMonthView, SimpleMonthAdapter.CalendarDay calendarDay, List<Course> courses) {

        if (courses != null && courses.size() > 0) {
            CourseDetailDialog courseDetailDialog = CourseDetailDialog.newInstance((ArrayList<Course>) courses);
            courseDetailDialog.show(getFragmentManager(), CourseDetailDialog.class.getName());
            StatReporter.courseTimePage();
        }
    }

    private static final class LoadTimeTable extends BaseApiContext<UserTimetableFragment, CourseListResult> {

        public LoadTimeTable(UserTimetableFragment userTimetableFragment) {
            super(userTimetableFragment);
        }

        @Override
        public CourseListResult request() throws Exception {
            return new TimeTableApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull CourseListResult response) {
            get().updateData(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().loadTimeTableFailed();
        }
    }


    //加载数据
    public void loadData() {
        ApiExecutor.exec(new LoadTimeTable(this));
    }

    private void loadTimeTableFailed() {
        MiscUtil.toast(R.string.load_timetable_info_failed);
    }

    private void updateData(CourseListResult courses) {
        List<Course> listCourse = courses.getResults();
        if (null != listCourse) {

            HashMap<String, List<Course>> mapCourse = new HashMap<>();
            for (int i = 0; i < listCourse.size(); i++) {
                Course course = listCourse.get(i);
                SimpleMonthAdapter.CalendarDay calendarDay = CalendarUtils.timestampToCalendarDay(course.getEnd());
                //指定月的课程信息
                List<Course> tempCourses = mapCourse.get(calendarDay.getYear() + "" + calendarDay.getMonth());
                if (tempCourses == null) {
                    tempCourses = new ArrayList<>();
                    mapCourse.put(calendarDay.getYear() + "" + calendarDay.getMonth(), tempCourses);
                }
                tempCourses.add(course);
            }
            setCourses(mapCourse);
        }
    }

    private void setCourses(HashMap<String, List<Course>> mapCourse){
        calendarView.setCourses(mapCourse);
    }

    @Override
    public String getStatName() {
        return "学生课表页";
    }
}
