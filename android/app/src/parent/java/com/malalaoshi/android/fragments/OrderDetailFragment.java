package com.malalaoshi.android.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.OrderRecyclerViewAdapter;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.adapter.TimeLineAdapter;
import com.malalaoshi.android.api.FetchOrderApi;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.course.CourseConfirmActivity;
import com.malalaoshi.android.decoration.TeacherItemDecoration;
import com.malalaoshi.android.entity.CreateCourseOrderResultEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.pay.PayActivity;
import com.malalaoshi.android.pay.api.DeleteOrderApi;
import com.malalaoshi.android.result.OkResult;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.Number;
import com.malalaoshi.android.view.CircleNetworkImage;
import com.malalaoshi.android.view.ScrollListView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;


public class OrderDetailFragment extends Fragment {
    private static String TAG = "OrderDetailFragment";
    private static final String ARG_ORDER_STATUS = "order status";
    private static final String ARG_ORDER_ID = "order id";
    private String orderStatus;
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
    protected CircleNetworkImage ivTeacherAvator;

     @Bind(R.id.tv_total_hours)
    protected TextView tvTotalHours;

    @Bind(R.id.listview)
    protected ScrollListView listview;

    @Bind(R.id.rl_pay_way)
    protected RelativeLayout rlPayWay;

   @Bind(R.id.tv_pay_way)
    protected TextView tvPayWay;

    @Bind(R.id.view_line_pay_way)
    protected View viewLinePayWay;

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

    @Bind(R.id.tv_cancel)
    protected TextView tvCancel;

    @Bind(R.id.tv_submit)
    protected TextView tvSubmit;


    private ImageLoader mImageLoader;

    private Order order;

    private boolean loadFinish = false;

    public static OrderDetailFragment newInstance(String orderId,String orderStatus) {
        if (TextUtils.isEmpty(orderStatus)|| TextUtils.isEmpty(orderId)){
            Log.w(TAG,"args error");
            return null;
        }
        OrderDetailFragment fragment = new OrderDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_STATUS, orderStatus);
        args.putString(ARG_ORDER_ID, orderId);
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
        orderStatus = args.getString(ARG_ORDER_STATUS);
        orderId = args.getString(ARG_ORDER_ID);
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
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

