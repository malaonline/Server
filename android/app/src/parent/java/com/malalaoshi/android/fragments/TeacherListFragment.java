package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
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

import org.json.JSONArray;
import org.json.JSONObject;


public class TeacherListFragment extends Fragment {
    private OnListFragmentInteractionListener mListener;
    private TeacherRecyclerViewAdapter adapter;

    private static final String TEACHERS_PATH_V1 = "/api/v1/teachers";

    public TeacherListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.teacher_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new GridLayoutManager(context, 2));
            adapter = new TeacherRecyclerViewAdapter(TeacherRecyclerViewAdapter.mValues, mListener);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new TeacherListGridItemDecoration(context));
        }
        refresh();
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

    public void refresh(){
        new loadTeachersTask().execute();
    }
    private class loadTeachersTask extends AsyncTask<String, Integer, String>{
        @Override
        protected String doInBackground(String... params){
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
                                            for(int ind=0; ind < gradesAry.length(); i++){
                                                tmp[i] = Long.parseLong(gradesAry.get(i).toString());
                                            }

                                            teacher.setGrades(tmp);
                                        }

                                        JSONArray tagsAry = obj.optJSONArray("tags");
                                        if(tagsAry != null && tagsAry.length() > 0){
                                            Long [] tmp = new Long[tagsAry.length()];
                                            for(int ind=0; ind < tagsAry.length(); i++){
                                                tmp[i] = Long.parseLong(tagsAry.get(i).toString());
                                            }

                                            teacher.setTags(tmp);
                                        }

                                        TeacherRecyclerViewAdapter.mValues.add(teacher);
                                    }
                                    if(result.length() > 0){
                                        adapter.notifyDataSetChanged();
                                    }
                                } catch (Exception e) {
                                    Log.e(LoginFragment.class.getName(), e.getMessage(), e);
                                }
                            }
                        }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        Log.e(LoginFragment.class.getName(), error.getMessage(), error);
                    }
                });
                requestQueue.add(jsArrayRequest);
                return "ok";
            }catch(Exception e){
                Log.e(LoginFragment.class.getName(), e.getMessage(), e);
            }
            return null;
        }
    }
}
