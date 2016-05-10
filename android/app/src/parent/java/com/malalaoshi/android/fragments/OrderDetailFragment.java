package com.malalaoshi.android.fragments;

import android.os.Bundle;
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

import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class OrderDetailFragment extends Fragment {
    private static String TAG = "OrderDetailFragment";
    private static final String ARG_PAGE_TYPE = "page flag";
    private static final String ARG_ORDER_STATUS = "order status";
    private static final String ARG_ORDER_ID = "order id";
    private String orderStatus;
    private String orderId;

    @Bind(R.id.rl_order_status)
    protected RelativeLayout rlOrderStatus;

    @Bind(R.id.tv_order_status)
    protected TextView tvOrderStatus;

    @Bind(R.id.tv_teacher_name)
    protected TextView tvTeacherName;

    @Bind(R.id.tv_course_name)
    protected TextView tvCourseName;

    @Bind(R.id.tv_school)
    protected TextView tvSchool;

   /* @Bind(R.id.tv_total_hours)
    protected TextView tvTotalHours;

    @Bind(R.id.rv_class_time)
    protected RecyclerView rvClassTime;*/

    @Bind(R.id.rl_pay_way)
    protected RelativeLayout rlPayWay;

   @Bind(R.id.tv_pay_way)
    protected TextView tvPayWay;

    /* @Bind(R.id.sub_order_time_info)
     protected View subOrderHoursInfo;*/

    @Bind(R.id.tv_order_id)
     protected TextView tvOrderId;

     @Bind(R.id.tv_create_order_time)
     protected TextView tvCreateOrderTime;

     @Bind(R.id.ll_pay_order_time)
     protected LinearLayout llPayOrderTime;

     @Bind(R.id.tv_pay_order_time)
     protected TextView tvPayOrderTime;

    public static OrderDetailFragment newInstance(String orderStatus, String orderId) {
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
        loadData();
    }

    private void initViews() {


    }

    private void loadData() {
    }

    @OnClick(R.id.tv_submit)
    public void onClickSubmit(View view){

    }

    @OnClick(R.id.tv_cancel)
    public void onClickCancel(View view){

    }
}
