package com.malalaoshi.android.fragments;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.ScheduleAdapter;
import com.malalaoshi.android.api.TimeTableApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.entity.Schedule;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.result.CourseListResult;
import com.malalaoshi.android.util.AuthUtils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleFragment extends BaseFragment implements RecyclerViewLoadMoreListener.OnLoadMoreListener {

    private static final String TAG = "ScheduleFragment";

    @Bind(R.id.rl_empty_schedule)
    protected RelativeLayout rlEmptySchedule;

    @Bind(R.id.tv_empty_tip)
    protected TextView tvEmptyTip;

    @Bind(R.id.tv_sign_up)
    protected TextView tvSignUp;

    @Bind(R.id.rv_schedule_list)
    protected RecyclerView mRecyclerView;

    private ScheduleAdapter mScheduleAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private Schedule scheduleData;

    private int last_index = 0;
    private int lastVisibleItem;

    private String hostNextUrl;
    private String hostPreviousUrl;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, contentView);
        initData();
        initView();
        setEvent();
        return contentView;
    }

    private void initView() {
        if (UserManager.getInstance().isLogin()){
            rlEmptySchedule.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
            tvEmptyTip.setText("暂时还没有课程哦!");
            tvSignUp.setText("去报名");
        }else{
            rlEmptySchedule.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
            tvEmptyTip.setText("您还没有登录哦!");
            tvSignUp.setText("去登录");
        }
    }

    private void initData() {
        
        //初始化list
        scheduleData = new Schedule();
        //初始化adapter
        mScheduleAdapter = new ScheduleAdapter(getActivity(),scheduleData);
        //初始化recyclerview
        //设置布局管理器
        mLinearLayoutManager = new GridScrollYLinearLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mScheduleAdapter);
        //设置Item增加/移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //设置分割线
      /*  mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL_LIST));*/
        mRecyclerView.setHasFixedSize(true);
        loadData();
    }

    private void setEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(mLinearLayoutManager, this, 20));
    }

    private void updateData(CourseListResult courses) {
        List<Course> listCourse = courses.getResults();
        if ((null == listCourse||listCourse.size()<=0)&&scheduleData.size()<=0) {

        }else{
            scheduleData.addAll(listCourse);
        }
        mScheduleAdapter.notifyDataSetChanged();
    }


    @Override
    public void onLoadMore() {
        mScheduleAdapter.setMoreStatus(ScheduleAdapter.LOADING_MORE);
        loadNextData();
    }

    public void loadData() {
        ApiExecutor.exec(new LoadTimeTable(this));
    }

    public void loadPreviousData() {
        ApiExecutor.exec(new LoadTimeTable(this));
    }

    public void loadNextData() {
        ApiExecutor.exec(new LoadTimeTable(this));
    }

    @OnClick(R.id.tv_sign_up)
    public void onClcikEmpty(View view){
        if (!UserManager.getInstance().isLogin()){
            //滑动到教师列表页
        }else {
            AuthUtils.redirectLoginActivity(getContext());
        }
    }


    private static final class LoadTimeTable extends BaseApiContext<ScheduleFragment, CourseListResult> {

        private String strUrl;
        public LoadTimeTable(ScheduleFragment scheduleFragment) {
            super(scheduleFragment);
            strUrl = null;
        }

        public LoadTimeTable(ScheduleFragment scheduleFragment, String url) {
            super(scheduleFragment);
            strUrl = url;
        }

        @Override
        public CourseListResult request() throws Exception {
            if (strUrl==null){
                return new TimeTableApi().get();
            }else{
                return new TimeTableApi().get(strUrl);
            }

        }

        @Override
        public void onApiSuccess(@NonNull CourseListResult response) {
            get().updateData(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            int i=0;
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
        }
    }

    @Override
    public String getStatName() {
        return "课表页";
    }
}
