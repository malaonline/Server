package com.malalaoshi.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.core.base.BaseFragment;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.decoration.TeacherItemDecoration;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.refresh.NormalRefreshViewHolder;
import com.malalaoshi.android.result.TeacherListResult;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.MiscUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;


public class TeacherListFragment extends BaseFragment implements BGARefreshLayout.BGARefreshLayoutDelegate, RecyclerViewLoadMoreListener.OnLoadMoreListener {
    public static String ARGS_FRAGEMENT_PAGE_TYPE = "pagetype";
    public static String ARGS_FRAGEMENT_GRADE_ID = "gradeId";
    public static String ARGS_FRAGEMENT_SUBJECT_ID = "subjectId";
    public static String ARGS_FRAGEMENT_TAG_IDS = "tagIds";

    public static int HOME_PAGE = 0;
    public static int FILTER_PAGE = 1;
    //页面类型
    private int pageType = HOME_PAGE;

    @Bind(R.id.fl_teacher_list)
    protected FrameLayout flTeacherList;

    @Bind(R.id.teacher_list_refresh_layout)
    protected BGARefreshLayout mRefreshLayout;

    @Bind(R.id.teacher_list_recycler_view)
    protected RecyclerView recyclerView;

    @Bind(R.id.teacher_filter_btn)
    protected Button teacherFilterBtn;

    private View FilterEmptyView;

    private TeacherRecyclerViewAdapter teacherListAdapter;
    private List<Teacher> teachersList = new ArrayList<>();

    //筛选条件
    private Long gradeId;
    private Long subjectId;
    private long[] tagIds;

    private String nextUrl = null;

