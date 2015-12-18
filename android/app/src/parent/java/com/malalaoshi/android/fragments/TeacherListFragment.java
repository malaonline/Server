package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.TeacherRecyclerViewAdapter;
import com.malalaoshi.android.decoration.TeacherListGridItemDecoration;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.listener.RecyclerViewLoadMoreListener;
import com.malalaoshi.android.util.RefreshLayoutUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class TeacherListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, RecyclerViewLoadMoreListener.OnLoadMoreListener{
    private OnListFragmentInteractionListener mListener;
    private TeacherRecyclerViewAdapter adapter;

    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers/";

    @Bind(R.id.teacher_list_refresh_layout)
    protected SwipeRefreshLayout refreshLayout;

    public TeacherListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_list, container, false);

        RecyclerView recyclerView = (RecyclerView)view.findViewById(R.id.teacher_list_recycler_view);
        if(recyclerView != null){
            Context context = view.getContext();
            GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new TeacherRecyclerViewAdapter(TeacherRecyclerViewAdapter.mValues, mListener);
            int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.teacher_list_card_diver);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new TeacherListGridItemDecoration(context,spacingInPixels));
            recyclerView.addOnScrollListener(new RecyclerViewLoadMoreListener(layoutManager, this, TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE));
        }
        ButterKnife.bind(this, view);
        RefreshLayoutUtils.initOnCreate(refreshLayout, this);
        RefreshLayoutUtils.refreshOnCreate(refreshLayout, this);
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
//        if (activity instanceof OnListFragmentInteractionListener) {
//            mListener = (OnListFragmentInteractionListener) activity;
//        } else {
//            throw new RuntimeException(activity.toString()
//                    + " must implement OnListFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onRefresh(){
        new loadTeachersTask().execute();
    }

    @Override
    public void onLoadMore(){
        new loadTeachersTask().execute();
    }

    /**
     * 根据筛选条件筛选老师
     * @param gradeId 年级ID
     * @param subjectId 科目ID
     * @param tagIds 风格ID数组。null or empty i.e. all
     */
    public void doFilter(long gradeId, long subjectId, List<Long> tagIds) {
        onRefresh();
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
        refreshLayout.setRefreshing(status);
    }

    private void notifyDataSetChanged(){
        if(TeacherRecyclerViewAdapter.mValues.size() < 20){
            adapter.setLoading(false);
        }
        adapter.notifyDataSetChanged();
    }

    private class loadTeachersTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String ...params){
            try{
                String url = MalaApplication.getInstance().getMalaHost()+TEACHERS_PATH_V1;
                RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
                JsonObjectRequest jsArrayRequest = new JsonObjectRequest(
                        Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>(){
                            @Override
                            public void onResponse(JSONObject response){
                                try{
                                    JSONArray result = response.getJSONArray("results");
                                    for(int i=0;i<result.length();i++){
                                        JSONObject obj = (JSONObject)result.get(i);
                                        Teacher teacher = new Teacher();
                                        teacher.setId(String.valueOf(i+1));
                                        teacher.setName(obj.getString("name"));
                                        String degreeStr = obj.optString("degree");
                                        if(degreeStr != null && degreeStr.length() == 1){
                                            teacher.setDegree(degreeStr.charAt(0));
                                        }
                                        teacher.setMinPrice(obj.optDouble("min_price"));
                                        teacher.setMaxPrice(obj.optDouble("max_price"));
                                        teacher.setSubject(obj.optLong("subject"));
                                        JSONArray gradesAry = obj.optJSONArray("grades");
                                        if(gradesAry != null && gradesAry.length() > 0){
                                            Long [] tmp = new Long[gradesAry.length()];
                                            for(int ind=0; ind < gradesAry.length(); ind++){
                                                tmp[ind] = Long.parseLong(gradesAry.get(ind).toString());
                                            }

                                            teacher.setGrades(tmp);
                                        }

                                        JSONArray tagsAry = obj.optJSONArray("tags");
                                        if(tagsAry != null && tagsAry.length() > 0){
                                            Long [] tmp = new Long[tagsAry.length()];
                                            for(int ind=0; ind < tagsAry.length(); ind++){
                                                tmp[ind] = Long.parseLong(tagsAry.get(ind).toString());
                                            }

                                            teacher.setTags(tmp);
                                        }

                                        TeacherRecyclerViewAdapter.mValues.add(teacher);
                                    }
                                    if(result.length() > 0){
                                        notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    Log.e(LoginFragment.class.getName(), e.getMessage(), e);
                                }
                                setRefreshing(false);
                            }
                        },
                        new Response.ErrorListener(){
                            @Override
                            public void onErrorResponse(VolleyError error){
                                setRefreshing(false);
                                Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                            }
                        });
                requestQueue.add(jsArrayRequest);
                return "ok";
            }catch(Exception e){
                setRefreshing(false);
                Log.e(LoginFragment.class.getName(), e.getMessage(), e);
            }
            return null;
        }
    }
}