    @Override
    public void onResume() {
        super.onResume();
        if (!loadFinish) {
            DialogUtil.startCircularProcessDialog(getContext(), "正在加载数据", true, false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!loadFinish) {
            DialogUtil.stopProcessDialog();
        }
    }

    private void stopProcess() {
        DialogUtil.stopProcessDialog();
    }

    private void initData() {
        loadData();
    }

    private void initViews() {


    }

    private void loadData() {
        ApiExecutor.exec(new FetchOrderRequest(this, orderId));
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View view){
        if (order!=null&&order.getId()!=null&&order.getTo_pay()!=null&&order.getStatus()!=null){
            if ("u".equals(order.getStatus())){
                openPayActivity();
            }else{
                startCourseConfirmActivity();
            }
        }
    }

    //启动购买课程页
    private void startCourseConfirmActivity() {
        Intent signIntent = new Intent(getContext(), CourseConfirmActivity.class);
        if (order != null && order.getTeacher() != null) {
            signIntent.putExtra(CourseConfirmActivity.EXTRA_TEACHER_ID, order.getTeacher());
            getContext().startActivity(signIntent);
        }
    }

    @OnClick(R.id.tv_cancel)
    public void onClickCancel(View view){
        //取消订单
        if (order!=null&&order.getId()!=null&&order.getTo_pay()!=null){
            startProcessDialog("正在取消订单...");
            ApiExecutor.exec(new CancelCourseOrderRequest(this, order.getId()+""));
        }
    }

    public void startProcessDialog(String message){
        DialogUtil.startCircularProcessDialog(getContext(),message,true,true);
    }

    public void stopProcessDialog(){
        DialogUtil.stopProcessDialog();
    }

    private void openPayActivity() {
        CreateCourseOrderResultEntity entity = new CreateCourseOrderResultEntity();
        entity.setId(order.getId()+"");
        entity.setOrder_id(order.getOrder_id());
        entity.setTo_pay((long)order.getTo_pay().doubleValue());
        boolean isEvaluated = true;
        isEvaluated = false;
        PayActivity.startPayActivity(entity, getActivity(), isEvaluated);
        getActivity().finish();
    }

    private void getOrderFailed() {
    }

    private void getOrderSuccess(Order response) {
        if (response==null) {
            return;
        }
        else{
            order = response;
            updateUI();
        }
    }


    public void updateUI(){
        if (order==null) return;
        tvTeacherName.setText(order.getTeacher_name());
        tvCourseName.setText(order.getGrade()+" "+order.getSubject());
        tvSchool.setText(order.getSchool());
        tvTotalHours.setText(order.getHours().toString());
        String strTopay = "金额异常";
        Double toPay = order.getTo_pay();
        if(toPay!=null){
            strTopay = Number.subZeroAndDot(toPay*0.01d);
        };
        tvMount.setText(strTopay);
        //rvClassTime;
        listview.setAdapter(new TimeLineAdapter(getContext(),new ArrayList<Order>()));

        if ("u".equals(order.getStatus())){
            tvOrderStatus.setText("订单待支付");
            rlPayWay.setVisibility(View.GONE);
            viewLinePayWay.setVisibility(View.GONE);
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(order.getCreated_at());
            llPayOrderTime.setVisibility(View.GONE);
            tvCancel.setVisibility(View.VISIBLE);
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setText("立即支付");
            rlOperation.setVisibility(View.VISIBLE);
        }else if ("p".equals(order.getStatus())){
            tvOrderStatus.setText("支付成功");
            rlPayWay.setVisibility(View.VISIBLE);
            tvPayWay.setText(order.getCharge_channel()); ;
            viewLinePayWay.setVisibility(View.VISIBLE);
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(order.getCreated_at());
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(order.getPaid_at());
            tvCancel.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setText("再次购买");
            rlOperation.setVisibility(View.VISIBLE);
        }else if ("d".equals(order.getStatus())){
            tvOrderStatus.setText("订单已关闭");
            rlPayWay.setVisibility(View.GONE);
            tvPayWay.setText(order.getCharge_channel()); ;
            viewLinePayWay.setVisibility(View.GONE);
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(order.getCreated_at());
            llPayOrderTime.setVisibility(View.GONE);
            tvPayOrderTime.setText(order.getPaid_at());
            tvCancel.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.VISIBLE);
            tvSubmit.setText("再次购买");
            rlOperation.setVisibility(View.VISIBLE);
        }else{
            tvOrderStatus.setText("退款成功");
            rlPayWay.setVisibility(View.VISIBLE);
            tvPayWay.setText(order.getCharge_channel()); ;
            viewLinePayWay.setVisibility(View.VISIBLE);
            tvOrderId.setText(order.getOrder_id());
            tvCreateOrderTime.setText(order.getCreated_at());
            llPayOrderTime.setVisibility(View.VISIBLE);
            tvPayOrderTime.setText(order.getPaid_at());
            tvCancel.setVisibility(View.GONE);
            tvSubmit.setVisibility(View.GONE);
            rlOperation.setVisibility(View.GONE);
        }

        String imgUrl = order.getTeacher_avatar();
        if (TextUtils.isEmpty(imgUrl)) {
            imgUrl = "";
        }
        ivTeacherAvator.setDefaultImageResId(R.drawable.ic_default_teacher_avatar);
        ivTeacherAvator.setErrorImageResId(R.drawable.ic_default_teacher_avatar);
        ivTeacherAvator.setImageUrl(imgUrl, mImageLoader);
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
            if (response!=null){
                get().getOrderSuccess(response);
            }else{
                get().getOrderFailed();
            }
        }

        @Override
        public void onApiFinished() {
            get().loadFinish = true;
            get().stopProcess();
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
            get().stopProcessDialog();
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
            MiscUtil.toast("订单取消失败!");
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("订单状态取消失败,请检查网络!");
        }
    }
}