    public static TeacherListFragment newInstance(int pageType) {
        TeacherListFragment f = new TeacherListFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_FRAGEMENT_PAGE_TYPE, pageType);
        f.setArguments(args);
        return f;
    }

    public static TeacherListFragment newInstance(int pageType, Long gradeId, Long subjectId, long[] tagIds) {
        TeacherListFragment f = new TeacherListFragment();
        Bundle args = new Bundle();
        args.putInt(ARGS_FRAGEMENT_PAGE_TYPE, pageType);
        args.putLong(ARGS_FRAGEMENT_GRADE_ID, gradeId);
        args.putLong(ARGS_FRAGEMENT_SUBJECT_ID, subjectId);
        args.putLongArray(ARGS_FRAGEMENT_TAG_IDS, tagIds);
        f.setArguments(args);
        return f;
    }

    public void searchTeachers(Long gradeId, Long subjectId, long[] tagIds) {
        if (pageType == FILTER_PAGE) {
            flTeacherList.removeAllViews();
            flTeacherList.addView(mRefreshLayout);
        }

        this.gradeId = gradeId;
        this.subjectId = subjectId;
        this.tagIds = tagIds;
        mRefreshLayout.beginRefreshing();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        if (getArguments() != null) {
            pageType = getArguments().getInt(ARGS_FRAGEMENT_PAGE_TYPE, 0);
            gradeId = getArguments().getLong(ARGS_FRAGEMENT_GRADE_ID);
            subjectId = getArguments().getLong(ARGS_FRAGEMENT_SUBJECT_ID);
            tagIds = getArguments().getLongArray(ARGS_FRAGEMENT_TAG_IDS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_list, container, false);
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
        if (pageType == HOME_PAGE) {
            teacherFilterBtn.setVisibility(View.VISIBLE);
        } else {
            teacherFilterBtn.setVisibility(View.GONE);
        }

        Context context = getContext();
        FilterEmptyView = LayoutInflater.from(context).inflate(R.layout.view_load_empty, null);
        teacherListAdapter = new TeacherRecyclerViewAdapter(teachersList);
        GridScrollYLinearLayoutManager layoutManager = new GridScrollYLinearLayoutManager(context, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(teacherListAdapter);
        recyclerView.addItemDecoration(new TeacherItemDecoration(context, TeacherItemDecoration.VERTICAL_LIST, getResources().getDimensionPixelSize(R.dimen.teacher_list_top_diver)));
        recyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(layoutManager, this, TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE));
        initReshLayout();
    }


    protected void setEvent() {
        mRefreshLayout.setDelegate(this);
    }

    protected void initReshLayout() {
       /* BGAMoocStyleRefreshViewHolder moocStyleRefreshViewHolder = new BGAMoocStyleRefreshViewHolder(this.getActivity(), false);
        moocStyleRefreshViewHolder.setOriginalImage(R.mipmap.bga_refresh_moooc);
        moocStyleRefreshViewHolder.setUltimateColor(R.color.tab_text_press_color);
        moocStyleRefreshViewHolder.setRefreshViewBackgroundColorRes(R.color.teacher_main_bg);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);*/

         /*BGAMeiTuanRefreshViewHolder meiTuanRefreshViewHolder = new BGAMeiTuanRefreshViewHolder(this.getActivity(), false);
        meiTuanRefreshViewHolder.setPullDownImageResource(R.mipmap.bga_refresh_mt_pull_down);
        meiTuanRefreshViewHolder.setChangeToReleaseRefreshAnimResId(R.anim.bga_refresh_mt_change_to_release_refresh);
        meiTuanRefreshViewHolder.setRefreshingAnimResId(R.anim.bga_refresh_mt_refreshing);
        mRefreshLayout.setRefreshViewHolder(meiTuanRefreshViewHolder);*/
        mRefreshLayout.setRefreshViewHolder(new NormalRefreshViewHolder(this.getActivity(), false));

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void refreshTeachers() {
        mRefreshLayout.beginRefreshing();
    }

    private void loadDatas() {
        NetworkSender.getTeachers(gradeId, subjectId, tagIds, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json == null || json.toString().isEmpty()) {
                    getTeachersFailed();
                    return;
                }
                TeacherListResult teacherResult = null;
                teacherResult = JsonUtil.parseStringData(json.toString(), TeacherListResult.class);
                if (teacherResult != null) {
                    if (teacherResult.getResults() != null && teacherResult.getResults().size() > 0) {
                        getTeachersSucceed(teacherResult);
                    } else {
                        getTeacherSucceedEmpty(teacherResult);
                    }
                    return;
                }
                getTeachersFailed();
            }

            @Override
            public void onFailed(VolleyError error) {
                getTeachersFailed();
            }
        });
    }

    private void getTeacherSucceedEmpty(TeacherListResult teacherResult) {
        if (pageType == FILTER_PAGE) {
            flTeacherList.removeAllViews();
            flTeacherList.addView(FilterEmptyView);
        }
        teachersList.clear();
        nextUrl = teacherResult.getNext();
        notifyDataSetChanged();
        setRefreshing(false);
        StatReporter.filterEmptyTeacherList();
    }

    private void notifyDataSetChanged() {
        if (nextUrl == null || !nextUrl.isEmpty()) {
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        } else {
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
    }


    private void getTeachersSucceed(TeacherListResult teacherResult) {
        if (pageType == FILTER_PAGE) {
            flTeacherList.removeAllViews();
            flTeacherList.addView(mRefreshLayout);
        }
        List<Teacher> teachers = teacherResult.getResults();
        teachersList.clear();
        if (teachers != null && teachers.size() > 0) {
            teachersList.addAll(teachers);
        }
        nextUrl = teacherResult.getNext();
        notifyDataSetChanged();
        setRefreshing(false);
    }

    private void getTeachersFailed() {
        notifyDataSetChanged();
        setRefreshing(false);
        MiscUtil.toast(R.string.home_get_teachers_fialed);
    }

    public void loadMoreTeachers() {
        if (nextUrl != null && !nextUrl.isEmpty()) {
            NetworkSender.getFlipTeachers(nextUrl, new NetworkListener() {
                @Override
                public void onSucceed(Object json) {
                    if (json == null || json.toString().isEmpty()) {
                        getTeachersFailed();
                        return;
                    }
                    TeacherListResult teacherResult = null;
                    teacherResult = JsonUtil.parseStringData(json.toString(), TeacherListResult.class);
                    if (teacherResult != null && teacherResult.getResults() != null && teacherResult.getResults().size() > 0) {
                        getMoreTeachersSucceed(teacherResult);
                        return;
                    }
                    getMoreTeachersSucceed(teacherResult);
                }

                @Override
                public void onFailed(VolleyError error) {
                    getMoreTeachersFailed();
                }
            });
        }
    }

    private void getMoreTeachersFailed() {
        notifyDataSetChanged();
        setRefreshing(false);
        MiscUtil.toast(R.string.home_get_teachers_fialed);
    }

    private void getMoreTeachersSucceed(TeacherListResult teacherResult) {
        List<Teacher> teachers = teacherResult.getResults();
        if (teachers != null && teachers.size() > 0) {
            teachersList.addAll(teachers);
        }
        nextUrl = teacherResult.getNext();
        notifyDataSetChanged();
        setRefreshing(false);
    }

    @Override
    public void onLoadMore() {
        if (teacherListAdapter != null && teacherListAdapter.getMoreStatus() != TeacherRecyclerViewAdapter.LOADING_MORE && nextUrl != null && !nextUrl.isEmpty()) {
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.LOADING_MORE);
            loadMoreTeachers();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout) {
        loadDatas();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout) {
        return true;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */

    public void setRefreshing(boolean status) {
        mRefreshLayout.endRefreshing();
    }

    //筛选
    @OnClick(R.id.teacher_filter_btn)
    public void onClickTeacherFilter() {
        StatReporter.ClickTeacherFilter();
        DialogFragment newFragment = FilterDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), FilterDialogFragment.class.getSimpleName());
    }

    @Override
    public String getStatName() {
        return "老师列表页";
    }
}