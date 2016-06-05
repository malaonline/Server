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
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.refresh.NormalRefreshViewHolder;
import com.malalaoshi.android.result.CourseListResult;
import com.malalaoshi.android.util.AuthUtils;
import com.malalaoshi.android.util.MiscUtil;

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
public class ScheduleFragment extends BaseFragment implements RecyclerViewLoadMoreListener.OnLoadMoreListener, BGARefreshLayout.BGARefreshLayoutDelegate, View.OnClickListener {

    private static final String TAG = "ScheduleFragment";

    private View defaultPager;

    @Bind(R.id.refresh_layout)
    protected BGARefreshLayout refreshLayout;

    @Bind(R.id.rv_schedule_list)
    protected RecyclerView mRecyclerView;

    private ScheduleAdapter mScheduleAdapter;

    private LinearLayoutManager mLinearLayoutManager;

    private String hostNextUrl;
    private String hostPreviousUrl;

    private boolean isFirstLoadFinish = false;

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
                updateView();
                break;
        }
    }


    private void initView() {
        defaultPager = LayoutInflater.from(getContext()).inflate(R.layout.view_schedule_empty, null);
        updateView();
    }

    private void updateView(){
        if (!UserManager.getInstance().isLogin()){
            setSignUpPager();
        }else{
            setSchedulePager();
            refreshLayout.beginRefreshing();
        }
    }

    protected void setEvent() {
        mRecyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(mLinearLayoutManager, this, 20));
        defaultPager.findViewById(R.id.tv_default_oper).setOnClickListener(this);
    }

    private void initData() {

        //初始化adapter
        mScheduleAdapter = new ScheduleAdapter(getActivity());
        //初始化recyclerview
        //设置布局管理器
        mLinearLayoutManager = new GridScrollYLinearLayoutManager(getActivity(), 1);
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

    private void setSignUpPager(){
        refreshLayout.removeAllViews();
        refreshLayout.addView(defaultPager);
        ((TextView)refreshLayout.findViewById(R.id.tv_default_tip)).setText("您还没有登录哦!");
        ((TextView)refreshLayout.findViewById(R.id.tv_default_oper)).setText("去登录");
    }

    private void setEmptySchedulePager(){
        refreshLayout.removeAllViews();
        refreshLayout.addView(defaultPager);
        ((TextView)refreshLayout.findViewById(R.id.tv_default_tip)).setText("您还没有课程哦!");
        ((TextView)refreshLayout.findViewById(R.id.tv_default_oper)).setText("去报名");
    }

    private void setSchedulePager(){
        refreshLayout.removeAllViews();
        refreshLayout.addView(mRecyclerView);
    }



    @Override
    public void onLoadMore() {
        if (mScheduleAdapter.getMoreStatus() != TeacherRecyclerViewAdapter.LOADING_MORE && hasNextData()) {
            mScheduleAdapter.setMoreStatus(ScheduleAdapter.LOADING_MORE);
            loadNextData();
        }
    }

    private boolean hasNextData(){
        return !EmptyUtils.isEmpty(hostNextUrl);
    }

    private boolean hasPreviousData(){
        return  !EmptyUtils.isEmpty(hostPreviousUrl);
    }

    public void loadData() {
        ApiExecutor.exec(new LoadTimeTable(this));
    }

    public void loadNextData() {
        if (!hasNextData()) {
            return;
        }
        ApiExecutor.exec(new LoadTimeTable(this,hostNextUrl,LoadTimeTable.TYPE_LOAD_NEXT));
    }

    public void loadPreviousData() {
        if (!hasPreviousData()) {
            refreshLayout.endRefreshing();
            return;
        }
        ApiExecutor.exec(new LoadTimeTable(this,hostPreviousUrl,LoadTimeTable.TYPE_LOAD_PREVIOUS));
    }

    private void updateData(CourseListResult courses) {
        List<Course> listCourse = courses.getResults();
        if (null != listCourse) {
            if (listCourse.size()>0){
                setSchedulePager();
                isFirstLoadFinish = true;
                hostNextUrl = courses.getNext();
                hostPreviousUrl = courses.getPrevious();
                mScheduleAdapter.addItem(listCourse);
                resetPosition();
            }else{
                setEmptySchedulePager();
            }
        }
    }

    private void resetPosition() {
        int index = mScheduleAdapter.getStartIndex();
        mLinearLayoutManager.scrollToPosition(index);
    }

    private void updateNextData(CourseListResult courses){
        List<Course> listCourse = courses.getResults();
        if ((null != listCourse)) {
            hostNextUrl = courses.getNext();
            mScheduleAdapter.addItem(listCourse);
        }
    }

    private void updatePreviousData(CourseListResult courses){
        List<Course> listCourse = courses.getResults();
        if ((null != listCourse)) {
            hostPreviousUrl = courses.getPrevious();
            mScheduleAdapter.addMoreItem(listCourse);
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
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout refreshLayout) {
        return false;
    }

    @OnClick(R.id.btn_goback)
    public void onClickGoBack(View view){
       resetPosition();
    }

    @Override
    public void onClick(View v) {
        if (UserManager.getInstance().isLogin()){
            //滑动到教师列表页
        }else {
            AuthUtils.redirectLoginActivity(getContext());
        }
    }

    private static final class LoadTimeTable extends BaseApiContext<ScheduleFragment, CourseListResult> {
        private int loadType = 0;
        private static final int TYPE_LOAD = 0;
        private static final int TYPE_LOAD_NEXT = 1;
        private static final int TYPE_LOAD_PREVIOUS = 2;
        private String strUrl;
        public LoadTimeTable(ScheduleFragment scheduleFragment) {
            super(scheduleFragment);
            loadType = TYPE_LOAD;
            strUrl = null;
        }

        public LoadTimeTable(ScheduleFragment scheduleFragment, String url, int type) {
            super(scheduleFragment);
            loadType = type;
            strUrl = url;
        }

        @Override
        public CourseListResult request() throws Exception {
            if (loadType!=TYPE_LOAD){
                return new TimeTableApi().get(strUrl);
            }else{
                return new TimeTableApi().get();
            }
        }

        @Override
        public void onApiSuccess(@NonNull CourseListResult response) {
            switch (loadType){
                case TYPE_LOAD:
                    get().updateData(response);
                    break;
                case TYPE_LOAD_NEXT:
                    get().updateNextData(response);
                    break;
                case TYPE_LOAD_PREVIOUS:
                    get().updatePreviousData(response);
                    break;
            }
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
            switch (loadType){
                case TYPE_LOAD:
                    break;
                case TYPE_LOAD_NEXT:
                    break;
                case TYPE_LOAD_PREVIOUS:
                    break;
            }
        }

        @Override
        public void onApiFinished() {
            super.onApiFinished();
            switch (loadType){
                case TYPE_LOAD:
                    get().updateRefeshStatus();
                    break;
                case TYPE_LOAD_NEXT:
                    get().updateNextRefeshStatus();
                    break;
                case TYPE_LOAD_PREVIOUS:
                    get().updatePreviousRefeshStatus();
                    break;
            }
        }
    }


    @Override
    public String getStatName() {
        return "课表页";
    }
}
