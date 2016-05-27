package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.course.CourseHelper;
import com.malalaoshi.android.course.adapter.CourseTimeAdapter;
import com.malalaoshi.android.course.api.CourseTimesApi;
import com.malalaoshi.android.course.model.CourseTimeModel;
import com.malalaoshi.android.dialogs.PromptDialog;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.TimesModel;
import com.malalaoshi.android.pay.PayActivity;
import com.malalaoshi.android.pay.PayManager;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.Number;
import com.malalaoshi.android.view.CircleNetworkImage;
import com.malalaoshi.android.view.ScrollListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/24.
 */
public class ConfirmOrderFragment  extends BaseFragment implements View.OnClickListener {

    private static final String ARG_CREATE_ORDER_INFO = "createCourseOrderEntity";
    private static final String ARG_ORDER_TEACHER_ID = "teacher id";
    private static final String ARG_ORDER_WEEKLY_TIME_SLOTS = "weekly time slots";
    private static final String ARG_ORDER_HOURS = "hours";
    private static final String ARG_ORDER_INFO = "order info";
    private static final String ARG_IS_CONFIRM_ORDER = "is confirm order";
    private static final String ARG_IS_EVALUATED = "is evaluated";

    @Bind(R.id.tv_teacher_name)
    protected TextView tvTeacherName;

    @Bind(R.id.tv_course_name)
    protected TextView tvCourseName;

    @Bind(R.id.tv_school)
    protected TextView tvSchool;

    @Bind(R.id.iv_teacher_avator)
    protected CircleNetworkImage ivTeacherAvator;

    @Bind(R.id.tv_total_hours)
    protected TextView tvTotalHours;

    @Bind(R.id.lv_show_times)
    protected ScrollListView lvShowTimes;


    @Bind(R.id.tv_mount)
    protected TextView tvMount;

    @Bind(R.id.tv_submit)
    protected TextView tvSubmit;

    boolean isEvaluated = true;

    private ImageLoader mImageLoader;

    private CourseTimeAdapter timesAdapter;

    private Order order;

    private CreateCourseOrderEntity createCourseOrderEntity;

    private long hours;

    private String weeklyTimeSlots;

    private long teacherId;

