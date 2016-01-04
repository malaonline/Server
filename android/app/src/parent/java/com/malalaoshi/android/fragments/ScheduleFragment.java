package com.malalaoshi.android.fragments;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.ScheduleAdapter;
import com.malalaoshi.android.decoration.DividerItemDecoration;
import com.malalaoshi.android.entity.Schedule;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.result.ScheduleListResult;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.RefreshLayoutUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.BGARefreshViewHolder;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate , RecyclerViewLoadMoreListener.OnLoadMoreListener {

    private static final String TAG = "ScheduleFragment";

    private static final String SCHEDULE_PATH_V1 = "";

    private BGARefreshLayout mRefreshLayout;

    private RecyclerView mRecyclerView;

    private ScheduleAdapter mScheduleAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private  List<Schedule> mSchedules;

    private int last_index = 0;
    private int lastVisibleItem;

    private RequestQueue requestQueue;
    private String hostUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        mRefreshLayout = (BGARefreshLayout) contentView.findViewById(R.id.schedule_list_refresh_layout);
        mRecyclerView = (RecyclerView) contentView.findViewById(R.id.rv_schedule_list);

        initData();
        setEvent();
        mRefreshLayout.beginRefreshing();
        return contentView;
    }

    private void initData() {
        requestQueue = MalaApplication.getHttpRequestQueue();
        hostUrl =  MalaApplication.getInstance().getMalaHost();

        BGAMoocStyleRefreshViewHolder moocStyleRefreshViewHolder = new BGAMoocStyleRefreshViewHolder(this.getActivity(), false);
        moocStyleRefreshViewHolder.setOriginalImage(R.mipmap.bga_refresh_moooc);
        moocStyleRefreshViewHolder.setUltimateColor(R.color.colorPrimary);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);

        //初始化list
        mSchedules = new ArrayList<>();
        //初始化adapter
        mScheduleAdapter = new ScheduleAdapter(getActivity(),mSchedules);
        //初始化recyclerview
        //设置布局管理器
        mLinearLayoutManager = new GridScrollYLinearLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mScheduleAdapter);
        //设置Item增加/移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));
        //处理在5.0以下版本中各个Item 间距过大的问题(解决方式:将要设置的间距减去各个Item的阴影宽度)
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            dealCardElevation(mRecyclerView);
        }
        mRecyclerView.setHasFixedSize(true);

    }

    private void dealCardElevation(RecyclerView mRecyclerView) {
        //获取阴影的宽度
        int cardElevation = getResources().getDimensionPixelSize(R.dimen.schedule_list_card_elevation);
        //将父窗口左右的padding减去Item阴影的宽度
        int leftPadding = mRecyclerView.getPaddingLeft();
        int rightPadding = mRecyclerView.getPaddingLeft();
        int bottomPadding = mRecyclerView.getPaddingLeft();
        int topPadding = mRecyclerView.getPaddingTop();
        leftPadding -= cardElevation;
        rightPadding -= cardElevation;
        mRecyclerView.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);
    }


    private void setEvent() {
        mRefreshLayout.setDelegate(this);
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(mLinearLayoutManager, this, 20));
    }

    public void loadData(){
        //清除数据
        mSchedules.clear();
        last_index = 0;

        //发送网络请求
        String url = hostUrl + SCHEDULE_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //ScheduleListResult result = JsonUtil.parseData(R.raw.schedule, ScheduleListResult.class, getActivity());
                ScheduleListResult result = JsonUtil.parseStringData(response, ScheduleListResult.class);

                dealData(result);
                mRefreshLayout.endRefreshing();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                //停止进度条
                mRefreshLayout.endRefreshing();
            }
        });

        jstringRequest.setTag(SCHEDULE_PATH_V1);
        requestQueue.add(jstringRequest);
    }

    public void loadMore(){

        String url = hostUrl + SCHEDULE_PATH_V1;
        StringRequest jstringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //ScheduleListResult result = JsonUtil.parseData(R.raw.schedule, ScheduleListResult.class, getActivity());
                ScheduleListResult result = JsonUtil.parseStringData(response, ScheduleListResult.class);
                dealMoreData(result);
                mRefreshLayout.endRefreshing();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dealRequestError(error.getMessage());
                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                //停止进度条
                mScheduleAdapter.setMoreStatus(ScheduleAdapter.PULLUP_LOAD_MORE);
                mRefreshLayout.endRefreshing();
            }
        });

        jstringRequest.setTag(SCHEDULE_PATH_V1);
        requestQueue.add(jstringRequest);
    }

    private void dealData(ScheduleListResult result) {
        if(result!=null){
            List<Schedule> list  = result.getResults();
            if (result.getCount()>0&&list!=null&&list.size()>=0){
                mScheduleAdapter.addMoreItem(list);
            }else{
                Toast.makeText(getActivity(),"没有更多数据了!",Toast.LENGTH_SHORT).show();
            }
        }else{
            dealRequestError("");
        }
    }

    private void dealRequestError(String message) {
        Toast.makeText(getActivity(),"网络请求失败,请稍后重试!",Toast.LENGTH_SHORT).show();
    }

    //处理请求的数据
    void dealMoreData(ScheduleListResult result) {
        if (result!=null){
            List<Schedule> list  = result.getResults();
            if (result.getCount()>0&&list!=null&&list.size()>=0){
                mScheduleAdapter.addMoreItem(list);
                mScheduleAdapter.setMoreStatus(ScheduleAdapter.PULLUP_LOAD_MORE);
            }else{
                mScheduleAdapter.setMoreStatus(ScheduleAdapter.NODATA_LOADING);
                Toast.makeText(getActivity(),"没有更多数据了!",Toast.LENGTH_SHORT).show();
            }
        }else{
            mScheduleAdapter.setMoreStatus(ScheduleAdapter.PULLUP_LOAD_MORE);
            Toast.makeText(getActivity(),"网络异常,请重试!",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        loadData();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {

        return true;
    }

    @Override
    public void onLoadMore() {
        mScheduleAdapter.setMoreStatus(ScheduleAdapter.LOADING_MORE);
        loadMore();
    }
}
