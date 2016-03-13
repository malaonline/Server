package com.malalaoshi.android.course;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.base.BaseFragment;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.entity.CourseDateEntity;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.CoursePriceUI;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.SchoolUI;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.pay.CouponActivity;
import com.malalaoshi.android.pay.PayActivity;
import com.malalaoshi.android.pay.PayManager;
import com.malalaoshi.android.pay.ResultCallback;
import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.MiscUtil;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Course confirm fragment
 * Created by tianwei on 3/5/16.
 */
public class CourseConfirmFragment extends BaseFragment implements AdapterView.OnItemClickListener, CourseDateChoiceView.OnCourseDateChoiceListener, View.OnClickListener {
    public static CourseConfirmFragment newInstance(Object[] schools, Object[] prices, Object teacherId, Object subject) {
        CourseConfirmFragment fragment = new CourseConfirmFragment();
        fragment.init(schools, prices, teacherId, subject);
        return fragment;
    }

    public CourseConfirmFragment() {
        coursePrices = new ArrayList<>();
        schoolList = new ArrayList<>();
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

    @Bind(R.id.lv_show_times)
    protected ListView timesListView;

    @Bind(R.id.rl_show_time_container)
    protected View showTimesLayout;

    @Bind(R.id.rl_scholarship_container)
    protected View scholarshipLayout;

    @Bind(R.id.tv_scholarship)
    protected TextView scholarView;

    @Bind(R.id.rl_review_layout)
    protected View reviewLayout;

    @Bind(R.id.line_evaluated)
    protected View evaluatedLine;

    @Bind(R.id.tv_cut_down)
    protected TextView cutReviewView;

    @Bind(R.id.tv_mount)
    protected TextView amountView;

    @Bind(R.id.tv_submit)
    protected View submitView;

    private final List<CoursePriceUI> coursePrices;
    private final List<SchoolUI> schoolList;
    private SchoolUI currentSchool;
    private GradeAdapter gradeAdapter;
    private SchoolAdapter schoolAdapter;
    private TimesAdapter timesAdapter;
    private ChoiceAdapter choiceAdapter;
    //学校FootView
    private View footView;
    //teacher id
    private Long teacher;
    //当前最小的小时数
    private int minHours;
    //当前选择的小时数
    private int currentHours;
    //选择的时间段
    private String selectedTimeSlots;
    //是否要展示上课时间列表
    private boolean isShowingTimes;
    //是否要更新上课时间列表
    private boolean shouldUpdateTimes;
    //当前选择的上课年级
    private CoursePriceUI currentGrade;
    //当前选择的奖学金
    private CouponEntity coupon;
    //当前的课程名
    private Subject subject;

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
        currentHours = 2;
        setHoursText();
        minusView.setOnClickListener(this);
        addView.setOnClickListener(this);
        showTimesLayout.setOnClickListener(this);
        scholarshipLayout.setOnClickListener(this);
        reviewLayout.setOnClickListener(this);
        choiceView.setOnCourseDateChoiceListener(this);
        submitView.setOnClickListener(this);
        cutReviewView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        return view;
    }