    public static ConfirmOrderFragment newInstance(Order order, CreateCourseOrderEntity entity, long hours, String weeklyTimeSlots, long teacherId, boolean isEvaluated) {
        if (entity!=null&&TextUtils.isEmpty(weeklyTimeSlots)&&hours<=0){
            return null;
        }
        ConfirmOrderFragment fragment = new ConfirmOrderFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_ORDER_INFO, order);
        args.putSerializable(ARG_CREATE_ORDER_INFO, entity);
        args.putBoolean(ARG_IS_CONFIRM_ORDER, true);
        args.putLong(ARG_ORDER_HOURS,hours);
        args.putString(ARG_ORDER_WEEKLY_TIME_SLOTS,weeklyTimeSlots);
        args.putLong(ARG_ORDER_TEACHER_ID,teacherId);
        args.putBoolean(ARG_IS_EVALUATED,isEvaluated);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args==null){
            throw new IllegalArgumentException("arguments can not been null");
        }
        hours = args.getLong(ARG_ORDER_HOURS);
        weeklyTimeSlots = args.getString(ARG_ORDER_WEEKLY_TIME_SLOTS);
        teacherId = args.getLong(ARG_ORDER_TEACHER_ID);
        order  = args.getParcelable(ARG_ORDER_INFO);
        isEvaluated = args.getBoolean(ARG_IS_EVALUATED,true);
        createCourseOrderEntity  = (CreateCourseOrderEntity) args.getSerializable(ARG_CREATE_ORDER_INFO);

        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_confirm_order, container, false);
        ButterKnife.bind(this, view);
        initViews();
        initData();
        setEvent();
        return view;
    }

    private void setEvent() {
        tvSubmit.setOnClickListener(this);
    }

    private void initData() {
        if (order==null) return;
        tvTeacherName.setText(order.getTeacher_name());
        tvCourseName.setText(order.getGrade()+" "+order.getSubject());
        tvSchool.setText(order.getSchool());
        tvTotalHours.setText(String.valueOf(order.getHours()));
        String strTopay = "金额异常";
        Double toPay = order.getTo_pay();
        if(toPay!=null){
            strTopay = Number.subZeroAndDot(toPay);
        };
        tvMount.setText(strTopay);
        String imgUrl = order.getTeacher_avatar();
        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = "";
        }
        ivTeacherAvator.setDefaultImageResId(R.drawable.ic_default_teacher_avatar);
        ivTeacherAvator.setErrorImageResId(R.drawable.ic_default_teacher_avatar);
        ivTeacherAvator.setImageUrl(imgUrl, mImageLoader);
        startProcessDialog("startProcessDialog");
        loadData();
    }

    private void initViews() {
        timesAdapter = new CourseTimeAdapter(getActivity());
        lvShowTimes.setAdapter(timesAdapter);
    }

    private void loadData() {
        ApiExecutor.exec(new FetchCourseTimesRequest(this, teacherId, hours, weeklyTimeSlots));
    }

    private void openPayActivity(CreateCourseOrderResultEntity entity) {
        if (entity==null) return;
        PayActivity.startPayActivity(entity, getActivity(), isEvaluated);
    }

    @Override
    public void onClick(View v) {
        tvSubmit.setOnClickListener(null);
        ApiExecutor.exec(new CreateOrderRequest(this, createCourseOrderEntity));
    }


    private static final class FetchCourseTimesRequest extends BaseApiContext<ConfirmOrderFragment, TimesModel> {

        private long teacherId;
        private String times;
        private long hours;

        public FetchCourseTimesRequest(ConfirmOrderFragment confirmOrderFragment,
                                       long teacherId, long hours, String times) {
            super(confirmOrderFragment);
            this.teacherId = teacherId;
            this.hours = hours;
            this.times = times;
        }

        @Override
        public TimesModel request() throws Exception {
            return new CourseTimesApi().get(teacherId, hours, times);
        }

        @Override
        public void onApiSuccess(@NonNull TimesModel response) {
            get().onFetchCourseTimesSuccess(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            super.onApiFailure(exception);
            MiscUtil.toast("上课时间获取失败");
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().stopProcessDialog();
        }
    }

    private void onFetchCourseTimesSuccess(TimesModel timesModel) {
        timesAdapter.clear();
        List<String[]> timeslots =  new ArrayList<>();
        for (int i=0;timesModel!=null&&i<timesModel.getData().size();i++){
            int size = timesModel.getData().get(i).size();
            timeslots.add(timesModel.getData().get(i).toArray(new String[size]));
        }
        if (timeslots != null){
            List<CourseTimeModel> times = CourseHelper.courseTimes(timeslots);
            timesAdapter.addAll(times);
            timesAdapter.notifyDataSetChanged();
        }
    }

    //创建订单
    private static final class CreateOrderRequest extends
            BaseApiContext<ConfirmOrderFragment, CreateCourseOrderResultEntity> {

        private CreateCourseOrderEntity entity;

        public CreateOrderRequest(ConfirmOrderFragment confirmOrderFragment,
                                  CreateCourseOrderEntity entity) {
            super(confirmOrderFragment);
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
            get().tvSubmit.setOnClickListener(null);
        }

        @Override
        public void onApiFinished() {
            get().tvSubmit.setOnClickListener(get());
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("创建订单失败");
        }
    }

    private void dealOrder(@NonNull CreateCourseOrderResultEntity entity) {
        if (!entity.isOk() && entity.getCode() == -1) {
            DialogUtil.showPromptDialog(
                    getFragmentManager(), R.drawable.ic_timeallocate,
                    "该老师部分时段已被占用，请重新选择上课时间!", "知道了", new PromptDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                        }
                    }, false, false);
        } else {
            openPayActivity(entity);
        }
    }



    @Override
    public String getStatName() {
        return "确认订单页";
    }
}
