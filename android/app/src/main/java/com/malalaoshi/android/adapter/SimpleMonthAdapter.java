package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.Toast;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.Cource;
import com.malalaoshi.android.listener.DatePickerController;
import com.malalaoshi.android.view.calendar.SimpleMonthView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SimpleMonthAdapter extends RecyclerView.Adapter<SimpleMonthAdapter.ViewHolder> implements SimpleMonthView.OnDayClickListener {
    protected static final int MONTHS_IN_YEAR = 12;
    private final TypedArray typedArray;
	private final Context mContext;
	private final DatePickerController mController;
    private final Calendar calendar;
    private final Integer firstMonth;
    private final Integer lastMonth;
    private boolean isDragging = false;
    private HashMap<CalendarDay, Integer> countMap;
    private HashMap<CalendarMonth, HashMap<CalendarDay, Integer>> monthCountMap =
            new HashMap<>();

    private HashMap<String,List<Cource>> mapCourse;

    public SimpleMonthAdapter(Context context, DatePickerController datePickerController, TypedArray typedArray) {
        this.typedArray = typedArray;
        calendar = Calendar.getInstance();
        firstMonth = typedArray.getInt(R.styleable.DayPickerView_firstMonth, 0);
        lastMonth = typedArray.getInt(R.styleable.DayPickerView_lastMonth, 11);
		mContext = context;
		mController = datePickerController;
		init();
	}

    public void setMapCourse(HashMap<String, List<Cource>> mapCourse) {
        this.mapCourse = mapCourse;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        final SimpleMonthView simpleMonthView = new SimpleMonthView(mContext, typedArray);
        return new ViewHolder(simpleMonthView, this);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position)
    {
        final SimpleMonthView v = viewHolder.simpleMonthView;

        final HashMap<String, Integer> drawingParams = new HashMap<String, Integer>();
        int month;
        int year;

        month = (firstMonth + (position % MONTHS_IN_YEAR)) % MONTHS_IN_YEAR;
        year = position / MONTHS_IN_YEAR - 1 + calendar.get(Calendar.YEAR);

        v.reuse();

        drawingParams.put(SimpleMonthView.VIEW_PARAMS_YEAR, year);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_MONTH, month);
        drawingParams.put(SimpleMonthView.VIEW_PARAMS_WEEK_START, calendar.getFirstDayOfWeek());
        v.setMonthParams(drawingParams);
        //设置课程信息
        if (mapCourse!=null){
            List<Cource> listCourse = mapCourse.get(year+month+"");
            if (listCourse!=null&&listCourse.size()>0){
                final Map<Integer, List<Cource>> mapCourse1 = new HashMap<>();
                for (int i =0;i<listCourse.size();i++){
                    Cource cource = listCourse.get(i);
                    List<Cource> listCource1 = mapCourse1.get(cource.getData().getDay());
                    if (listCource1==null){
                        listCource1 = new ArrayList<>();
                        mapCourse1.put(cource.getData().getDay(),listCource1);
                    }
                    listCource1.add(cource);
                }
                v.setCourses(mapCourse1);
            }
        }

        v.showMothInfo(isDragging);

        CalendarMonth calendarMonth = new CalendarMonth(year, month);
        if (monthCountMap.containsKey(calendarMonth)) {
             v.setEventSymbols(monthCountMap.get(calendarMonth));
        }
        v.invalidate();
    }

    public long getItemId(int position) {
		return position;
	}

    @Override
    public int getItemCount()
    {
        int itemCount = 10 * MONTHS_IN_YEAR;
        return itemCount;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        final SimpleMonthView simpleMonthView;

        public ViewHolder(View itemView, SimpleMonthView.OnDayClickListener onDayClickListener)
        {
            super(itemView);
            simpleMonthView = (SimpleMonthView) itemView;
            simpleMonthView.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            simpleMonthView.setClickable(true);
            simpleMonthView.setOnDayClickListener(onDayClickListener);
        }
    }

	protected void init() {
        if (typedArray.getBoolean(R.styleable.DayPickerView_currentDaySelected, false))
            onDayTapped(new CalendarDay(System.currentTimeMillis()));
	}

	public void onDayClick(SimpleMonthView simpleMonthView, CalendarDay calendarDay, List<Cource> courses) {
		if (calendarDay != null) {
			onDayTapped(calendarDay);
            /*if (courses!=null&&courses.size()>0){
                Toast.makeText(mContext,courses.get(0).getSubject(),Toast.LENGTH_SHORT).show();
            }*/
            Toast.makeText(mContext,calendarDay.getYear()+"年"+calendarDay.getMonth()+"月"+calendarDay.getDay()+"日",Toast.LENGTH_SHORT).show();
        }
	}

	protected void onDayTapped(CalendarDay calendarDay) {
		mController.onDayOfMonthSelected(calendarDay.year, calendarDay.month, calendarDay.day);
	}


    public void setDragging(boolean isDragging) {
        this.isDragging = isDragging;
        notifyDataSetChanged();
    }

    public void setCountMap(HashMap<CalendarDay, Integer> countMap) {
        this.countMap = countMap;

        monthCountMap.clear();
        Iterator it = countMap.entrySet().iterator();
        while (it.hasNext()) {
            HashMap.Entry<CalendarDay, Integer> entry = (HashMap.Entry<CalendarDay, Integer>) it.next();
            CalendarMonth calendarMonth = new CalendarMonth(entry.getKey());
            if (monthCountMap.containsKey(calendarMonth)) {
                HashMap<CalendarDay, Integer> tempMap = monthCountMap.get(calendarMonth);
                tempMap.put(entry.getKey(), entry.getValue());
            } else {
                HashMap<CalendarDay, Integer> tempMap = new HashMap<>();
                tempMap.put(entry.getKey(), entry.getValue());
                monthCountMap.put(calendarMonth, tempMap);
            }
        }

        notifyDataSetChanged();
    }

    public static class CalendarMonth implements Serializable {
        private Calendar calendar = Calendar.getInstance();

        int month;
        int year;

        public CalendarMonth(CalendarDay day) {
            month = day.month;
            year = day.year;
            calendar.set(Calendar.MONTH, day.month);
            calendar.set(Calendar.YEAR, day.year);
        }

        public CalendarMonth(int year, int month) {
            this.year = year;
            this.month = month;
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, year);
        }

        @Override
        public String toString() {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{ year: ");
            stringBuilder.append(year);
            stringBuilder.append(" month: ");
            stringBuilder.append(month);
            stringBuilder.append(" }");
            return stringBuilder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CalendarMonth) {
                CalendarMonth other = (CalendarMonth) o;
                if (other.year == this.year
                        && other.month == this.month) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return calendar.hashCode() + month * 37 + year;
        }
    }

    public static class CalendarDay implements Serializable
    {
        private static final long serialVersionUID = -5456695978688356202L;
        private Calendar calendar = Calendar.getInstance();

		public int day;
		public int month;
		public int year;

		public CalendarDay() {
			setTime(System.currentTimeMillis());
		}

		public CalendarDay(int year, int month, int day) {
			setDay(year, month, day);
		}

		public CalendarDay(long timeInMillis) {
			setTime(timeInMillis);
		}

		public CalendarDay(Calendar calendar) {
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
		}

		private void setTime(long timeInMillis) {
			if (calendar == null) {
				calendar = Calendar.getInstance();
            }
			calendar.setTimeInMillis(timeInMillis);
			month = this.calendar.get(Calendar.MONTH);
			year = this.calendar.get(Calendar.YEAR);
			day = this.calendar.get(Calendar.DAY_OF_MONTH);
		}

		public void set(CalendarDay calendarDay) {
		    year = calendarDay.year;
			month = calendarDay.month;
			day = calendarDay.day;
		}

		public void setDay(int year, int month, int day) {
			this.year = year;
			this.month = month;
			this.day = day;
		}

        public Date getDate()
        {
            if (calendar == null) {
                calendar = Calendar.getInstance();
            }
            calendar.set(year, month, day);
            return calendar.getTime();
        }

        public int getDay() {
            return day;
        }

        public int getMonth() {
            return month;
        }

        public int getYear() {
            return year;
        }

        @Override
        public String toString()
        {
            final StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("{ year: ");
            stringBuilder.append(year);
            stringBuilder.append(", month: ");
            stringBuilder.append(month);
            stringBuilder.append(", day: ");
            stringBuilder.append(day);
            stringBuilder.append(" }");

            return stringBuilder.toString();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof CalendarDay) {
                CalendarDay other = (CalendarDay) o;
                if (other.year == this.year
                        && other.month == this.month
                        && other.day == this.day) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return calendar.hashCode() + 17 * day + 31 * month + year;
        }
    }
}