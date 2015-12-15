package com.malalaoshi.android.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

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
 * Created by liumengjun on 12/14/15.
 */
public class FilterDialogFragment extends DialogFragment {
    private static final String TAG = FilterDialogFragment.class.getSimpleName();
    private static final String API_SUBJECTS_URL = "/api/v1/subjects/";
    private static final String API_GRADES_URL = "/api/v1/grades/";
    private static final String API_TAGS_URL = "/api/v1/tags/";
    private static final String[] FILTER_VIEW_TITLES = new String[]{"年级", "科目", "风格"};

    @Bind(R.id.filter_bar_left)
    protected ImageView mLeftIcon;
    @Bind(R.id.filter_bar_title)
    protected TextView mTitleView;
    @Bind(R.id.filter_bar_close)
    protected ImageView mCloseIcon;

    @Bind(R.id.filter_views)
    protected ViewFlipper mFilterViews;
    @Bind(R.id.filter_grages_list1)
    protected GridView mGragesViewList1;
    @Bind(R.id.filter_grages_list2)
    protected GridView mGragesViewList2;
    @Bind(R.id.filter_grages_list3)
    protected GridView mGragesViewList3;
    @Bind(R.id.filter_subjects_list)
    protected GridView mSubjectsViewList;
    @Bind(R.id.filter_tags_list)
    protected GridView mTagsViewList;
    @Bind(R.id.filter_tags_all)
    protected CheckedTextView mTagsAll;

    private List<Map<String, Object>> mSubjectsList;
    private List<Map<String, Object>> mGragesList;
    private List<Map<String, Object>> mTagsList;

    private View mLastSelectedGradeView;
    private String mSelectedGradeId;

    private View mLastSelectedSubjectView;
    private String mSelectedSubjectId;

