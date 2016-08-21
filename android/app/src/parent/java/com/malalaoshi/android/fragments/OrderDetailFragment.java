package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.api.FetchOrderApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.image.MalaImageView;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.course.CourseHelper;
import com.malalaoshi.android.course.adapter.CourseTimeAdapter;
import com.malalaoshi.android.course.model.CourseTimeModel;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.pay.PayActivity;
import com.malalaoshi.android.pay.api.DeleteOrderApi;
import com.malalaoshi.android.result.OkResult;
import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.ConversionUtils;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.view.ScrollListView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrderDetailFragment extends BaseFragment {

    private static String TAG = "OrderDetailFragment";
    private static final String ARG_ORDER_ID = "order id";
    private String orderId;

    @Bind(R.id.tv_order_status)
    protected TextView tvOrderStatus;

    @Bind(R.id.tv_teacher_name)
    protected TextView tvTeacherName;

    @Bind(R.id.tv_course_name)
    protected TextView tvCourseName;

    @Bind(R.id.tv_school)
    protected TextView tvSchool;

    @Bind(R.id.iv_teacher_avator)
    protected MalaImageView ivTeacherAvator;

    @Bind(R.id.tv_total_hours)
    protected TextView tvTotalHours;

    @Bind(R.id.lv_show_times)
    protected ScrollListView lvShowTimes;

    @Bind(R.id.rl_pay_way)
    protected RelativeLayout rlPayWay;

    @Bind(R.id.tv_pay_way)
    protected TextView tvPayWay;

    @Bind(R.id.rl_order_time)
    protected RelativeLayout rlOrderTime;

    @Bind(R.id.tv_order_id)
    protected TextView tvOrderId;

    @Bind(R.id.tv_create_order_time)
    protected TextView tvCreateOrderTime;

    @Bind(R.id.ll_pay_order_time)
    protected LinearLayout llPayOrderTime;

    @Bind(R.id.tv_pay_order_time)
    protected TextView tvPayOrderTime;

    @Bind(R.id.rl_operation)
    protected RelativeLayout rlOperation;

    @Bind(R.id.tv_mount)
    protected TextView tvMount;

    @Bind(R.id.tv_cancel_order)
    protected TextView tvCancelOrder;

    @Bind(R.id.tv_submit)
    protected TextView tvSubmit;

    @Bind(R.id.tv_teacher_status)
    protected TextView tvTeacherStatus;


    private CourseTimeAdapter timesAdapter;

    private Order order;

    public static OrderDetailFragment newInstance(String orderId) {
        if (EmptyUtils.isEmpty(orderId)) {
            return null;
        }
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args == null) {
            throw new IllegalArgumentException("arguments can not been null");
        }
        orderId = args.getString(ARG_ORDER_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_detail, container, false);
        ButterKnife.bind(this, view);
        initViews();
        initData();
        return view;
    }

    private void initData() {
        startProcessDialog("正在加载数据···");
        loadData();
    }

    private void initViews() {
        timesAdapter = new CourseTimeAdapter(getActivity());
        lvShowTimes.setAdapter(timesAdapter);
    }

    private void loadData() {
        ApiExecutor.exec(new FetchOrderRequest(this, orderId));
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View view) {
        if (order != null && order.getStatus() != null) {
            if ("u".equals(order.getStatus())) {
                openPayActivity();
            } else {
                startCourseConfirmActivity();
            }
        }
    }

    //启动购买课程页
    private void startCourseConfirmActivity() {
        if (order != null && order.getTeacher() != null) {
            Subject subject = Subject.getSubjectIdByName(order.getSubject());
            Long teacherId = Long.valueOf(order.getTeacher());
            if (teacherId != null && subject != null) {
                CourseConfirmActivity.open(getContext(), teacherId, order.getTeacher_name(), order.getTeacher_avatar(), subject);
            }
        }
    }

    @OnClick(R.id.tv_cancel_order)
    public void onClickCancel(View view) {
        //取消订单
        if (order != null && order.getId() != null && order.getTo_pay() != null) {
            startProcessDialog("正在取消订单...");
            ApiExecutor.exec(new CancelCourseOrderRequest(this, order.getId() + ""));
        }
    }


    private void openPayActivity() {
        if (order == null || order.getId() == null || EmptyUtils.isEmpty(order.getOrder_id()) || order.getTo_pay() == null)
            return;
        CreateCourseOrderResultEntity entity = new CreateCourseOrderResultEntity();
        entity.setId(order.getId() + "");
        entity.setOrder_id(order.getOrder_id());
        entity.setTo_pay((long) order.getTo_pay().doubleValue());
        PayActivity.startPayActivity(entity, getActivity(), true);
        getActivity().finish();
    }

    private void getOrderInfoFailed() {
        MiscUtil.toast("订单信息获取失败!");
    }

    private void getOrderInfoSuccess(Order response) {
        if (response == null) {
            return;
        } else {
            order = response;
            updateOrderInfoUI();
        }
    }

    public void updateOrderInfoUI() {
        if (order == null) return;
        tvTeacherName.setText(order.getTeacher_name());
        tvCourseName.setText(order.getGrade() + " " + order.getSubject());
        tvSchool.setText(order.getSchool());
        tvTotalHours.setText(order.getHours().toString());
        String strTopay = "金额异常";
        Double toPay = order.getTo_pay();
        if (toPay != null) {
            strTopay = String.format("%.2f", toPay * 0.01d);
        }
        ;
        tvMount.setText(strTopay);

        if ("u".equals(order.getStatus())) {
            tvOrderStatus.setText("订单待支付");
            rlPayWay.setVisibility(View.GONE);
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getCreated_at())));
            llPayOrderTime.setVisibility(View.GONE);
            tvCancelOrder.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setText("立即支付");
            rlOperation.setVisibility(View.VISIBLE);
        } else if ("p".equals(order.getStatus())) {
            tvOrderStatus.setText("支付成功");
            rlPayWay.setVisibility(View.VISIBLE);
            tvPayWay.setText(order.getCharge_channel());
            ;
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getCreated_at())));
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getPaid_at())));
            tvCancelOrder.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setText("再次购买");
            rlOperation.setVisibility(View.VISIBLE);
        } else if ("d".equals(order.getStatus())) {
            tvOrderStatus.setText("订单已关闭");
            rlPayWay.setVisibility(View.GONE);
            tvPayWay.setText(order.getCharge_channel());
            ;
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getCreated_at())));
            llPayOrderTime.setVisibility(View.GONE);
            rlOperation.setVisibility(View.VISIBLE);
            tvCancelOrder.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setText("再次购买");
        } else {
            tvOrderStatus.setText("退款成功");
            rlPayWay.setVisibility(View.VISIBLE);
            tvPayWay.setText(order.getCharge_channel());
            ;
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getCreated_at())));
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(CalendarUtils.timestampToTime(ConversionUtils.convertToLong(order.getPaid_at())));
            tvCancelOrder.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.GONE);
            rlOperation.setVisibility(View.GONE);
        }
        if (!order.is_teacher_published()) {
            tvCancelOrder.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.GONE);
            tvTeacherStatus.setVisibility(View.VISIBLE);
        } else {
            tvTeacherStatus.setVisibility(View.GONE);
        }
        String imgUrl = order.getTeacher_avatar();
        ivTeacherAvator.loadCircleImage(imgUrl, R.drawable.ic_default_teacher_avatar);

        //上课时间
        List<String[]> timeslots = order.getTimeslots();

        if (timeslots != null) {
            List<CourseTimeModel> times = CourseHelper.courseTimes(timeslots);
            timesAdapter.addAll(times);
            timesAdapter.notifyDataSetChanged();
        }
    }

    private static final class FetchOrderRequest extends BaseApiContext<OrderDetailFragment, Order> {

        private String orderId;

        public FetchOrderRequest(OrderDetailFragment orderDetailFragment, String orderId) {
            super(orderDetailFragment);
            this.orderId = orderId;
        }

        @Override
        public Order request() throws Exception {
            return new FetchOrderApi().get(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull Order response) {
            if (response != null) {
                get().getOrderInfoSuccess(response);
            } else {
                get().getOrderInfoFailed();
            }
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单信息获取失败,请检查网络!");
        }
    }


    private static final class CancelCourseOrderRequest extends BaseApiContext<OrderDetailFragment, OkResult> {

        private String orderId;

        public CancelCourseOrderRequest(OrderDetailFragment orderDetailFragment, String orderId) {
            super(orderDetailFragment);
            this.orderId = orderId;
        }

        @Override
        public OkResult request() throws Exception {
            return new DeleteOrderApi().delete(orderId);
        }

        @Override
        public void onApiSuccess(@NonNull OkResult response) {
            get().getActivity().finish();
            if (response.isOk()) {
                get().order.setStatus("d");
                MiscUtil.toast("订单已取消!");
            } else {
                MiscUtil.toast("订单取消失败!");
            }
        }

        @Override
        public void onApiFinished() {
            get().stopProcessDialog();
            get().getActivity().finish();
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态取消失败,请检查网络!");
        }
    }

    @Override
    public String getStatName() {
        return "订单详情页";
    }

}
