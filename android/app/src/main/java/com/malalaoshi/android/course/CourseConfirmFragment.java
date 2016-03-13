package com.malalaoshi.android.course;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.base.BaseFragment;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.CoursePriceUI;
import com.malalaoshi.android.entity.CourseDateEntity;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.SchoolUI;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.MiscUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Course confirm fragment
 * Created by tianwei on 3/5/16.
 */
public class CourseConfirmFragment extends BaseFragment implements AdapterView.OnItemClickListener, CourseDateChoiceView.OnCourseDateChoiceListener, View.OnClickListener {
    public static CourseConfirmFragment newInstance(Object[] schools, Object[] prices, Object teacherId) {
        CourseConfirmFragment fragment = new CourseConfirmFragment();
        fragment.init(schools, prices, teacherId);
        return fragment;
    }

    public CourseConfirmFragment() {
        coursePrices = new ArrayList<>();
        schoolList = new ArrayList<>();
        choiceList = new ArrayList<>();
    }

    @Bind(R.id.gv_course)
    protected GridView gridView;

    @Bind(R.id.ll_place)
    protected ListView placeListView;

    @Bind(R.id.list_choice)
    protected ListView choiceListView;

    @Bind(R.id.choice_time_view)
    protected CourseDateChoiceView choiceView;

    @Bind(R.id.ll_week)
    protected View weekContainer;

    @Bind(R.id.tv_hours)
    protected TextView hoursView;

    @Bind(R.id.iv_minus)
    protected View minusView;

    @Bind(R.id.iv_add)
    protected View addView;

    @Bind(R.id.iv_show_times)
    protected View showTimesView;

    @Bind(R.id.lv_show_times)
    protected ListView timesListView;

