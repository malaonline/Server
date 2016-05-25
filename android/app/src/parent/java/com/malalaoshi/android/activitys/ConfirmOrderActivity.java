package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.entity.CreateCourseOrderEntity;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.fragments.ConfirmOrderFragment;
import com.malalaoshi.android.fragments.OrderDetailFragment;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/24.
 */
public class ConfirmOrderActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    private static String EXTRA_CREATE_ORDER_INFO = "createCourseOrderEntity";
    private static String EXTRA_ORDER_INFO = "order_info";
    private static String EXTRA_ORDER_TEACHER_ID = "teacher id";
    private static String EXTRA_ORDER_WEEKLY_TIME_SLOTS = "weekly time slots";
    private static String EXTRA_ORDER_HOURS = "hours";
    private static String EXTRA_IS_EVALUATED = "isEvaluated";

    @Bind(R.id.title_view)
    protected TitleBarView titleView;

    /**
     * 确认订单
     * @param context
     * @param order
     */
    public static void open(Context context, Order order, long hours, String weeklyTimeSlots, long teacherId, CreateCourseOrderEntity entity, boolean isEvaluated) {
        if (context!=null&&entity!=null&&order!=null&&hours>0&&!TextUtils.isEmpty(weeklyTimeSlots)) {
            Intent intent = new Intent(context, ConfirmOrderActivity.class);
            intent.putExtra(EXTRA_ORDER_INFO, order);
            intent.putExtra(EXTRA_CREATE_ORDER_INFO, entity);
            intent.putExtra(EXTRA_ORDER_HOURS,hours);
            intent.putExtra(EXTRA_ORDER_WEEKLY_TIME_SLOTS,weeklyTimeSlots);
            intent.putExtra(EXTRA_ORDER_TEACHER_ID,teacherId);
            intent.putExtra(EXTRA_IS_EVALUATED,true);
            context.startActivity(intent);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initViews();
        titleView.setOnTitleBarClickListener(this);
    }

    private void initViews() {
        Intent intent = getIntent();
        titleView.setTitle("确认订单");
        Order order = intent.getParcelableExtra(EXTRA_ORDER_INFO);
        CreateCourseOrderEntity entity = (CreateCourseOrderEntity) intent.getSerializableExtra(EXTRA_CREATE_ORDER_INFO);
        long hours = intent.getLongExtra(EXTRA_ORDER_HOURS,0);
        String weeklyTimeSlots = intent.getStringExtra(EXTRA_ORDER_WEEKLY_TIME_SLOTS);
        long teacherId = intent.getLongExtra(EXTRA_ORDER_TEACHER_ID,0);
        boolean isEvaluated = intent.getBooleanExtra(EXTRA_IS_EVALUATED,true);
        ConfirmOrderFragment confirmOrderFragment = ConfirmOrderFragment.newInstance(order,entity,hours,weeklyTimeSlots.toString(),teacherId,isEvaluated);
        FragmentUtil.openFragment(R.id.order_fragment, getSupportFragmentManager(), null, confirmOrderFragment, OrderDetailFragment.class.getName());
    }


    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected String getStatName() {
        return "确认订单";
    }
}
