package com.malalaoshi.android.util;

import android.support.v4.widget.SwipeRefreshLayout;

import com.malalaoshi.android.R;

/**
 * Created by zl on 15/12/16.
 */
public final class RefreshLayoutUtils{
    private RefreshLayoutUtils(){}

    public static void initOnCreate(SwipeRefreshLayout refreshLayout, SwipeRefreshLayout.OnRefreshListener refreshListener){
        refreshLayout.setColorSchemeResources(R.color.color_red_ff4444_ff, R.color.color_green_99cc00_ff, R.color.color_blue_33b5e5, R.color.color_orange_ffbb33_ff);
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