    private List<String> mSelectedTagsId;

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSubjectsList = new ArrayList<>();
        mGragesList = new ArrayList<>();
        mTagsList = new ArrayList<>();
        mSelectedTagsId = new ArrayList<>();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(window.getAttributes().width, 1000);//Here!
        //设置自定义的title  layout
        window.setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.dialog_filter_title);
        ButterKnife.bind(this, getDialog());
        mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
        getData();
    }

    private void getData() {
        getSubjectsList();
        getGradesList();
        getTagsList();
    }

    private void getSubjectsList() {
        String url = MalaApplication.getInstance().getMalaHost() + API_SUBJECTS_URL;
        RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mSubjectsList.clear();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id", obj.getString("id"));
                                item.put("name", obj.getString("name"));
                                mSubjectsList.add(item);
                            }
                        }catch (Exception e) {
                        }
                        mSubjectsViewList.setAdapter(new SimpleAdapter(getActivity(),
                                mSubjectsList, R.layout.abc_list_item_singlechoice,
                                new String[]{"name"},
                                new int[]{R.id.text1}));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "get subjects list error", error);
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void getGradesList() {
        String url = MalaApplication.getInstance().getMalaHost() + API_GRADES_URL;
        RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mGragesList.clear();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id", obj.getString("id"));
                                item.put("name", obj.getString("name"));
                                JSONArray subjects = obj.getJSONArray("subjects");
                                int[] subjectIds = new int[subjects.length()];
                                for (int s = 0; s < subjectIds.length; s++) {
                                    subjectIds[s] = subjects.getInt(s);
                                }
                                item.put("subjects", subjectIds);
                                List<Map> subGrades = new ArrayList<>();
                                Map itemClone = new HashMap(item);
                                itemClone.put("name", item.get("name")+"全部");
                                subGrades.add(itemClone);
                                JSONArray subset = obj.getJSONArray("subset");
                                for (int k = 0; k < subset.length(); k++) {
                                    JSONObject subObj = subset.getJSONObject(k);
                                    Map<String, Object> subItem = new HashMap<String, Object>();
                                    subItem.put("id", subObj.getString("id"));
                                    subItem.put("name", subObj.getString("name"));
                                    JSONArray subjects2 = subObj.getJSONArray("subjects");
                                    int[] subjectIds2 = new int[subjects2.length()];
                                    for (int s = 0; s < subjectIds2.length; s++) {
                                        subjectIds2[s] = subjects2.getInt(s);
                                    }
                                    subItem.put("subjects", subjectIds2);
                                    subGrades.add(subItem);
                                }
                                item.put("subset", subGrades);
                                mGragesList.add(item);
                            }
                        }catch (Exception e) {
                        }
                        List<Map<String,Object>> subGrades1 = (List<Map<String,Object>>)mGragesList.get(0).get("subset");
                        mGragesViewList1.setAdapter(new SimpleAdapter(getActivity(),
                                subGrades1, R.layout.abc_list_item_singlechoice,
                                new String[]{"name"},
                                new int[]{R.id.text1}));
                        List<Map<String,Object>> subGrades2 = (List<Map<String,Object>>)mGragesList.get(1).get("subset");
                        mGragesViewList2.setAdapter(new SimpleAdapter(getActivity(),
                                subGrades2, R.layout.abc_list_item_singlechoice,
                                new String[]{"name"},
                                new int[]{R.id.text1}));
                        List<Map<String,Object>> subGrades3 = (List<Map<String,Object>>)mGragesList.get(2).get("subset");
                        mGragesViewList3.setAdapter(new SimpleAdapter(getActivity(),
                                subGrades3, R.layout.abc_list_item_singlechoice,
                                new String[]{"name"},
                                new int[]{R.id.text1}));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "get grades list error", error);
            }
        });
        requestQueue.add(jsonRequest);
    }

    private void getTagsList() {
        String url = MalaApplication.getInstance().getMalaHost() + API_TAGS_URL;
        RequestQueue requestQueue = MalaApplication.getHttpRequestQueue();
        JsonObjectRequest jsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mTagsList.clear();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id", obj.getString("id"));
                                item.put("name", obj.getString("name"));
                                mTagsList.add(item);
                            }
                        }catch (Exception e) {
                        }
                        mTagsViewList.setAdapter(new SimpleAdapter(getActivity(),
                                mTagsList, R.layout.abc_list_item_multichoice,
                                new String[]{"name"},
                                new int[]{R.id.text1}));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "get tags list error", error);
            }
        });
        requestQueue.add(jsonRequest);
    }

    @OnClick(R.id.filter_bar_left)
    protected void onClickLeftIcon() {
        mFilterViews.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in));
        mFilterViews.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out));
        mFilterViews.showPrevious();
        mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
        if (mFilterViews.getDisplayedChild()==0) {
            mLeftIcon.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.filter_bar_close)
    protected void onClickCloseIcon() {
        dismiss();
    }

    @OnItemClick(R.id.filter_grages_list1)
    protected void onClickGradesList1(AdapterView<?> parent, View view, int position, long id) {
        selectGrade(1, position, view);
    }

    @OnItemClick(R.id.filter_grages_list2)
    protected void onClickGradesList2(AdapterView<?> parent, View view, int position, long id) {
        selectGrade(2, position, view);
    }

    @OnItemClick(R.id.filter_grages_list3)
    protected void onClickGradesList3(AdapterView<?> parent, View view, int position, long id) {
        selectGrade(3, position, view);
    }

    private void selectGrade(int stage, int position, View view) {
        Log.d(TAG, "OnItemClick{" + stage + "}: " + position);
        CheckedTextView ckt = (CheckedTextView)view;
        if (mLastSelectedGradeView!=null) {
            ((CheckedTextView)mLastSelectedGradeView).setChecked(false);
        }
        ckt.setChecked(true);
        mLastSelectedGradeView = ckt;
        List<Map<String,Object>> subGrades = (List<Map<String,Object>>)mGragesList.get(stage-1).get("subset");
        mSelectedGradeId = (String)subGrades.get(position).get("id");
        gotoNextFilterView();
    }

    @OnItemClick(R.id.filter_subjects_list)
    protected void onClickSubjectsList(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "OnItemClick{subject}: " + position);
        CheckedTextView ckt = (CheckedTextView)view;
        if (mLastSelectedSubjectView!=null) {
            ((CheckedTextView)mLastSelectedSubjectView).setChecked(false);
        }
        ckt.setChecked(true);
        mLastSelectedSubjectView = ckt;
        mSelectedSubjectId = (String)mSubjectsList.get(position).get("id");
        gotoNextFilterView();
    }

    private void gotoNextFilterView() {
        mFilterViews.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
        mFilterViews.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
        mFilterViews.showNext();
        mLeftIcon.setVisibility(View.VISIBLE);
        mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
    }

    @OnItemClick(R.id.filter_tags_list)
    protected void onClickTagsList(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "OnItemClick{tags}: " + position);
        if (mTagsAll.isChecked()) {
            mTagsAll.setChecked(false);
        }
        CheckedTextView ckt = (CheckedTextView)view;
        ckt.setChecked(!ckt.isChecked());
        String tagId = (String)mTagsList.get(position).get("id");
        if (ckt.isChecked()) {
            mSelectedTagsId.add(tagId);
        } else {
            mSelectedTagsId.remove(tagId);
        }
    }

    @OnClick(R.id.filter_tags_all)
    protected void onClickTagsAll() {
        Log.d(TAG, "OnItemClick{tags}: all");
        mTagsAll.setChecked(!mTagsAll.isChecked());
        if (mTagsAll.isChecked()) {
            for (int i = 0; i < mTagsViewList.getChildCount(); i++) {
                CheckedTextView v = (CheckedTextView)mTagsViewList.getChildAt(i);
                if (v.isChecked()) {
                    v.setChecked(false);
                }
            }

        }
    }

    @OnClick(R.id.filter_search)
    protected void onClickBtnSearch() {
        dismiss();
    }
}
