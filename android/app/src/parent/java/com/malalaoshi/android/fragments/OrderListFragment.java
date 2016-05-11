package com.malalaoshi.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.OrderRecyclerViewAdapter;
import com.malalaoshi.android.api.MoreOrderListApi;
import com.malalaoshi.android.api.OrderListApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.decoration.TeacherItemDecoration;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.net.MoreTeacherListApi;
import com.malalaoshi.android.net.TeacherListApi;
import com.malalaoshi.android.refresh.NormalRefreshViewHolder;
import com.malalaoshi.android.result.OrderListResult;
import com.malalaoshi.android.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;

/**
 * Created by kang on 16/5/5.
 */
public class OrderListFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, RecyclerViewLoadMoreListener.OnLoadMoreListener  {

    @Bind(R.id.fl_order_list)
    protected FrameLayout flOrderList;

    @Bind(R.id.order_list_refresh_layout)
    protected BGARefreshLayout mRefreshLayout;

    @Bind(R.id.order_list_recycler_view)
    protected RecyclerView recyclerView;

    private View emptyView;

    private OrderRecyclerViewAdapter orderRecyclerViewAdapter;
    private List<Order> orderList = new ArrayList<>();

    private String nextUrl = null;

    public static OrderListFragment newInstance() {
        OrderListFragment f = new OrderListFragment();
        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);
        ButterKnife.bind(this, view);
        initViews();
        setEvent();
        initData();
        return view;
    }

    private void initData() {
        mRefreshLayout.beginRefreshing();
    }

    private void initViews() {

        Context context = getContext();
        emptyView = LayoutInflater.from(context).inflate(R.layout.view_load_empty, null);
        orderRecyclerViewAdapter = new OrderRecyclerViewAdapter(orderList);
        GridScrollYLinearLayoutManager layoutManager = new GridScrollYLinearLayoutManager(context, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(orderRecyclerViewAdapter);
        recyclerView.addItemDecoration(new TeacherItemDecoration(context, TeacherItemDecoration.VERTICAL_LIST, getResources().getDimensionPixelSize(R.dimen.teacher_list_top_diver)));
        recyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(layoutManager, this, OrderRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE));
        initReshLayout();
    }


    protected void setEvent() {
        mRefreshLayout.setDelegate(this);
    }

    protected void initReshLayout() {
        mRefreshLayout.setRefreshViewHolder(new NormalRefreshViewHolder(this.getActivity(), false));

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    private void getOrderListSucceedEmpty(OrderListResult orderListResult) {
        flOrderList.removeAllViews();
        flOrderList.addView(emptyView);
        orderList.clear();
        nextUrl = orderListResult.getNext();
        notifyDataSetChanged();
        setRefreshing(false);
        StatReporter.filterEmptyTeacherList();
    }

    private void notifyDataSetChanged() {
        if (nextUrl == null || !nextUrl.isEmpty()) {
            orderRecyclerViewAdapter.setMoreStatus(OrderRecyclerViewAdapter.NODATA_LOADING);
        } else {
            orderRecyclerViewAdapter.setMoreStatus(OrderRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
    }


    private void getOrderListSucceed(OrderListResult orderListResult) {
        flOrderList.removeAllViews();
        flOrderList.addView(mRefreshLayout);
        List<Order> orders = orderListResult.getResults();
        orderList.clear();
        if (orders != null && orders.size() > 0) {
            orderList.addAll(orders);
        }
        nextUrl = orderListResult.getNext();
        notifyDataSetChanged();
        setRefreshing(false);
    }

    private void getOrderListFailed() {
        notifyDataSetChanged();
        setRefreshing(false);
        MiscUtil.toast(R.string.home_get_teachers_fialed);
    }

    public void loadMoreTeachers() {
        if (nextUrl != null && !nextUrl.isEmpty()) {
            ApiExecutor.exec(new FetchMoreOrderListRequest(this, nextUrl));
        }
    }

    private void getMoreTeachersFailed() {
        notifyDataSetChanged();
        MiscUtil.toast(R.string.home_get_teachers_fialed);
    }

    private void getMoreOrdersFinished() {
        setRefreshing(false);
    }

    private void getMoreOrdersSucceed(OrderListResult orderListResult) {
        List<Order> orders = orderListResult.getResults();
        if (orders != null && orders.size() > 0) {
            orderList.addAll(orders);
        }
        nextUrl = orderListResult.getNext();
        notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {
        if (orderRecyclerViewAdapter != null && orderRecyclerViewAdapter.getMoreStatus() != OrderRecyclerViewAdapter.LOADING_MORE && nextUrl != null && !nextUrl.isEmpty()) {
            orderRecyclerViewAdapter.setMoreStatus(OrderRecyclerViewAdapter.LOADING_MORE);
            loadMoreTeachers();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        ApiExecutor.exec(new FetchOrderListRequest(this));
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return true;
    }


    public void setRefreshing(boolean status) {
        mRefreshLayout.endRefreshing();
    }


    @Override
    public String getStatName() {
        return "订单列表页";
    }

    private static final class FetchMoreOrderListRequest extends BaseApiContext<OrderListFragment, OrderListResult> {
        private String nextUrl;

        public FetchMoreOrderListRequest(OrderListFragment orderListFragment, String nextUrl) {
            super(orderListFragment);
            this.nextUrl = nextUrl;
        }

        @Override
        public OrderListResult request() throws Exception {
            return new MoreOrderListApi().getOrderList(nextUrl);
        }

        @Override
        public void onApiSuccess(@NonNull OrderListResult result) {
            if (EmptyUtils.isNotEmpty(result.getResults())) {
                get().getMoreOrdersSucceed(result);
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().getMoreTeachersFailed();
        }

        @Override
        public void onApiFinished() {
            get().getMoreOrdersFinished();
        }
    }

    private static final class FetchOrderListRequest extends BaseApiContext<OrderListFragment, OrderListResult> {

        public FetchOrderListRequest(OrderListFragment orderListFragment) {
            super(orderListFragment);
        }

        @Override
        public OrderListResult request() throws Exception {
            return new OrderListApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull OrderListResult result) {
            if (EmptyUtils.isNotEmpty(result.getResults())) {
                get().getOrderListSucceed(result);
            } else {
                get().getOrderListSucceedEmpty(result);
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            get().getOrderListFailed();
        }
    }
}
