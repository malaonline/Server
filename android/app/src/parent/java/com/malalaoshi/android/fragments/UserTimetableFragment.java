package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.listener.DatePickerController;
import com.malalaoshi.android.view.calendar.DayPickerView;

import java.util.Calendar;

/**
 * Created by kang on 16/1/28.
 */
public class UserTimetableFragment extends Fragment implements DatePickerController {
    private DayPickerView calendarView;
    private TextView tvOffDate;
    private LinearLayout llWeek;
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
        return v;
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

    //加载数据
    public void loadDatas(){

        updateDatas();
    }

    private void updateDatas() {

    }
}
