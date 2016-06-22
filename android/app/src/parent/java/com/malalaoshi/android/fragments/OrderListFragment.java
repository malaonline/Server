package com.malalaoshi.android.fragments;

import com.malalaoshi.android.adapter.OrderAdapter;
import com.malalaoshi.android.api.MoreOrderListApi;
import com.malalaoshi.android.api.OrderListApi;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.result.OrderListResult;


/**
 * Created by kang on 16/5/5.
 */
public class OrderListFragment extends BaseRefreshFragment<OrderListResult>{
    private String nextUrl;

    private OrderAdapter adapter;

    public static OrderListFragment newInstance() {
        OrderListFragment f = new OrderListFragment();
        return f;
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new OrderAdapter(getContext());
        return adapter;
    }

    @Override
    protected OrderListResult refreshRequest() throws Exception {
        return new OrderListApi().get();
    }

    @Override
    protected void refreshFinish(OrderListResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void loadMoreFinish(OrderListResult response) {
        super.loadMoreFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected OrderListResult loadMoreRequest() throws Exception {
        return new MoreOrderListApi().getOrderList(nextUrl);
    }

    @Override
    protected void afterCreateView() {

    }

    @Override
    public String getStatName() {
        return "订单列表页";
    }


}
