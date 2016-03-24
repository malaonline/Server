package com.malalaoshi.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.VolleyError;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.decoration.TeacherItemDecoration;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.net.NetworkListener;
import com.malalaoshi.android.net.NetworkSender;
import com.malalaoshi.android.result.TeacherListResult;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.util.MiscUtil;


import java.util.ArrayList;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;


public class TeacherListFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate, RecyclerViewLoadMoreListener.OnLoadMoreListener{
    private TeacherRecyclerViewAdapter teacherListAdapter;

    @Bind(R.id.teacher_list_refresh_layout)
    protected BGARefreshLayout mRefreshLayout;

    @Bind(R.id.teacher_filter_btn)
    protected Button teacherFilterBtn;
    private int teacherFilterBtnVisiable = View.VISIBLE;

    private  List<Teacher> teachersList = new ArrayList<>();

    //筛选条件
    private Long gradeId;
    private Long subjectId;
    private Long [] tagIds;

    private String nextUrl = null;

    public TeacherListFragment(){
    }

    public TeacherListFragment setTeacherList(List<Teacher> teachers){
        teachersList = teachers;
        return this;
    }

    public TeacherListFragment setSearchCondition(Long gradeId, Long subjectId, Long [] tagIds){
        this.gradeId = gradeId;
        this.subjectId = subjectId;
        this.tagIds = tagIds;
        return this;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View view = inflater.inflate(R.layout.teacher_list, container, false);
        ButterKnife.bind(this, view);
        teacherFilterBtn.setVisibility(teacherFilterBtnVisiable);
        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.teacher_list_recycler_view);
        setEvent();
        Context context = view.getContext();
        teacherListAdapter = new TeacherRecyclerViewAdapter(teachersList);
        GridScrollYLinearLayoutManager layoutManager = new GridScrollYLinearLayoutManager(context, 1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(teacherListAdapter);
        recyclerView.addItemDecoration(new TeacherItemDecoration(context, TeacherItemDecoration.VERTICAL_LIST, getResources().getDimensionPixelSize(R.dimen.teacher_list_top_diver)));
        recyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(layoutManager, this, TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE));
        initReshLayout();
        mRefreshLayout.beginRefreshing();
        return view;
    }


    protected void setEvent(){
        mRefreshLayout.setDelegate(this);
    }

    public void setFiltertBtnVisiable(int visiable){
        if (teacherFilterBtn==null){
            teacherFilterBtnVisiable = visiable;
        }else{
            teacherFilterBtn.setVisibility(visiable);
        }
    }

    protected void initReshLayout() {
        BGAMoocStyleRefreshViewHolder moocStyleRefreshViewHolder = new BGAMoocStyleRefreshViewHolder(this.getActivity(), false);
        moocStyleRefreshViewHolder.setOriginalImage(R.mipmap.bga_refresh_moooc);
        moocStyleRefreshViewHolder.setUltimateColor(R.color.tab_text_press_color);
        moocStyleRefreshViewHolder.setRefreshViewBackgroundColorRes(R.color.teacher_main_bg);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);
    }

    @Override
    public void onDetach(){
        super.onDetach();
    }

    public void refreshTeachers(){
        mRefreshLayout.beginRefreshing();
    }


    private void loadDatas(){
        NetworkSender.getTeachers(gradeId, subjectId, tagIds, new NetworkListener() {
            @Override
            public void onSucceed(Object json) {
                if (json==null||json.toString().isEmpty()) {
                    getTeachersFailed();
                    return;
                }
                TeacherListResult teacherResult = null;
                teacherResult = JsonUtil.parseStringData(json.toString(), TeacherListResult.class);
                if (teacherResult!=null&&teacherResult.getResults()!=null&&teacherResult.getResults().size()>0) {
                    getTeachersSucceed(teacherResult);
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

    private void getTeachersSucceed(TeacherListResult teacherResult) {
        List<Teacher> teachers = teacherResult.getResults();
        if(teachers != null && teachers.size() > 0){
            teachersList.clear();
            teachersList.addAll(teachers);
        }
        nextUrl = teacherResult.getNext();
        if (nextUrl==null||!nextUrl.isEmpty()){
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        }else{
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
        setRefreshing(false);
    }

    private void getTeachersFailed() {
        if (nextUrl==null||!nextUrl.isEmpty()){
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        }else{
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
        setRefreshing(false);
        MiscUtil.toast(R.string.home_get_teachers_fialed);
    }
    
    public void loadMoreTeachers(){
        if(nextUrl!=null&& !nextUrl.isEmpty() ) {
            NetworkSender.getFlipTeachers(nextUrl, new NetworkListener() {
                @Override
                public void onSucceed(Object json) {
                    if (json==null||json.toString().isEmpty()) {
                        getTeachersFailed();
                        return;
                    }
                    TeacherListResult teacherResult = null;
                    teacherResult = JsonUtil.parseStringData(json.toString(), TeacherListResult.class);
                    if (teacherResult!=null&&teacherResult.getResults()!=null&&teacherResult.getResults().size()>0) {
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
        if (nextUrl==null||!nextUrl.isEmpty()){
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        }else{
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
        MiscUtil.toast(R.string.home_get_teachers_fialed);
    }

    private void getMoreTeachersSucceed(TeacherListResult teacherResult) {
        List<Teacher> teachers = teacherResult.getResults();
        if(teachers != null && teachers.size() > 0){
            teachersList.addAll(teachers);
        }
        nextUrl = teacherResult.getNext();
        if (nextUrl==null||!nextUrl.isEmpty()){
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
        }else{
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
        }
    }

    @Override
    public void onLoadMore(){
        if(teacherListAdapter != null&&teacherListAdapter.getMoreStatus()!=TeacherRecyclerViewAdapter.LOADING_MORE && nextUrl!=null&& !nextUrl.isEmpty() ){
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.LOADING_MORE);
            loadMoreTeachers();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout){
        loadDatas();
    }

    @Override
    public boolean onBGARefreshLayoutBeginLoadingMore(BGARefreshLayout bgaRefreshLayout){
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

    public void setRefreshing(boolean status){
        mRefreshLayout.endRefreshing();
    }

    class FooterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{
        private final GridLayoutManager gridLayoutManager;

        public FooterSpanSizeLookup(GridLayoutManager gridLayoutManager){
            this.gridLayoutManager = gridLayoutManager;
        }

        @Override
        public int getSpanSize(int position){
            if(gridLayoutManager.getItemCount() - 1 == position){
                return 2;
            }else{
                return 1;
            }
        }
    }

    //筛选
    @OnClick(R.id.teacher_filter_btn)
    public void onClickTeacherFilter(View view){
        DialogFragment newFragment = FilterDialogFragment.newInstance();
        newFragment.show(getFragmentManager(), FilterDialogFragment.class.getSimpleName());
    }

}