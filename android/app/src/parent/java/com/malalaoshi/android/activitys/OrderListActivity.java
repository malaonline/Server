package com.malalaoshi.android.activitys;

import android.os.Bundle;
import android.support.annotation.Nullable;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.fragments.OrderListFragment;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/5.
 */
public class OrderListActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    @Bind(R.id.title_view)
    protected TitleBarView titleView;

    //筛选结果列表
    private OrderListFragment orderListFragment;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        ButterKnife.bind(this);
        initViews(savedInstanceState);
        titleView.setOnTitleBarClickListener(this);
    }
    private void initViews(Bundle savedInstanceState) {
        titleView.setTitle("我的订单");
        if (savedInstanceState==null){
            orderListFragment = OrderListFragment.newInstance();
            FragmentUtil.openFragment(R.id.order_fragment, getSupportFragmentManager(), null, orderListFragment, OrderListFragment.class.getName());
        }
    }

    @Override
    protected String getStatName() {
        return "订单列表页";
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }
}
