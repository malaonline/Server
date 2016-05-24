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
import com.malalaoshi.android.fragments.OrderDetailFragment;
import com.malalaoshi.android.util.DialogUtil;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/5.
 */
public class OrderInfoActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    private static String EXTRA_ORDER_ID = "order_id";
    @Bind(R.id.title_view)
    protected TitleBarView titleView;

    public static void open(Context context, String orderId) {
        if (!TextUtils.isEmpty(orderId)) {
            Intent intent = new Intent(context, OrderInfoActivity.class);
            intent.putExtra(EXTRA_ORDER_ID, orderId);
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
        titleView.setTitle("订单详情");
        String orderId = intent.getStringExtra(EXTRA_ORDER_ID);
        OrderDetailFragment orderDetailFragment = OrderDetailFragment.newInstance(orderId);
        FragmentUtil.openFragment(R.id.order_fragment, getSupportFragmentManager(), null, orderDetailFragment, OrderDetailFragment.class.getName());
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
        return "订单详情";
    }
}
