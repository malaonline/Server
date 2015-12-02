package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.util.FragmentUtil;
import com.malalaoshi.android.view.WheelView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindTeacherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindTeacherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindTeacherFragment extends Fragment {
    private static final String TAG = FindTeacherFragment.class.getSimpleName();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String API_SUBJECTS_URL = "/api/v1/subjects/";
    private static final String API_GRADES_URL = "/api/v1/grades/";
    private static final String API_SCHOOLS_URL = "/api/v1/schools/";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<Map<String,String>> subjectsList;
    private List<Map<String,String>> gradesList;
    private List<Map<String,String>> schoolsList;
    @Bind(R.id.school_list)
    protected ListView mSchoolListView;
    @Bind(R.id.school_row)
    protected View mSchoolRow;
    @Bind(R.id.school_tv)
    protected TextView mSchoolLabel;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FindTeacherFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FindTeacherFragment newInstance(String param1, String param2) {
        FindTeacherFragment fragment = new FindTeacherFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FindTeacherFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        subjectsList = new ArrayList<Map<String, String>>();
        gradesList = new ArrayList<Map<String, String>>();
        schoolsList = new ArrayList<Map<String, String>>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_find_teacher, container, false);
        ButterKnife.bind(this, view);
        // 科目年级事件
        View subjectsGradesRow = view.findViewById(R.id.subjects_grades_row);
        subjectsGradesRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                View subjectsGradesComp = view.findViewById(R.id.subjects_grades_comp);
                int visibility = subjectsGradesComp.getVisibility();
                if (visibility==View.GONE) {
                    subjectsGradesComp.setVisibility(View.VISIBLE);
                } else {
                    subjectsGradesComp.setVisibility(View.GONE);
                }
            }
        });
        // 科目年级列表
//        ListView subjectsListView = (ListView)view.findViewById(R.id.find_teacher_subjects_list);
//        subjectsListView.setAdapter(new SimpleAdapter(getActivity(), subjectsList, R.layout.abc_list_menu_item_layout, new String[]{"name"}, new int[]{R.id.title}));
//        subjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                if (position < 0 || position >= subjectsList.size()) {
//                    return;
//                }
//                String subject = subjectsList.get(position).get("name");
//                TextView label = (TextView)view.findViewById(R.id.subject_text);
//                label.setText(subject);
//            }
//        });
//        updateListView(API_SUBJECTS_URL, subjectsList, subjectsListView);
        WheelView subjectsListView = (WheelView) view.findViewById(R.id.find_teacher_subjects_list);
        subjectsListView.setOffset(1);
        subjectsListView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "subject selectedIndex: " + selectedIndex + ", item: " + item);
                TextView label = (TextView) view.findViewById(R.id.subject_text);
                label.setText(item);
            }
        });
        updateListView(API_SUBJECTS_URL, subjectsList, subjectsListView);
//        ListView gradesListView = (ListView)view.findViewById(R.id.find_teacher_grades_list);
//        gradesListView.setAdapter(new SimpleAdapter(getActivity(), gradesList, R.layout.abc_list_menu_item_layout, new String[]{"name"}, new int[]{R.id.title}));
//        gradesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
//                if (position < 0 || position >= gradesList.size()) {
//                    return;
//                }
//                String grade = gradesList.get(position).get("name");
//                TextView label = (TextView) view.findViewById(R.id.grade_text);
//                label.setText(grade);
//            }
//        });
//        updateListView(API_GRADES_URL, gradesList, gradesListView);
        WheelView gradesListView = (WheelView) view.findViewById(R.id.find_teacher_grades_list);
        gradesListView.setOffset(1);
        gradesListView.setOnWheelViewListener(new WheelView.OnWheelViewListener() {
            @Override
            public void onSelected(int selectedIndex, String item) {
                Log.d(TAG, "grade selectedIndex: " + selectedIndex + ", item: " + item);
                TextView label = (TextView) view.findViewById(R.id.grade_text);
                label.setText(item);
            }
        });
        updateListView(API_GRADES_URL, gradesList, gradesListView);
        // 选择学习中心
        mSchoolListView.setAdapter(new SimpleAdapter(getActivity(), schoolsList, R.layout.abc_list_menu_item_layout, new String[]{"name"}, new int[]{R.id.title}));
        updateListView(API_SCHOOLS_URL, schoolsList, mSchoolListView);
        return view;
    }

    private void updateListView(final String apiUrl, final List<Map<String, String>> dataSet, final View listView) {
        String url = MalaApplication.getInstance().getMalaHost() + apiUrl;
        Log.d(TAG, String.valueOf(url));
        RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        dataSet.clear();
                        List<String> list = new ArrayList<>();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            int len = results.length();
                            for (int i = 0; i < len; i++) {
                                JSONObject obj = results.getJSONObject(i);
                                Map<String, String> item = new HashMap<String, String>();
                                item.put("name", obj.getString("name"));
                                dataSet.add(item);
                                list.add(obj.getString("name"));
                            }
                        }catch (Exception e) {
                        }
                        if (listView instanceof WheelView) {
                            ((WheelView)listView).setItems(list);
                        } else {
                            ((SimpleAdapter)((ListView)listView).getAdapter()).notifyDataSetChanged();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, "get data list error", error);
                    }
                });
        requestQueue.add(jsonRequest);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
//            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    @OnClick(R.id.find_teacher_btn)
    protected void onBtnFindTeacherClick() {
        FragmentUtil.opFragmentMainActivity(getFragmentManager(), this, new TeacherListFragment(), TeacherListFragment.class.getName());
    }

    @OnClick(R.id.school_row)
    protected void onViewSchoolRowClick() {
        if (mSchoolListView==null) return;
        int visibility = mSchoolListView.getVisibility();
        if (visibility==View.GONE) {
            mSchoolListView.setVisibility(View.VISIBLE);
        } else {
            mSchoolListView.setVisibility(View.GONE);
        }
    }

    @OnItemClick(R.id.school_list)
    protected void onListViewSchoolItemClick(AdapterView<?> parent, View v, int position, long id) {
        Log.d(TAG, "select school " + position);
        if (position < 0 || position >= schoolsList.size() || mSchoolLabel == null) {
            return;
        }
        String school = schoolsList.get(position).get("name");
        mSchoolLabel.setText(school);
    }

}
