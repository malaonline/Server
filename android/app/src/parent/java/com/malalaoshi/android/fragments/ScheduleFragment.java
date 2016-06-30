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
import android.widget.Button;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.ScheduleAdapter;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.api.TimeTableApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.view.ErrorView;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.refresh.NormalRefreshViewHolder;
import com.malalaoshi.android.result.CourseListResult;
import com.malalaoshi.android.util.AuthUtils;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.util.ScrollSpeedLinearLayoutManger;
import com.malalaoshi.android.view.DefaultView;
import com.malalaoshi.android.view.ListDefaultView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;
import de.greenrobot.event.EventBus;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleFragment extends BaseFragment implements RecyclerViewLoadMoreListener.OnLoadMoreListener, BGARefreshLayout.BGARefreshLayoutDelegate {

    private static final String TAG = "ScheduleFragment";

    @Bind(R.id.view_unsigin_up)
    protected DefaultView unSiginupView;

    @Bind(R.id.view_empty)
    protected DefaultView emptyView;

    @Bind(R.id.view_error)
    protected ListDefaultView errorView;

    @Bind(R.id.refresh_layout)
    protected BGARefreshLayout refreshLayout;

    @Bind(R.id.rv_schedule_list)
    protected RecyclerView mRecyclerView;

    @Bind(R.id.btn_goback)
    protected Button btnGoback;

    private ScheduleAdapter mScheduleAdapter;

    private ScrollSpeedLinearLayoutManger mLinearLayoutManager;

    private String hostNextUrl;
    private String hostPreviousUrl;

    private boolean isFirstLoadFinish = false;
    private OnClickEmptyCourse onClickEmptyCourse;


    public interface OnClickEmptyCourse{
        public void onClickEmptyCourse(View v);
    }

    public void setOnClickEmptyCourse(OnClickEmptyCourse onClickEmptyCourse) {
        this.onClickEmptyCourse = onClickEmptyCourse;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        ButterKnife.bind(this, contentView);
        initData();
        initView();
        setEvent();
        EventBus.getDefault().register(this);
        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_LOGOUT_SUCCESS:
                mScheduleAdapter.clear();
                updateView();
                break;
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
            case BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA:
            case BusEvent.BUS_EVENT_PAY_SUCCESS:
                isFirstLoadFinish = false;
                updateView();
                break;
        }
    }


    private void initView() {
        unSiginupView.setText("您还没有登录哦!");
        unSiginupView.setButtonText("去登陆");

        emptyView.setText("您还没有课程哦!");
        emptyView.setButtonText("去报名");

        updateView();
    }

    private void updateView(){
        if (!UserManager.getInstance().isLogin()){
            setNoSignUpView();
        }else{
            setListView();
            refreshLayout.beginRefreshing();
        }
    }

    protected void setEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(mLinearLayoutManager, this, 20));
        unSiginupView.setOnBtnClickListener(new DefaultView.OnBtnClickListener() {
            @Override
            public void OnBtnClickListener(View view) {
                AuthUtils.redirectLoginActivity(getContext());
            }
        });
        emptyView.setOnBtnClickListener(new DefaultView.OnBtnClickListener() {
            @Override
            public void OnBtnClickListener(View view) {
                if (onClickEmptyCourse!=null){
                    onClickEmptyCourse.onClickEmptyCourse(view);
                }
            }
        });
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mRecyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        updateView();
                    }
                });
            }
        });
    }

    private void initData() {
        //初始化adapter
        mScheduleAdapter = new ScheduleAdapter(getActivity());
        //初始化recyclerview
        //设置布局管理器
        mLinearLayoutManager = new ScrollSpeedLinearLayoutManger(getActivity(), 1);
        mLinearLayoutManager.setSpeedSlow();
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        // 设置adapter
        mRecyclerView.setAdapter(mScheduleAdapter);
        //设置Item增加/移除的动画
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(mLinearLayoutManager, this, TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE));
        initReshLayout();
    }

    protected void initReshLayout() {
        refreshLayout.setRefreshViewHolder(new NormalRefreshViewHolder(this.getActivity(), false));
        refreshLayout.setDelegate(this);
    }

    private void setNoSignUpView(){
        unSiginupView.setVisibility(View.VISIBLE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    private void setEmptyView(){
        unSiginupView.setVisibility(View.GONE);
        emptyView.setVisibility(View.VISIBLE);
        errorView.setVisibility(View.GONE);
    }

    private void setErrorView(){
        unSiginupView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.VISIBLE);
    }

    private void setListView(){
        unSiginupView.setVisibility(View.GONE);
        emptyView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        int firstvisiable = mLinearLayoutManager.findFirstVisibleItemPosition();
        int lastvisiable = mLinearLayoutManager.findLastVisibleItemPosition();
        int index = mScheduleAdapter.getStartIndex();
        if (firstvisiable>index||lastvisiable<index){
            btnGoback.setVisibility(View.VISIBLE);
        }else{
            btnGoback.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.btn_goback)
    public void onClickGoBack(View view){
        mRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                int index = mScheduleAdapter.getStartIndex();
                //mLinearLayoutManager.scrollToPosition(index);
                // mLinearLayoutManager.scrollToPositionWithOffset(index,0);
                // mLinearLayoutManager.scrollToPositionWithOffset(index,0);
                mRecyclerView.smoothScrollToPosition(index+1);
            }
        });
        //btnGoback.setVisibility(View.GONE);
    }

    private boolean hasNextData(){
        return !EmptyUtils.isEmpty(hostNextUrl);
    }

    private boolean hasPreviousData(){
        return  !EmptyUtils.isEmpty(hostPreviousUrl);
    }

    public void loadData() {
        setListView();
        ApiExecutor.exec(new LoadTimeTable(this));
    }

    public void loadNextData() {
        if (!hasNextData()) {
            return;
        }
        ApiExecutor.exec(new LoadMoreTimeTable(this,hostNextUrl));
    }

    public void loadPreviousData() {
        if (!hasPreviousData()) {
            refreshLayout.endRefreshing();
            MiscUtil.toast("没有数据了!");
            return;
        }
        ApiExecutor.exec(new PullTimeTable(this,hostPreviousUrl));
    }

    private void updateData(CourseListResult courses) {
        if (courses!=null&&courses.getResults()!=null&&courses.getResults().size()>0){
            setListView();
            mScheduleAdapter.clear();
            isFirstLoadFinish = true;
            hostNextUrl = courses.getNext();
            hostPreviousUrl = courses.getPrevious();
            mScheduleAdapter.addItem(courses.getResults());

            int index = mScheduleAdapter.getStartIndex();
            //mLinearLayoutManager.scrollToPosition(index);
            mLinearLayoutManager.scrollToPositionWithOffset(index,0);
            //resetPosition();
        }else{
            setEmptyView();
        }

    }

    private void updateNextData(CourseListResult courses){
        if (courses!=null&&courses.getResults()!=null&&courses.getResults().size()>0){
            hostNextUrl = courses.getNext();
            mScheduleAdapter.addItem(courses.getResults());
        }
    }

    private void updatePreviousData(CourseListResult courses){
        if (courses!=null&&courses.getResults()!=null&&courses.getResults().size()>0){
            hostPreviousUrl = courses.getPrevious();
            mScheduleAdapter.addMoreItem(courses.getResults());
        }
    }

    private void updateRefeshStatus() {
        refreshLayout.endRefreshing();
        if (!hasNextData()) {
            mScheduleAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        } else {
            mScheduleAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
    }

    private void updatePreviousRefeshStatus() {
        refreshLayout.endRefreshing();
    }

    private void updateNextRefeshStatus() {
        if (!hasNextData()) {
            mScheduleAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        } else {
            mScheduleAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout refreshLayout) {
        if (!isFirstLoadFinish){
            loadData();
        }else{
            loadPreviousData();
        }
    }

    @Override
    public void onLoadMore() {
        if (mScheduleAdapter.getMoreStatus() != TeacherRecyclerViewAdapter.LOADING_MORE && hasNextData()) {
            mScheduleAdapter.setMoreStatus(ScheduleAdapter.LOADING_MORE);
            loadNextData();
        }
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    private static final class LoadTimeTable extends BaseApiContext<ScheduleFragment, CourseListResult> {

        public LoadTimeTable(ScheduleFragment scheduleFragment) {
            super(scheduleFragment);
        }

        @Override
        public CourseListResult request() throws Exception {
            return new TimeTableApi().get();
        }

        @Override
        public void onApiSuccess(@NonNull CourseListResult response) {
            get().updateData(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
            get().setErrorView();
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().updateRefeshStatus();
        }
    }

    private static final class LoadMoreTimeTable extends BaseApiContext<ScheduleFragment, CourseListResult> {
        private String strUrl;

        public LoadMoreTimeTable(ScheduleFragment scheduleFragment, String url) {
            super(scheduleFragment);
            strUrl = url;
        }

        @Override
        public CourseListResult request() throws Exception {
            return new TimeTableApi().get(strUrl);
        }

        @Override
        public void onApiSuccess(@NonNull CourseListResult response) {
            get().updateNextData(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().updateNextRefeshStatus();
        }
    }


    private static final class PullTimeTable extends BaseApiContext<ScheduleFragment, CourseListResult> {
        private String strUrl;

        public PullTimeTable(ScheduleFragment scheduleFragment, String url) {
            super(scheduleFragment);
            strUrl = url;
        }

        @Override
        public CourseListResult request() throws Exception {
            return new TimeTableApi().get(strUrl);
        }

        @Override
        public void onApiSuccess(@NonNull CourseListResult response) {
            get().updatePreviousData(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            get().updatePreviousRefeshStatus();
        }
    }


    @Override
    public String getStatName() {
        return "课表页";
    }
}
