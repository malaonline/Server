package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.chanven.lib.cptr.PtrDefaultHandler;
import com.chanven.lib.cptr.PtrFrameLayout;
import com.chanven.lib.cptr.PtrHandler;
import com.chanven.lib.cptr.loadmore.OnLoadMoreListener;
import com.chanven.lib.cptr.recyclerview.RecyclerAdapterWithHF;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.ScheduleAdapter;
import com.malalaoshi.android.api.TimeTableApi;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.core.network.api.ApiExecutor;
import com.malalaoshi.android.core.network.api.BaseApiContext;
import com.malalaoshi.android.core.usercenter.UserManager;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.view.RefreshHeaderView;
import com.malalaoshi.android.entity.Course;
import com.malalaoshi.android.entity.ScheduleCourse;
import com.malalaoshi.android.entity.ScheduleDate;
import com.malalaoshi.android.entity.ScheduleItem;
import com.malalaoshi.android.result.CourseListResult;
import com.malalaoshi.android.util.AuthUtils;
import com.malalaoshi.android.util.CalendarUtils;
import com.malalaoshi.android.util.MiscUtil;
import com.malalaoshi.android.view.DefaultView;
import com.malalaoshi.android.view.ListDefaultView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/6/29.
 */
public class ScheduleFragment extends BaseFragment {

    public enum LayoutType {
        REFRESH_FAILED,
        LIST,
        EMPTY,
        UNSIGNUP
    }

    //刷新容器
    private PtrFrameLayout refreshLayout;

    private RecyclerView recyclerView;

    private BaseRecycleAdapter adapter;

    private LinearLayoutManager layoutManager;

    private DefaultView emptyView;
    private ListDefaultView errorView;
    private DefaultView unsignupView;
    protected Button btnGoback;