    private final List<CoursePriceUI> coursePrices;
    private final List<SchoolUI> schoolList;
    private SchoolUI currentSchool;
    private final List<String> choiceList;
    private PriceAdapter priceAdapter;
    private SchoolAdapter schoolAdapter;
    private TimesAdapter timesAdapter;
    private ChoiceAdapter choiceAdapter;
    private View footView;
    private Long teacher;
    private int minHours;
    private int currentHours;
    private String selectedTimeSlots;
    private boolean isShowTimes;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_confirm, null);
        ButterKnife.bind(this, view);
        initGridView();
        initSchoolListView();
        initChoiceListView();
        initTimesListView();
        minHours = 2;
        hoursView.setText(minHours + "");
        minusView.setOnClickListener(this);
        addView.setOnClickListener(this);
        showTimesView.setOnClickListener(this);
        choiceView.setOnCourseDateChoicedListener(this);
        return view;
    }

    private void init(Object[] schools, Object[] prices, Object teacherId) {
        if (teacherId != null) {
            this.teacher = (Long) teacherId;
        }
        final String[] gradeList = MalaApplication.getInstance()
                .getApplicationContext().getResources().getStringArray(R.array.grade_list);
        if (schools != null) {
            for (Object school : schools) {
                SchoolUI schoolUI = new SchoolUI((School) school);
                schoolList.add(schoolUI);
            }
        }
        if (prices != null) {
            String text;
            for (Object price : prices) {
                CoursePriceUI priceUI = new CoursePriceUI((CoursePrice) price);
                text = gradeList[priceUI.getPrice().getGrade().getId().intValue()];
                text += " " + ((int) priceUI.getPrice().getPrice()) + "/小时";
                priceUI.setGradePrice(text);
                coursePrices.add(priceUI);
            }
        }
    }

    private void initChoiceListView() {
        choiceAdapter = new ChoiceAdapter(getActivity());
        choiceListView.setAdapter(choiceAdapter);
    }

    private void initTimesListView() {
        timesAdapter = new TimesAdapter(getActivity());
        timesListView.setAdapter(timesAdapter);
    }

    private void fetchWeekData() {
        if (teacher == null || currentSchool == null) {
            return;
        }
        NetworkSender.getCourseWeek(teacher, currentSchool.getSchool().getId(), new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                try {
                    choiceView.setData(CourseDateEntity.format(json.toString()));
                    weekContainer.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                    MiscUtil.toast("课表数据错误");
                }

            }

            @Override
            public void onFailed(VolleyError error) {
                Log.i("AABB", "error");
            }
        });
    }

    @Override
    public void onCourseDateChoice(List<Long> sections) {
        minHours = sections.size() * 2;
        if (currentHours < minHours) {
            currentHours = minHours;
            hoursView.setText(currentHours + "");
        }
        selectedTimeSlots = "";
        for (Long time : sections) {
            selectedTimeSlots += time + "+";
        }
        if (selectedTimeSlots.endsWith("+")) {
            selectedTimeSlots = selectedTimeSlots.substring(0, selectedTimeSlots.length() - 1);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_minus) {
            if (currentHours - 2 >= minHours) {
                currentHours -= 2;
                hoursView.setText(currentHours + "");
            }
        } else if (v.getId() == R.id.iv_add) {
            currentHours += 2;
            hoursView.setText(currentHours + "");
        } else if (v.getId() == R.id.iv_show_times) {
            if (!isShowTimes) {
                fetchCourseTimes();
            } else {
                //hide
            }
        }
    }

    private void fetchCourseTimes() {
        NetworkSender.fetchCourseTimes(teacher, selectedTimeSlots, currentHours + "", new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Times times = mapper.readValue(json.toString(), Times.class);
                    Log.i("aabb", "dd");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                timesAdapter.clear();
                timesAdapter.addAll(null);
                timesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailed(VolleyError error) {

            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == placeListView.getId()) {
            for (SchoolUI item : schoolList) {
                item.setCheck(false);
            }
            currentSchool = (SchoolUI) schoolAdapter.getItem(position);
            currentSchool.setCheck(true);
            if (footView.getParent() == null) {
                placeListView.addFooterView(footView);
            }
            schoolAdapter.clear();
            schoolAdapter.add(currentSchool);
            weekContainer.setVisibility(View.GONE);
            minHours = 2;
            selectedTimeSlots = "";
            schoolAdapter.notifyDataSetChanged();
            fetchWeekData();
        }
        if (parent.getId() == gridView.getId()) {
            priceAdapter.setCurrentItem(position);
        }
    }

    private void initGridView() {
        priceAdapter = new PriceAdapter(getActivity());
        priceAdapter.addAll(coursePrices);
        gridView.setAdapter(priceAdapter);
        gridView.setOnItemClickListener(this);
    }

    private void initSchoolListView() {
        if (schoolList.size() > 1) {
            footView = View.inflate(getActivity(), R.layout.listview_course_foot_view, null);
            footView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    schoolAdapter.clear();
                    schoolList.remove(currentSchool);
                    schoolList.add(0, currentSchool);
                    schoolAdapter.addAll(schoolList);
                    schoolAdapter.notifyDataSetChanged();
                    placeListView.removeFooterView(v);
                }
            });
            placeListView.addFooterView(footView);
        }
        schoolAdapter = new SchoolAdapter(getActivity());
        if (schoolList.size() > 0) {
            currentSchool = schoolList.get(0);
            schoolAdapter.clear();
            schoolAdapter.add(currentSchool);
        }
        placeListView.setAdapter(schoolAdapter);
        placeListView.setOnItemClickListener(this);
    }

    private static class PriceAdapter extends MalaBaseAdapter<CoursePriceUI> {

        private int currentItem;

        public PriceAdapter(Context context) {
            super(context);
            currentItem = -1;
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            return View.inflate(context, R.layout.view_course_price_item, null);
        }

        @Override
        protected void fillView(int position, View convertView, CoursePriceUI data) {
            TextView view = (TextView) convertView;
            if (data.isCheck()) {
                view.setTextColor(Color.WHITE);
                view.setBackgroundResource(R.drawable.bg_course_price_pressed);
            } else {
                view.setTextColor(context.getResources().getColor(R.color.theme_blue));
                view.setBackgroundResource(R.drawable.bg_course_price_normal);
            }
            ((TextView) convertView).setText(data.getGradePrice());
        }

        public void setCurrentItem(int item) {
            if (currentItem == item) {
                return;
            }
            if (currentItem >= 0 && currentItem < getCount()) {
                getList().get(currentItem).setCheck(false);
            }
            if (item >= 0 && item < getCount()) {
                getList().get(item).setCheck(true);
            }
            currentItem = item;
            notifyDataSetChanged();
        }
    }

    private static class SchoolAdapter extends MalaBaseAdapter<SchoolUI> {

        public SchoolAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = View.inflate(context, R.layout.view_course_place_item, null);
            ViewHolder holder = new ViewHolder();
            holder.nameView = (TextView) view.findViewById(R.id.tv_name);
            holder.addressView = (TextView) view.findViewById(R.id.tv_location);
            holder.distanceView = (TextView) view.findViewById(R.id.tv_distance);
            holder.checkView = (ImageView) view.findViewById(R.id.iv_check);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, SchoolUI data) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.nameView.setText(data.getSchool().getName());
            holder.addressView.setText(data.getSchool().getAddress());
            holder.distanceView.setText("< " + LocationUtil.formatRegion(data.getSchool().getRegion()));
            holder.checkView.setImageResource(data.isCheck() ? R.drawable.ic_check : R.drawable.ic_check_out);
        }

        public class ViewHolder {
            TextView nameView;
            TextView addressView;
            TextView distanceView;
            ImageView checkView;
        }
    }

    private static class ChoiceAdapter extends MalaBaseAdapter<String> {
        public ChoiceAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            return new TextView(context);
        }

        @Override
        protected void fillView(int position, View convertView, String data) {

        }
    }

    private static class TimesAdapter extends MalaBaseAdapter<String> {

        public TimesAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = View.inflate(context, R.layout.view_course_selected_times, null);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, String data) {
            //
        }
    }

    private static class Times {
        private List<List<String>> data;
    }
}
