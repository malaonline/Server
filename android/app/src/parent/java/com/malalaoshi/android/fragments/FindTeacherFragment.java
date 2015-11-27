package com.malalaoshi.android.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.malalaoshi.android.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FindTeacherFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FindTeacherFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FindTeacherFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String[] subjectsArr;
    private String[] gradesArr;

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
        subjectsArr = new String[]{"语文", "数学", "英语"};
        gradesArr = new String[]{"一年级", "二年级", "三年级", "四年级", "五年级", "六年级"};
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view = inflater.inflate(R.layout.fragment_find_teacher, container, false);
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
        ListView subjectsListView = (ListView)view.findViewById(R.id.find_teacher_subjects_list);
        subjectsListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.abc_list_menu_item_layout, R.id.title, subjectsArr));
        subjectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position<0 || position>=subjectsArr.length) {
                    return;
                }
                String subject = subjectsArr[position];
                TextView label = (TextView)view.findViewById(R.id.subject_text);
                label.setText(subject);
            }
        });
        ListView gradesListView = (ListView)view.findViewById(R.id.find_teacher_grades_list);
        gradesListView.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.abc_list_menu_item_layout, R.id.title, gradesArr));
        gradesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position < 0 || position >= gradesArr.length) {
                    return;
                }
                String grade = gradesArr[position];
                TextView label = (TextView) view.findViewById(R.id.grade_text);
                label.setText(grade);
            }
        });
        // 选择上课地点
        return view;
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

}
