package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
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
    private static String EXTRA_ORDER_INFO = "order_info";
    private static String EXTRA_UI_TYPE    = "ui type";
    private static int UI_TYPE_CORFIRM     = 0;   //确认订单
    private static int UI_TYPE_SHOW_ORDER  = 1;   //查看订单详情

    @Bind(R.id.title_view)
    protected TitleBarView titleView;

    /**
     * 确认订单
     * @param context
     * @param orderId
     * @param order
     */
    public static void open(Context context, String orderId, Order order) {
        if (!TextUtils.isEmpty(orderId)) {
            Intent intent = new Intent(context, OrderInfoActivity.class);
            intent.putExtra(EXTRA_ORDER_ID, orderId);
            intent.putExtra(EXTRA_UI_TYPE, UI_TYPE_SHOW_ORDER);
            intent.putExtra(EXTRA_ORDER_INFO, order);
            context.startActivity(intent);
        }
    }

    /**
     * 查看订单
     * @param context
     * @param orderId
     */
    public static void open(Context context, String orderId) {
        if (!TextUtils.isEmpty(orderId)) {
            Intent intent = new Intent(context, OrderInfoActivity.class);
            intent.putExtra(EXTRA_ORDER_ID, orderId);
            intent.putExtra(EXTRA_UI_TYPE, UI_TYPE_SHOW_ORDER);
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
        int uiType = intent.getIntExtra(EXTRA_UI_TYPE,-1);
        OrderDetailFragment orderDetailFragment = null;
        if (uiType==UI_TYPE_CORFIRM){
            titleView.setTitle("确认订单");
            String orderId = intent.getStringExtra(EXTRA_ORDER_ID);
            Order order = intent.getParcelableExtra(EXTRA_ORDER_INFO);
            orderDetailFragment = OrderDetailFragment.newInstance(orderId,order);
        }else if (uiType==UI_TYPE_SHOW_ORDER){
            titleView.setTitle("订单详情");
            String orderId = intent.getStringExtra(EXTRA_ORDER_ID);
            orderDetailFragment = OrderDetailFragment.newInstance(orderId);
        }
        if (orderDetailFragment!=null){
            FragmentUtil.openFragment(R.id.order_fragment, getSupportFragmentManager(), null, orderDetailFragment, OrderDetailFragment.class.getName());
        }else{
            this.finish();
        }
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
