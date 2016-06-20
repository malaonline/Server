package com.malalaoshi.android.course;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.activitys.ConfirmOrderActivity;
import com.malalaoshi.android.api.SchoolListApi;
import com.malalaoshi.android.api.TeacherInfoApi;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.usercenter.api.EvaluatedApi;
import com.malalaoshi.android.core.usercenter.entity.Evaluated;
import com.malalaoshi.android.core.utils.DialogUtils;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.course.adapter.CourseTimeAdapter;
import com.malalaoshi.android.course.api.CourseWeekDataApi;
import com.malalaoshi.android.course.model.CourseTimeModel;
import com.malalaoshi.android.entity.CouponEntity;
import com.malalaoshi.android.entity.CourseDateEntity;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.CoursePriceUI;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.entity.SchoolUI;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.pay.coupon.CouponActivity;
import com.malalaoshi.android.receiver.WeakFragmentReceiver;
import com.malalaoshi.android.result.CoursePriceListResult;
import com.malalaoshi.android.result.SchoolListResult;
import com.malalaoshi.android.util.LocManager;
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
public class CourseConfirmFragment extends BaseFragment implements AdapterView.OnItemClickListener,
        CourseDateChoiceView.OnCourseDateChoiceListener, View.OnClickListener {

    private static final int REQUEST_CODE_COUPON = 0x10;

    private static final String ARG_TEACHER_ID = "teacher id";
    private static final String ARG_TEACHER_NAME = "teacher name";
    private static final String ARG_TEACHER_AVATOR = "teacher avator";
    private static final String ARG_SUBJECT = "subject";

    public static CourseConfirmFragment newInstance(Object[] schools, Object[] prices, Object teacherId, Object
            subject, String teacherAvator, String teacherName) {
        CourseConfirmFragment fragment = new CourseConfirmFragment();
        fragment.init(schools, prices, teacherId, subject, teacherAvator, teacherName);
        return fragment;
    }

    public static CourseConfirmFragment newInstance(Long teacherId, String teacherName, String teacherAvator, Subject subject) {
        if (teacherId == null || subject == null) return null;
        CourseConfirmFragment fragment = new CourseConfirmFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_TEACHER_ID, teacherId);
        args.putString(ARG_TEACHER_NAME, teacherName);
        args.putString(ARG_TEACHER_AVATOR, teacherAvator);
        args.putParcelable(ARG_SUBJECT, subject);
        fragment.setArguments(args);
        return fragment;
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
    private long noCouponSum; //以分为单位

    @Bind(R.id.tv_submit)
    protected View submitView;

    private final List<CoursePriceUI> coursePrices;
    private final List<SchoolUI> schoolList;
    private SchoolUI currentSchool;
    private GradeAdapter gradeAdapter;
    private SchoolAdapter schoolAdapter;
    private CourseTimeAdapter timesAdapter;
    //学校FootView
    private View footView;
    //teacher id
    private Long teacher;
    //teacher avatar
    private String teacherAvatar;
    //teacher name
    private String teacherName;
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

    private BroadcastReceiver receiver;

    private final class Receiver extends WeakFragmentReceiver {

        public Receiver(Fragment fragment) {
            super(fragment);
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (UserManager.ACTION_LOGOUT.equals(intent.getAction())) {
                UserManager.getInstance().startLoginActivity();
            }
        }
    }

    public CourseConfirmFragment() {
        coursePrices = new ArrayList<>();
        schoolList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_confirm, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if (args != null) {
            Long teacherId = args.getLong(ARG_TEACHER_ID);
            String teacherName = args.getString(ARG_TEACHER_NAME);
            String teacherAvator = args.getString(ARG_TEACHER_AVATOR);
            Subject subject = args.getParcelable(ARG_SUBJECT);
            init(teacherId, teacherName, teacherAvator, subject);
        }
        //initGridView();
        //initSchoolListView();
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
        choiceView.setOnCourseNoteClickListener(new CourseDateChoiceView.onCourseNoteClickListener() {
            @Override
            public void onClick() {
                openCourseNoteDialog();
            }
        });
        submitView.setOnClickListener(this);
        cutReviewView.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        EventBus.getDefault().register(this);
        receiver = new Receiver(this);
        if (!UserManager.getInstance().isLogin()) {
            getActivity().finish();
            UserManager.getInstance().startLoginActivity();
        }
        return view;
    }

    private void openCourseNoteDialog() {
        NoteDialog dialog = new NoteDialog();
        dialog.setTitle("课程保留规则");
        dialog.setContent(R.string.course_expire_reserve);
        DialogUtils.showDialog(getFragmentManager(), dialog, "course_note");
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter();
        filter.addAction(UserManager.ACTION_LOGOUT);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(receiver);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    private void init(Long teacherId, String teacherName, String teacherAvatar, Subject subject) {
        if (teacherId != null && subject != null) {
            this.teacher = teacherId;
            this.teacherAvatar = teacherAvatar;
            this.teacherName = teacherName;
            this.subject = subject;
        }
        startProcessDialog("正在加载数据···");
        fetchEvaluated();
        fetchSchools();
        fetchCoursePrices();
    }


    private void init(Object[] schools, Object[] prices, Object teacherId, Object subject, String teacherAvator, String teacherName) {
        if (teacherId != null) {
            this.teacher = (Long) teacherId;
            this.teacherAvatar = teacherAvator;
            this.teacherName = teacherName;
        }
        //final String[] gradeList = MalaApplication.getInstance()
        //        .getApplicationContext().getResources().getStringArray(R.array.grade_list);
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
                text = ((CoursePrice) price).getGrade().getName();//gradeList[priceUI.getPrice().getGrade().getId().intValue() - 1];
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
        timesAdapter = new CourseTimeAdapter(getActivity());
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
        }
        setHoursText();
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
        StringBuilder weeklyTimeSlots = new StringBuilder();

        entity.setTeacher(teacher);
        List<Integer> list = new ArrayList<>();
        for (CourseDateEntity item : selectedTimeSlots) {
            list.add((int) item.getId());
            weeklyTimeSlots.append(item.getId()).append(" ");
        }
        entity.setWeekly_time_slots(list);

        //Context context, Order order, long hours,String weeklyTimeSlots,long teacherId
        Order order = new Order();
        order.setTeacher(String.valueOf(teacher));
        order.setTeacher_name(teacherName);
        order.setTeacher_avatar(teacherAvatar);
        order.setHours(currentHours);
        order.setGrade(currentGrade.getPrice().getGrade().getName());
        order.setSubject(subject.getName());
        order.setTo_pay((double) calculateCost());
        order.setSchool(currentSchool.getSchool().getName());
        boolean isEvaluated = true;
        if (evaluated != null && !evaluated.isEvaluated()) {
            isEvaluated = false;
        }
        ConfirmOrderActivity.open(getContext(), order, currentHours, weeklyTimeSlots.toString(), teacher, entity, isEvaluated);
    }

    private void openScholarShipActivity() {
        CouponActivity.launch(getActivity(), REQUEST_CODE_COUPON, coupon, noCouponSum);
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
            updateCourseTimes(null);
            return;
        }
        updateCourseTimes(CourseHelper.calculateCourse(currentHours, selectedTimeSlots));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == placeListView.getId()) {
            for (SchoolUI item : schoolList) {
                item.setCheck(false);
            }
            currentSchool = (SchoolUI) schoolAdapter.getItem(position);
            currentSchool.setCheck(true);
            if (schoolList.size() > 1&&footView.getParent() == null) {
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

    private void updateCourseTimes(List<CourseTimeModel> times) {
        timesAdapter.clear();
        if (EmptyUtils.isEmpty(times)) {
            timesAdapter.notifyDataSetChanged();
            return;
        }
        timesAdapter.addAll(times);
        timesAdapter.notifyDataSetChanged();
        ((ImageView) showTimesImageView).setImageDrawable(
                getResources().getDrawable(R.drawable.ic_drop_up));
    }

    private float calculateCost() {
        if (currentGrade == null) {
            return 0.0f;
        }
        float sum = currentGrade.getPrice().getPrice() * currentHours;
        noCouponSum = (long) sum;
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
        return sum;
    }

    /**
     * 计算总费用
     */
    private void calculateSum() {
        float sum = calculateCost();
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

    @Override
    public String getStatName() {
        return "课程确认";
    }

    //是否需要测评建档
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


    private void fetchCoursePrices() {
        if (teacher != null) {
            ApiExecutor.exec(new CoursePriceListRequest(this, teacher));
        }
    }

    private static final class CoursePriceListRequest extends BaseApiContext<CourseConfirmFragment, Teacher> {
        private Long teacherId;

        public CoursePriceListRequest(CourseConfirmFragment courseConfirmFragment, Long teacherId) {
            super(courseConfirmFragment);
            this.teacherId = teacherId;
        }

        @Override
        public Teacher request() throws Exception {
            return new TeacherInfoApi().get(teacherId);
        }

        @Override
        public void onApiSuccess(@NonNull Teacher response) {
            if (response.getPrices() != null) {
                CoursePriceListResult priceListResult = new CoursePriceListResult();
                priceListResult.setResults(response.getPrices());
                get().onLoadCoursePricesSuccess(priceListResult);
            } else {
                get().loadFailure("价格信息下载失败!");
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
            get().loadFailure("价格信息下载失败!");
        }

        @Override
        public void onApiFinished() {
        }

    }

    private void onLoadCoursePricesSuccess(CoursePriceListResult response) {
        if (response == null || response.getResults() == null) {
            return;
        }
        List<CoursePrice> prices = response.getResults();
        //final String[] gradeList = MalaApplication.getInstance()
        //        .getApplicationContext().getResources().getStringArray(R.array.grade_list);
        if (prices != null) {
            String text;
            for (Object price : prices) {
                CoursePriceUI priceUI = new CoursePriceUI((CoursePrice) price);
                text = ((CoursePrice) price).getGrade().getName();;//gradeList[priceUI.getPrice().getGrade().getId().intValue() - 1];
                text += "  " + (priceUI.getPrice().getPrice() / 100f) + "/小时";
                priceUI.setGradePrice(text);
                coursePrices.add(priceUI);
            }
        }
        initGridView();
    }

    private void fetchSchools() {
        if (subject != null) {
            ApiExecutor.exec(new LoadSchoolListRequest(this));
        }
    }

    private static final class LoadSchoolListRequest extends BaseApiContext<CourseConfirmFragment, SchoolListResult> {

        public LoadSchoolListRequest(CourseConfirmFragment courseConfirmFragment) {
            super(courseConfirmFragment);
        }

        @Override
        public SchoolListResult request() throws Exception {
            return new SchoolListApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull SchoolListResult response) {
            if (response.getResults() != null) {
                get().onLoadSchoolListSuccess(response);
            } else {
                get().loadFailure("学校信息加载失败!");
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
        }

    }

    private void loadFailure(String message) {
        MiscUtil.toast(message);
    }

    private void onLoadSchoolListSuccess(SchoolListResult response) {
        //获取体验中心
        List<School> schools = new ArrayList<>();
        schools.addAll(response.getResults());

        //获取位置
        Location location = LocManager.getInstance().getLocation();
        if (location != null) {
            //根据位置排序
            LocationUtil.sortByDistance(schools, location.getLatitude(), location.getLongitude());
        }
        for (Object school : schools) {
            SchoolUI schoolUI = new SchoolUI((School) school);
            schoolList.add(schoolUI);
        }
        initSchoolListView();
    }
}