    private void init(Object[] schools, Object[] prices, Object teacherId, Object subject) {
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
                text += " " + (priceUI.getPrice().getPrice() / 100f) + "/小时";
                priceUI.setGradePrice(text);
                coursePrices.add(priceUI);
            }
        }
        if (subject != null) {
            this.subject = Subject.getSubjectIdByName(subject.toString());
        } else {
            this.subject = null;
        }
        fetchEvaluated();
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
            }
        });
    }

    private void fetchEvaluated() {
        if (subject != null) {
            NetworkSender.getEvaluated(subject.getId(), new NetworkListener() {
                @Override
                public void onSucceed(Object json) {
                    try {
                        JSONObject js = new JSONObject(json.toString());
                        if (!js.optBoolean("evaluated")) {
                            reviewLayout.setVisibility(View.VISIBLE);
                            evaluatedLine.setVisibility(View.VISIBLE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailed(VolleyError error) {

                }
            });
        }
    }

    @Override
    public void onCourseDateChoice(List<Long> sections) {
        minHours = sections.size() * 2;
        minHours = minHours < 2 ? 2 : minHours;
        if (currentHours < minHours) {
            currentHours = minHours;
            setHoursText();
        }
        selectedTimeSlots = "";
        for (Long time : sections) {
            selectedTimeSlots += time + "+";
        }
        if (selectedTimeSlots.endsWith("+")) {
            selectedTimeSlots = selectedTimeSlots.substring(0, selectedTimeSlots.length() - 1);
        }
        timesListView.setVisibility(View.GONE);
        isShowingTimes = false;
    }

    private void setHoursText() {
        shouldUpdateTimes = true;
        hoursView.setText(String.valueOf(currentHours));
        calculateSum();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_minus) {
            if (currentHours - 2 >= minHours) {
                currentHours -= 2;
                setHoursText();
            }
        } else if (v.getId() == R.id.iv_add) {
            currentHours += 2;
            setHoursText();
        } else if (v.getId() == R.id.rl_show_time_container) {
            if (isShowingTimes) {
                timesListView.setVisibility(View.GONE);
                isShowingTimes = false;
                return;
            }
            if (!shouldUpdateTimes) {
                timesListView.setVisibility(View.VISIBLE);
                isShowingTimes = true;
                return;
            }
            fetchCourseTimes();
        } else if (v.getId() == R.id.rl_scholarship_container) {
            openScholarShipActivity();
        } else if (v.getId() == R.id.rl_review_layout) {
            startActivity(new Intent(getActivity(), SettingRecordActivity.class));
        } else if (v.getId() == R.id.tv_submit) {
            onSubmit();
        }
    }

    private void onSubmit() {
        if (currentGrade == null) {
            MiscUtil.toast("请选择上课年级");
            return;
        }
        if (currentSchool == null) {
            MiscUtil.toast("请选择上课地点");
            return;
        }
        if (TextUtils.isEmpty(selectedTimeSlots)) {
            MiscUtil.toast("请选择上课时间");
            return;
        }
        CreateCourseOrderEntity entity = new CreateCourseOrderEntity();
        if (coupon != null) {
            entity.setCoupon(coupon.getId());
        }
        entity.setGrade(currentGrade.getPrice().getGrade().getId());
        entity.setHours(currentHours);
        entity.setSchool(currentSchool.getSchool().getId());
        if (subject != null) {
            entity.setSubject(subject.getId());
        } else {
            entity.setSubject(0);
        }
        entity.setTeacher(teacher);
        String[] array = selectedTimeSlots.split("\\+");
        List<Integer> list = new ArrayList<>();
        for (String value : array) {
            list.add(Integer.valueOf(value));
        }
        entity.setWeekly_time_slots(list);
        /**
         * 为什么把创建订单外面，因为创建订单时有加载时间，可以方便做加载动画
         */
        PayManager.getInstance().createOrder(entity, new ResultCallback<Object>() {
            @Override
            public void onResult(Object entity) {
                CreateCourseOrderResultEntity result = JsonUtil.parseStringData(
                        entity.toString(), CreateCourseOrderResultEntity.class);
                if (result == null) {
                    MiscUtil.toast("创建订单失败");
                } else {
                    openPayActivity(result);
                }
            }
        });
    }

    private void openPayActivity(CreateCourseOrderResultEntity entity) {
        PayActivity.startPayActivity(entity, getActivity());
    }

    private void openScholarShipActivity() {
        Intent intent = new Intent(getActivity(), CouponActivity.class);
        getActivity().startActivityForResult(intent, 10);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            coupon = data.getParcelableExtra("coupon");
            scholarView.setText("-￥" + coupon.getAmount());
            calculateSum();
        }
    }

    private void fetchCourseTimes() {
        NetworkSender.fetchCourseTimes(teacher, selectedTimeSlots, currentHours + "", new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                Times times = JsonUtil.parseStringData(json.toString(), Times.class);
                timesAdapter.clear();
                if (times != null) {
                    timesAdapter.addAll(times.getDisplayTimes());
                    timesAdapter.notifyDataSetChanged();
                }
                timesListView.setVisibility(View.VISIBLE);
                shouldUpdateTimes = false;
                isShowingTimes = true;
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
            currentHours = 2;
            setHoursText();
            calculateSum();
            selectedTimeSlots = "";
            schoolAdapter.notifyDataSetChanged();
            fetchWeekData();
        }
        if (parent.getId() == gridView.getId()) {
            gradeAdapter.setCurrentItem(position);
            currentGrade = (CoursePriceUI) gradeAdapter.getItem(position);
            calculateSum();
        }
    }

    /**
     * 计算总费用
     */
    private void calculateSum() {
        if (currentGrade == null) {
            return;
        }
        float sum = currentGrade.getPrice().getPrice() * currentHours;
        if (coupon != null) {
            sum -= Integer.valueOf(coupon.getAmount());
        }
        sum = sum < 0 ? 0 : sum;
        sum = sum / 100f;
        amountView.setText(String.valueOf(sum));
    }

    private void initGridView() {
        gradeAdapter = new GradeAdapter(getActivity());
        gradeAdapter.addAll(coursePrices);
        gridView.setAdapter(gradeAdapter);
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

    private static class GradeAdapter extends MalaBaseAdapter<CoursePriceUI> {

        private int currentItem;

        public GradeAdapter(Context context) {
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
            if (data.getSchool().getRegion() == null) {
                holder.distanceView.setText("未知");
            } else {
                holder.distanceView.setText("< " + LocationUtil.formatRegion(data.getSchool().getRegion()));
            }
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
            int height = context.getResources().getDimensionPixelOffset(R.dimen.course_time_height);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            view.setLayoutParams(params);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, String data) {
            ((TextView) convertView).setText(data);
        }

    }

    private static class Times {
        private List<List<String>> data;
        private List<String> displayTimes;

        public List<List<String>> getData() {
            return data;
        }

        public void setData(List<List<String>> data) {
            this.data = data;
        }

        public List<String> getDisplayTimes() {
            if (MiscUtil.isEmpty(data)) {
                return new ArrayList<>();
            }
            displayTimes = new ArrayList<>();
            for (List<String> item : data) {
                try {
                    String[] begins = CalendarUtils.format(item.get(0));
                    String[] ends = CalendarUtils.format(item.get(1));
                    displayTimes.add(String.format("%s (%s-%s)", begins[0], begins[1], ends[1]));
                } catch (Exception e) {

                }
            }
            return displayTimes;
        }
    }
}
