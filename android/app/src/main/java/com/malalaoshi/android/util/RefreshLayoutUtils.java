package com.malalaoshi.android.util;

import android.support.v4.widget.SwipeRefreshLayout;

import com.malalaoshi.android.R;

/**
 * Created by zl on 15/12/16.
 */
public final class RefreshLayoutUtils{
    private RefreshLayoutUtils(){}

    public static void initOnCreate(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener refreshListener){
        refreshLayout.setColorSchemeResources(R.color.red_light, R.color.green_light, R.color.blue_light, R.color.orange_light);
        refreshLayout.setOnRefreshListener(refreshListener);
    }

    public static void refreshOnCreate(final SwipeRefreshLayout refreshLayout, final SwipeRefreshLayout.OnRefreshListener refreshListener){
        HandlerUtils.postDelayed(new Runnable(){

            @Override
            public void run(){
                refreshLayout.setRefreshing(true);
                refreshListener.onRefresh();
            }

        }, 100);
    }
}
