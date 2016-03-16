package com.malalaoshi.android.fragments;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.decoration.TeacherItemDecoration;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.result.TeacherListResult;
import com.malalaoshi.android.util.JsonUtil;


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

    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers";

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

    public void loadDatas(){
        mRefreshLayout.beginRefreshing();
    }

    @Override
    public void onLoadMore(){
        if(teacherListAdapter != null&&teacherListAdapter.getMoreStatus()!=TeacherRecyclerViewAdapter.LOADING_MORE && nextUrl!=null&& !nextUrl.isEmpty() ){
            teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.LOADING_MORE);
            new LoadTeachersTask(){
                @Override
                public void afterTask(TeacherListResult response){
                    if(response != null){
                        try{
                            nextUrl = response.getNext();
                        }catch(Exception e){
                            nextUrl = null;
                        }
                    }
                    if(nextUrl == null){
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout){
        //刷新
        teachersList.clear();
        nextUrl = MalaApplication.getInstance().getMalaHost()+TEACHERS_PATH_V1;
        boolean hasParam = false;
        if(gradeId != null && gradeId > 0){
            nextUrl += "?grade=" + gradeId;
            hasParam = true;
        }
        if(subjectId != null && subjectId > 0){
            nextUrl += hasParam ? "&subject=" : "?subject=";
            nextUrl += subjectId;
            hasParam = true;
        }
        if(tagIds != null && tagIds.length > 0){
            nextUrl += hasParam ? "&tags=" : "?tags=";
            for(int i=0; i<tagIds.length;){
                nextUrl += tagIds[i];
                if(++i < tagIds.length){
                    nextUrl += "+";
                }
            }
        }
        new LoadTeachersTask(){
            @Override
            public void afterTask(TeacherListResult response){
                setRefreshing(false);
            }
        }.execute();
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


    private class LoadTeachersTask extends AsyncTask<String, Integer, String>{
        public void afterTask(TeacherListResult response){
        }
        @Override
        protected String doInBackground(String ...params){
            String url = nextUrl;
            RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
            StringRequest jsArrayRequest = new StringRequest(
                    Request.Method.GET, url,
                    new Response.Listener<String>(){
                        @Override
                        public void onResponse(String response){
                            TeacherListResult teacherResult = null;
                            try{
                                teacherResult = JsonUtil.parseStringData(response, TeacherListResult.class);
                                List<Teacher> teachers = teacherResult.getResults();
                                if(teachers != null && teachers.size() > 0){
                                    teachersList.addAll(teachers);
                                }
                                nextUrl = teacherResult.getNext();
                                return;
                            }catch (Exception e){
                                Log.e(LoginFragment.class.getName(), e.getMessage(), e);
                            }finally{
                                afterTask(teacherResult);
                                if (nextUrl==null||!nextUrl.isEmpty()){
                                    teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
                                }else{
                                    teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
                                }
                            }

                        }
                    },
                    new Response.ErrorListener(){
                        @Override
                        public void onErrorResponse(VolleyError error){
                            afterTask(null);
                            if (nextUrl==null||!nextUrl.isEmpty()){
                                teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.NODATA_LOADING);
                            }else{
                                teacherListAdapter.setMoreStatus(TeacherRecyclerViewAdapter.PULLUP_LOAD_MORE);
                            }
                            Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                        }
                    });
            requestQueue.add(jsArrayRequest);
            return "ok";
        }
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