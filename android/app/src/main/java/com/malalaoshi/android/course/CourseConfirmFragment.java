package com.malalaoshi.android.course;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.api.EvaluatedApi;
import com.malalaoshi.android.core.usercenter.entity.Evaluated;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.course.api.CourseWeekDataApi;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.entity.CourseDateEntity;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.CoursePriceUI;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.SchoolUI;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.pay.CouponActivity;
import com.malalaoshi.android.pay.PayActivity;
import com.malalaoshi.android.pay.PayManager;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.Number;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;

/**
 * Course confirm fragment
 * Created by tianwei on 3/5/16.
 */
public class CourseConfirmFragment extends BaseFragment implements AdapterView.OnItemClickListener, CourseDateChoiceView.OnCourseDateChoiceListener, View.OnClickListener {

    private static final int REQUEST_CODE_COUPON = 0x10;

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

    @Bind(R.id.iv_show_times)
    protected View showTimesImageView;


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

    @Bind(R.id.rl_price)
    protected RelativeLayout rlPrice;

    @Bind(R.id.tv_price)
    protected TextView tvPrice;

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
    //学校FootView
    private View footView;
    //teacher id
    private Long teacher;
    //当前最小的小时数
    private int minHours;
    //当前选择的小时数
    private int currentHours;
    //选择的时间段
    private List<CourseDateEntity> selectedTimeSlots;
    //是否要展示上课时间列表
    private boolean isShowingTimes;
    //当前选择的上课年级
    private CoursePriceUI currentGrade;
    //当前选择的奖学金
    private CouponEntity coupon;
    //当前的课程名
    private Subject subject;
    //标识是否是第一次购买
    private Evaluated evaluated;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_confirm, container, false);
        ButterKnife.bind(this, view);
        initGridView();
        initSchoolListView();
        initChoiceListView();
        initTimesListView();
        minHours = 2;
        setCurrentHours(2);
        setHoursText();
        isShowingTimes = true;
        minusView.setOnClickListener(this);
        addView.setOnClickListener(this);
        showTimesLayout.setOnClickListener(this);
        scholarshipLayout.setOnClickListener(this);
        reviewLayout.setOnClickListener(this);
        choiceView.setOnCourseDateChoiceListener(this);
        submitView.setOnClickListener(this);
        cutReviewView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        EventBus.getDefault().register(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
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
                text = gradeList[priceUI.getPrice().getGrade().getId().intValue() - 1];
                text += "  " + (priceUI.getPrice().getPrice() / 100f) + "/小时";
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
        ChoiceAdapter choiceAdapter = new ChoiceAdapter(getActivity());
        choiceListView.setAdapter(choiceAdapter);
        selectedTimeSlots = new ArrayList<>();
    }

    private void initTimesListView() {
        timesAdapter = new TimesAdapter(getActivity());
        timesListView.setAdapter(timesAdapter);
    }

    private static final class FetchWeekDataRequest extends
            BaseApiContext<CourseConfirmFragment, String> {

        private long teacherId;
        private long schoolId;

        public FetchWeekDataRequest(CourseConfirmFragment courseConfirmFragment, long teacherId, long schoolId) {
            super(courseConfirmFragment);
            this.teacherId = teacherId;
            this.schoolId = schoolId;
        }

        @Override
        public String request() throws Exception {
            return new CourseWeekDataApi().get(teacherId, schoolId);
        }

        @Override
        public void onApiSuccess(@NonNull String response) {
            get().onFetchWeekDataSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("课表数据错误");
        }
    }

    private void onFetchWeekDataSuccess(String response) {
        try {
            choiceView.setData(CourseDateEntity.format(response));
            weekContainer.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            MiscUtil.toast("课表数据错误");
        }
    }

    private void fetchWeekData() {
        if (teacher == null || currentSchool == null) {
            return;
        }
        ApiExecutor.exec(new FetchWeekDataRequest(this, teacher, currentSchool.getSchool().getId()));
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_RELOAD_FETCHEVALUATED:
                fetchEvaluated();
                break;
        }
    }

    private void fetchEvaluated() {
        if (subject != null) {
            ApiExecutor.exec(new FetchEvaluatedRequest(this, subject.getId()));
        }
    }

    @Override
    public void onCourseDateChoice(List<CourseDateEntity> sections) {
        minHours = sections.size() * 2;
        minHours = minHours < 2 ? 2 : minHours;
        if (currentHours < minHours) {
            setCurrentHours(minHours);
            setHoursText();
        }
        selectedTimeSlots = sections;
        calculateCourseTimes();
        ((ImageView) showTimesImageView).setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
    }

    private void setCurrentHours(int hours) {
        currentHours = hours;
        calculateCourseTimes();
    }

    private void setHoursText() {
        hoursView.setText(String.valueOf(currentHours));
        calculateSum();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_minus) {
            if (currentHours - 2 >= minHours) {
                setCurrentHours(currentHours - 2);
                setHoursText();
            }
            ((ImageView) showTimesImageView).setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
        } else if (v.getId() == R.id.iv_add) {
            setCurrentHours(currentHours + 2);
            setHoursText();
            ((ImageView) showTimesImageView).setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
        } else if (v.getId() == R.id.rl_show_time_container) {
            if (isShowingTimes) {
                ((ImageView) showTimesImageView).setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
                isShowingTimes = false;
                timesListView.setVisibility(View.GONE);
            } else {
                ((ImageView) showTimesImageView).setImageDrawable(getResources().getDrawable(R.drawable.ic_drop_up));
                timesListView.setVisibility(View.VISIBLE);
                isShowingTimes = true;
            }
        } else if (v.getId() == R.id.rl_scholarship_container) {
            StatReporter.clickScholarship(getStatName());
            openScholarShipActivity();
        } else if (v.getId() == R.id.rl_review_layout) {
            startActivity(new Intent(getActivity(), SettingRecordActivity.class));
            StatReporter.evaluatePage(getStatName());
        } else if (v.getId() == R.id.tv_submit) {
            onSubmit();
            StatReporter.submitCourse(getStatName());
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
        if (EmptyUtils.isEmpty(selectedTimeSlots)) {
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
        List<Integer> list = new ArrayList<>();
        for (CourseDateEntity item : selectedTimeSlots) {
            list.add((int) item.getId());
        }
        entity.setWeekly_time_slots(list);
        /**
         * 为什么把创建订单外面，因为创建订单时有加载时间，可以方便做加载动画
         */
        submitView.setOnClickListener(null);
        ApiExecutor.exec(new CreateOrderRequest(this, entity));
    }

    private void dealOrder(@NonNull CreateCourseOrderResultEntity entity) {
        if (!entity.isOk() && entity.getCode() == -1) {
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "该老师部分时段已被占用，请重新选择上课时间!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            //刷新数据
                            fetchWeekData();
                        }
                    }, false, false);
        } else {
            coupon = null;
            scholarView.setText("未使用奖学金");
            calculateSum();
            openPayActivity(entity);
        }
    }

    private void openPayActivity(CreateCourseOrderResultEntity entity) {
        boolean isEvaluated = true;
        if (evaluated != null && !evaluated.isEvaluated()) {
            isEvaluated = false;
        }
        PayActivity.startPayActivity(entity, getActivity(), isEvaluated);
    }

    private void openScholarShipActivity() {
        CouponActivity.launch(getActivity(), REQUEST_CODE_COUPON, coupon);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }
        if (REQUEST_CODE_COUPON == requestCode) {
            CouponEntity coupon = data.getParcelableExtra(CouponActivity.EXTRA_COUPON);
            refreshCoupon(coupon);
        }
    }

    private void refreshCoupon(CouponEntity coupon) {
        if (coupon != null && coupon.isCheck()) {
            this.coupon = coupon;
            String sum = Number.subZeroAndDot(Double.valueOf(coupon.getAmount()) * 0.01d);
            scholarView.setText("-￥" + sum);
            calculateSum();
        } else {
            this.coupon = null;
            calculateSum();
            scholarView.setText("未使用奖学金");
        }
    }

    private void calculateCourseTimes() {
        if (EmptyUtils.isEmpty(selectedTimeSlots)) {
            return;
        }
        updateCourseTimes(CourseHelper.calculateCourse(currentHours, selectedTimeSlots));
    }

    private void updateCourseTimes(List<String> times) {
        timesAdapter.clear();
        if (times != null) {
            timesAdapter.addAll(times);
            timesAdapter.notifyDataSetChanged();
        }
        ((ImageView) showTimesImageView).setImageDrawable(
                getResources().getDrawable(R.drawable.ic_drop_up));
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
            setCurrentHours(2);
            setHoursText();
            calculateSum();
            selectedTimeSlots = new ArrayList<>();
            calculateCourseTimes();
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
            rlPrice.setVisibility(View.VISIBLE);
            float price = sum <= 0 ? 1 : sum;
            price = price / 100f;
            tvPrice.setText("¥ " + String.valueOf(price));
            sum -= Integer.valueOf(coupon.getAmount());
        } else {
            rlPrice.setVisibility(View.GONE);
        }
        sum = sum <= 0 ? 1 : sum;
        sum = sum / 100f;
        amountView.setText("¥ " + String.valueOf(sum));
    }

    private void initGridView() {
        gradeAdapter = new GradeAdapter(getActivity());
        gradeAdapter.addAll(coursePrices);
        gridView.setAdapter(gradeAdapter);
        gridView.setOnItemClickListener(this);
        if (coursePrices.size() > 0) {
            gradeAdapter.setCurrentItem(0);
            currentGrade = (CoursePriceUI) gradeAdapter.getItem(0);
        }
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
            currentSchool.setCheck(true);
            schoolAdapter.add(currentSchool);
            fetchWeekData();
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
            if (data.getSchool().getDistance() == null) {
                holder.distanceView.setText("未知");
            } else {
                holder.distanceView.setText("< " + LocationUtil.formatDistance(data.getSchool().getDistance()));
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
            AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, height);
            view.setLayoutParams(params);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, String data) {
            ((TextView) convertView).setText(data);
        }

    }

    @Override
    public String getStatName() {
        return "课程确认";
    }

    private static final class CreateOrderRequest extends
            BaseApiContext<CourseConfirmFragment, CreateCourseOrderResultEntity> {

        private CreateCourseOrderEntity entity;

        public CreateOrderRequest(CourseConfirmFragment courseConfirmFragment,
                                  CreateCourseOrderEntity entity) {
            super(courseConfirmFragment);
            this.entity = entity;
        }

        @Override
        public CreateCourseOrderResultEntity request() throws Exception {
            return PayManager.getInstance().createOrder(entity);
        }

        @Override
        public void onApiSuccess(@NonNull CreateCourseOrderResultEntity response) {
            get().dealOrder(response);
        }

        @Override
        public void onApiStarted() {
            get().submitView.setOnClickListener(null);
        }

        @Override
        public void onApiFinished() {
            get().submitView.setOnClickListener(get());
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("创建订单失败");
        }
    }


    private static final class FetchEvaluatedRequest extends BaseApiContext<CourseConfirmFragment, Evaluated> {

        private long subjectId;

        public FetchEvaluatedRequest(CourseConfirmFragment courseConfirmFragment, Long subjectId) {
            super(courseConfirmFragment);
            this.subjectId = subjectId;
        }

        @Override
        public Evaluated request() throws Exception {
            return new EvaluatedApi().get(subjectId);
        }

        @Override
        public void onApiSuccess(@NonNull Evaluated response) {
            get().evaluated = response;
            if (!response.isEvaluated()) {
                get().reviewLayout.setVisibility(View.VISIBLE);
                get().evaluatedLine.setVisibility(View.VISIBLE);
            }
        }
    }
}