    private String hostNextUrl;
    private String hostPreviousUrl;
    private OnClickEmptyCourse onClickEmptyCourse;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_schedule, container, false);
        initView(contentView);
        initData();
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
                adapter.clear();
                loadData();
                break;
            case BusEvent.BUS_EVENT_LOGIN_SUCCESS:
            case BusEvent.BUS_EVENT_RELOAD_TIMETABLE_DATA:
            case BusEvent.BUS_EVENT_PAY_SUCCESS:
                adapter.clear();
                loadData();
                break;
        }
    }

    private void setEvent() {

        recyclerView.addOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int firstvisiable = layoutManager.findFirstVisibleItemPosition();
                int lastvisiable = layoutManager.findLastVisibleItemPosition();
                int index = ((ScheduleAdapter)adapter).getFirstUnpassMonth();
                if (adapter.getItemCount()>0&&(firstvisiable>index||lastvisiable<index)){
                    btnGoback.setVisibility(View.VISIBLE);
                }else{
                    btnGoback.setVisibility(View.GONE);
                }
            }
        });

        btnGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPosition();
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
                refreshList();
            }
        });

        unsignupView.setOnBtnClickListener(new DefaultView.OnBtnClickListener() {
            @Override
            public void OnBtnClickListener(View view) {
                AuthUtils.redirectLoginActivity(getContext());
            }
        });
    }

    private void refreshList() {
        autoRefresh();
    }

    private void resetPosition() {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                int index = ((ScheduleAdapter)adapter).getFirstUnpassMonth();
                recyclerView.smoothScrollToPosition(index+1);
            }
        });
    }

    private void initData() {
        errorView.setText("加载失败了,点击刷新");

        emptyView.setText("您还没有课程哦!");
        emptyView.setButtonText("去报名");

        unsignupView.setText("您还没有登录哦!");
        unsignupView.setButtonText("去登录");
        loadData();
    }

    private void loadData() {
        if (!UserManager.getInstance().isLogin()){
            setLayout(LayoutType.UNSIGNUP);
        }else{
            //刷新
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    autoRefresh();
                }
            });
        }
    }

    public void setLayout(LayoutType type) {
        switch (type) {
            case EMPTY:
                emptyView.setVisibility(View.VISIBLE);
                errorView.setVisibility(View.GONE);
                unsignupView.setVisibility(View.GONE);
                btnGoback.setVisibility(View.GONE);
                break;
            case REFRESH_FAILED:
                emptyView.setVisibility(View.GONE);
                errorView.setVisibility(View.VISIBLE);
                unsignupView.setVisibility(View.GONE);
                btnGoback.setVisibility(View.GONE);
                break;
            case LIST:
                emptyView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                unsignupView.setVisibility(View.GONE);
                btnGoback.setVisibility(View.GONE);
                break;
            case UNSIGNUP:
                emptyView.setVisibility(View.GONE);
                errorView.setVisibility(View.GONE);
                unsignupView.setVisibility(View.VISIBLE);
                btnGoback.setVisibility(View.GONE);
                break;
        }
    }

    private void initView(View view) {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        emptyView = (DefaultView) view.findViewById(R.id.view_empty);
        errorView = (ListDefaultView) view.findViewById(R.id.view_error);
        unsignupView = (DefaultView) view.findViewById(R.id.view_unsigin_up);
        btnGoback = (Button) view.findViewById(R.id.btn_goback);
        initRefresh(view);
        layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new ScheduleAdapter(getContext());
        recyclerView.setAdapter(new RecyclerAdapterWithHF(adapter));
    }

    private void initRefresh(View view) {
        refreshLayout = (PtrFrameLayout) view.findViewById(R.id.refresh);
        //Header
        RefreshHeaderView headerView = new RefreshHeaderView(getContext());
        refreshLayout.setHeaderView(headerView);
        refreshLayout.addPtrUIHandler(headerView);
        refreshLayout.setKeepHeaderWhenRefresh(true);
        refreshLayout.setPullToRefresh(false);
        //这个会引起自动刷新刷新两次
        //refreshLayout.setEnabledNextPtrAtOnce(true);
        refreshLayout.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                return PtrDefaultHandler.checkContentCanBePulledDown(frame, content, header);
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                ScheduleFragment.this.onRefreshBegin();
            }
        });
        //Footer
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void loadMore() {
                onLoadMoreBegin();
            }
        });
    }

    /**
     * 刷新开始
     */
    protected void onRefreshBegin() {
        if (!UserManager.getInstance().isLogin()){
            setLayout(LayoutType.UNSIGNUP);
            refreshLayout.refreshComplete();
            return;
        }
        setLayout(LayoutType.LIST);
        if (adapter.getItemCount()>0){
            if (EmptyUtils.isEmpty(hostPreviousUrl)){
                refreshLayout.refreshComplete();
                MiscUtil.toast("没有数据了!");
            }else{
                ApiExecutor.exec(new PullTimeTable(this,hostPreviousUrl));
            }
        }else{
            ApiExecutor.exec(new LoadTimeTable(this));
        }
    }

    /**
     * 加载更多开始
     */
    protected void onLoadMoreBegin() {
        ApiExecutor.exec(new LoadMoreTimeTable(this,hostNextUrl));
    }

    /**
     * 自动刷新
     */
    protected void autoRefresh() {
        if (UserManager.getInstance().isLogin()){
            refreshLayout.autoRefresh();
        }
    }

    /**
     * 刷新完成
     */
    protected void refreshFinish(CourseListResult response) {
        refreshLayout.refreshComplete();
        if (response == null) {
            if (adapter.getItemCount()<=0){
                setLayout(LayoutType.REFRESH_FAILED);
            }
            return;
        }
        if (EmptyUtils.isEmpty(response.getResults())) {
            adapter.clear();
            setLayout(LayoutType.EMPTY);
        } else {
            setLayout(LayoutType.LIST);
            adapter.clear();
            addItems(adapter,response);
            int index = ((ScheduleAdapter)adapter).getFirstUnpassMonth();
            layoutManager.scrollToPositionWithOffset(index,0);
        }

        if (EmptyUtils.isEmpty(response.getNext())) {
            refreshLayout.setLoadMoreEnable(false);
        } else {
            refreshLayout.setLoadMoreEnable(true);
        }

        if (response != null) {
            hostNextUrl = response.getNext();
            hostPreviousUrl = response.getPrevious();
        }
    }

    /**
     * 上拉加载更多完成
     */
    protected void loadMoreFinish(CourseListResult response) {
        if (response == null) {
            refreshLayout.loadMoreComplete(false);
            return;
        }
        insertItems(adapter,response);
        if (EmptyUtils.isEmpty(response.getNext())) {
            refreshLayout.loadMoreComplete(false);
        } else {
            refreshLayout.loadMoreComplete(true);
        }
    }

    /**
     * 下拉加载更多
     */
    protected void pullMoreFinish(CourseListResult response) {
        refreshLayout.refreshComplete();
        if (response == null) {
            //没有数据了
            return;
        }
        addItems(adapter,response);
        if (response != null) {
            hostPreviousUrl = response.getPrevious();
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
            get().pullMoreFinish(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
            get().pullMoreFinish(null);
        }
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
            get().refreshFinish(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
            get().refreshFinish(null);
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
            get().loadMoreFinish(response);
        }

        @Override
        public void onApiFailure(Exception exception) {
            MiscUtil.toast("加载失败,请检查网络连接!");
            get().loadMoreFinish(null);
        }

    }

    public interface OnClickEmptyCourse{
        public void onClickEmptyCourse(View v);
    }

    public void setOnClickEmptyCourse(OnClickEmptyCourse onClickEmptyCourse) {
        this.onClickEmptyCourse = onClickEmptyCourse;
    }


    protected void insertItems(BaseRecycleAdapter adapter, CourseListResult response) {
        if (response!=null&&response.getResults()!=null){
            List<ScheduleItem> items = convertItems(response.getResults());
            if  (items!=null){
                adapter.insertData(items);
            }
        }
    }

    protected void addItems(BaseRecycleAdapter adapter, CourseListResult response) {
        if (response!=null&&response.getResults()!=null){
            List<ScheduleItem> items = convertItems(response.getResults());
            if  (items!=null){
                adapter.addData(items);
            }
        }
    }

    private List<ScheduleItem> convertItems(List<Course> newDatas) {
        Collections.sort(newDatas);
        List<ScheduleItem> tempDatas = new ArrayList<>();
        if (newDatas!=null&&newDatas.size()>0){
            Calendar currentCalendar = null;
            for (int i = 0; i < newDatas.size(); i++) {
                Course course = newDatas.get(i);
                if (currentCalendar==null){
                    currentCalendar =  CalendarUtils.timestampToCalendar(course.getStart());
                    tempDatas.add(new ScheduleDate(course.getStart()));
                }else{
                    Calendar start =  CalendarUtils.timestampToCalendar(course.getStart());
                    if (currentCalendar.get(Calendar.YEAR)!=start.get(Calendar.YEAR)||currentCalendar.get(Calendar.MONTH)!=start.get(Calendar.MONTH)){
                        currentCalendar = start;
                        tempDatas.add(new ScheduleDate(course.getStart()));

                    }
                }
                tempDatas.add(new ScheduleCourse(course));
            }
        }
        return tempDatas;
    }

    @Override
    public String getStatName() {
        return "课表页";
    }


}
