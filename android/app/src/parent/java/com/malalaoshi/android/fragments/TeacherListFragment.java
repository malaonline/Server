package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.decoration.TeacherListGridItemDecoration;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.result.TeacherListResult;
import com.malalaoshi.android.util.JsonUtil;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import cn.bingoogolapple.refreshlayout.BGAMoocStyleRefreshViewHolder;
import cn.bingoogolapple.refreshlayout.BGARefreshLayout;
import cn.bingoogolapple.refreshlayout.widget.GridScrollYLinearLayoutManager;


public class TeacherListFragment extends Fragment implements BGARefreshLayout.BGARefreshLayoutDelegate, RecyclerViewLoadMoreListener.OnLoadMoreListener{
    private OnListFragmentInteractionListener mListener;
    private TeacherRecyclerViewAdapter adapter;

    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers";

    @Bind(R.id.teacher_list_refresh_layout)
    protected BGARefreshLayout mRefreshLayout;

    private  List<Teacher> teachersList;

    private Long gradeId;
    private Long subjectId;
    private Long [] tagIds;

    private String next = null;

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

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.teacher_list_recycler_view);
        ButterKnife.bind(this, view);

        setListener();
        if(recyclerView != null){
            Context context = view.getContext();
            adapter = new TeacherRecyclerViewAdapter(teachersList, mListener);
            GridScrollYLinearLayoutManager layoutManager = new GridScrollYLinearLayoutManager(context, 2);
            layoutManager.setSpanSizeLookup(new FooterSpanSizeLookup(layoutManager));
            recyclerView.setLayoutManager(layoutManager);

            //处理在5.0以下版本中各个Item 间距过大的问题(解决方式:将要设置的间距减去各个Item的阴影宽度)
            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                dealCardElevation(recyclerView);
            }
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.teacher_list_card_diver);

            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new TeacherListGridItemDecoration(context,spacingInPixels));

            recyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(layoutManager, this, TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE));
        }

        processLogic(savedInstanceState);

        mRefreshLayout.beginRefreshing();

        return view;
    }


    protected void setListener(){
        mRefreshLayout.setDelegate(this);
    }
    protected void processLogic(Bundle savedInstanceState) {
        BGAMoocStyleRefreshViewHolder moocStyleRefreshViewHolder = new BGAMoocStyleRefreshViewHolder(this.getActivity(), false);
        moocStyleRefreshViewHolder.setOriginalImage(R.mipmap.bga_refresh_moooc);
        moocStyleRefreshViewHolder.setUltimateColor(R.color.colorPrimary);
        moocStyleRefreshViewHolder.setRefreshViewBackgroundColorRes(R.color.colorWhite);
        mRefreshLayout.setRefreshViewHolder(moocStyleRefreshViewHolder);
    }

    //防止在5.0以下版本中出现RecyclerView左右边距距离父窗口间距过大的问题,将RecyclerView的左右padding减去Item的阴影宽度
    private void dealCardElevation(RecyclerView recyclerView) {
        //获取阴影的宽度
        int cardElevation = getResources().getDimensionPixelSize(R.dimen.teacher_list_card_elevation);
        //将父窗口左右的padding减去Item阴影的宽度
        int leftPadding = recyclerView.getPaddingLeft();
        int rightPadding = recyclerView.getPaddingLeft();
        int bottomPadding = recyclerView.getPaddingLeft();
        int topPadding = recyclerView.getPaddingTop();
        leftPadding -= cardElevation;
        rightPadding -= cardElevation;
        recyclerView.setPadding(leftPadding,topPadding,rightPadding,bottomPadding);
    }

    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
//        if (activity instanceof OnListFragmentInteractionListener){
//            mListener = (OnListFragmentInteractionListener) activity;
//        } else {
//            throw new RuntimeException(activity.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onLoadMore(){
        if(adapter != null && adapter.hasLoadMoreView && !adapter.loading && adapter.canLoadMore){
            adapter.loading = true;
            new LoadTeachersTask(){
                @Override
                public void afterTask(TeacherListResult response){
                    if(response != null){
                        try{
                            next = response.getNext();
                        }catch(Exception e){
                            next = null;
                        }
                    }
                    adapter.loading = false;
                    if(next == null){
                        adapter.canLoadMore = false;
                    }
                }
            }.execute();
        }
    }

    @Override
    public void onBGARefreshLayoutBeginRefreshing(BGARefreshLayout bgaRefreshLayout){
        if(!adapter.loading){
            teachersList.clear();
            next = MalaApplication.getInstance().getMalaHost()+TEACHERS_PATH_V1;
            boolean hasParam = false;
            if(gradeId != null && gradeId > 0){
                next += "?grade=" + gradeId;
                hasParam = true;
            }
            if(subjectId != null && subjectId > 0){
                next += hasParam ? "&subject=" : "?subject=";
                next += subjectId;
                hasParam = true;
            }
            if(tagIds != null && tagIds.length > 0){
                next += hasParam ? "&tags=" : "?tags=";
                for(int i=0; i<tagIds.length;){
                    next += tagIds[i];
                    if(++i < tagIds.length){
                        next += "+";
                    }
                }
            }
            new LoadTeachersTask(){
                @Override
                public void afterTask(TeacherListResult response){
                    adapter.loading = false;
                    setRefreshing(false);
                }
            }.execute();
        }
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
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Teacher item);
    }

    public void setRefreshing(boolean status){
        mRefreshLayout.endRefreshing();
    }

    private void notifyDataSetChanged(){
        if(teachersList != null && teachersList.size() < 20){
            adapter.loading = false;
        }
        adapter.notifyDataSetChanged();
    }

    private class LoadTeachersTask extends AsyncTask<String, Integer, String>{
        public void afterTask(TeacherListResult response){
        }
        @Override
        protected String doInBackground(String ...params){
            try{
                String url = next;
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
                                        notifyDataSetChanged();
                                    }
                                }catch (Exception e){
                                    Log.e(LoginFragment.class.getName(), e.getMessage(), e);
                                }finally{
                                    afterTask(teacherResult);
                                }
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                afterTask(null);
                                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                            }
                        });
                requestQueue.add(jsArrayRequest);
                return "ok";
            }catch(Exception e){
                afterTask(null);
                Log.e(LoginFragment.class.getName(), e.getMessage(), e);
            }
            return null;
        }
    }

    class FooterSpanSizeLookup extends GridLayoutManager.SpanSizeLookup{
        private final GridLayoutManager gridLayoutManager;

        public FooterSpanSizeLookup(GridLayoutManager gridLayoutManager){
            this.gridLayoutManager = gridLayoutManager;
        }

        @Override
        public int getSpanSize(int position){
            if(gridLayoutManager.getItemCount() - 1 == position && adapter.hasLoadMoreView && adapter.canLoadMore){
                return 2;
            }else{
                return 1;
            }
        }
    }

}
