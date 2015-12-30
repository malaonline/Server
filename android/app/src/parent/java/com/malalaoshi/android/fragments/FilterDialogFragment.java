package com.malalaoshi.android.fragments;

import android.app.Fragment;
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
import com.android.volley.toolbox.StringRequest;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.TeacherListFilterActivity;
import com.malalaoshi.android.base.BaseDialogFragment;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.result.TagListResult;
import com.malalaoshi.android.util.JsonUtil;

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
public class FilterDialogFragment extends BaseDialogFragment {
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

    private List<Map<String, Object>> mTotalSubjectsList;
    private List<Map<String, Object>> mSubjectsList; // for subjects list adapter
    private List<Map<String, Object>> mGragesList;
    private List<Map<String, Object>> mTagsList;
    private List<Map<String, Object>> mSubGrages1List; // 小学adapter data
    private List<Map<String, Object>> mSubGrages2List; // 初中adapter data
    private List<Map<String, Object>> mSubGrages3List; // 高中adapter data

    private View mLastSelectedGradeView;
    private long mSelectedGradeId;

    private View mLastSelectedSubjectView;
    private long mSelectedSubjectId;

    private List<Long> mSelectedTagsId;

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTotalSubjectsList = new ArrayList<>();
        mSubjectsList = new ArrayList<>();
        mGragesList = new ArrayList<>();
        mTagsList = new ArrayList<>();
        mSelectedTagsId = new ArrayList<>();
        mSubGrages1List = new ArrayList<>();
        mSubGrages2List = new ArrayList<>();
        mSubGrages3List = new ArrayList<>();
        setData();
    }

    private void setData() {
        setSubjectsList();
        setGradesList();
//        setTagsList(Tag.tags);
    }

    private void setSubjectsList() {
        for (int i = 0; i < Subject.subjectList.size(); i++) {
            Subject subject = Subject.subjectList.get(i);
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", subject.getId());
            item.put("name", subject.getName());
            mTotalSubjectsList.add(item);
        }
    }

    private void setGradesList() {
        long[] subjects1 = new long[]{1,2,3};
        long[] subjects2 = new long[]{1,2,3,4,5,6,7,8,9};
        // 小学
        Grade primary = Grade.getGradeById(Grade.PRIMARY_ID);
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", primary.getId());
        item.put("name", primary.getName());
        item.put("subjects", subjects1);
        item.put("subset", mSubGrages1List);
        mGragesList.add(item);
        item = new HashMap<String, Object>();
        item.put("id", primary.getId());
        item.put("name", primary.getName()+"全部");
        item.put("subjects", subjects1);
        mSubGrages1List.add(item);
        // 初中
        Grade middle = Grade.getGradeById(Grade.MIDDLE_ID);
        item = new HashMap<String, Object>();
        item.put("id", middle.getId());
        item.put("name", middle.getName());
        item.put("subjects", subjects2);
        item.put("subset", mSubGrages2List);
        mGragesList.add(item);
        item = new HashMap<String, Object>();
        item.put("id", middle.getId());
        item.put("name", middle.getName()+"全部");
        item.put("subjects", subjects2);
        mSubGrages2List.add(item);
        // 高中
        Grade senior = Grade.getGradeById(Grade.SENIOR_ID);
        item = new HashMap<String, Object>();
        item.put("id", senior.getId());
        item.put("name", senior.getName());
        item.put("subjects", subjects2);
        item.put("subset", mSubGrages3List);
        mGragesList.add(item);
        item = new HashMap<String, Object>();
        item.put("id", senior.getId());
        item.put("name", senior.getName()+"全部");
        item.put("subjects", subjects2);
        mSubGrages3List.add(item);
        // collect all grade
        for (Grade g: Grade.gradeList) {
            if (g.getSupersetId() == null) {
                continue;
            }
            if (g.getSupersetId() == Grade.PRIMARY_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name", primary.getName() + g.getName());
                item.put("subjects", subjects1);
                mSubGrages1List.add(item);
            }
            if (g.getSupersetId() == Grade.MIDDLE_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name", middle.getName() + g.getName());
                item.put("subjects", subjects2);
                mSubGrages2List.add(item);
            }
            if (g.getSupersetId() == Grade.SENIOR_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name", senior.getName() + g.getName());
                item.put("subjects", subjects2);
                mSubGrages3List.add(item);
            }
        }
    }

    private void setTagsList(List<Tag> tags) {
        if (tags == null || tags.isEmpty()) {
            return;
        }
        mTagsList.clear();
        for (int i = 0; i < tags.size(); i++) {
            Tag obj = tags.get(i);
            Map<String, Object> item = new HashMap<String, Object>();
            item.put("id", obj.getId());
            item.put("name", obj.getName());
            mTagsList.add(item);
        }
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
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        window.setLayout(width, width);//Here!
        ButterKnife.bind(this, getDialog());
        mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
        mSubjectsViewList.setAdapter(new SimpleAdapter(getActivity(),
                mSubjectsList, R.layout.abc_list_item_singlechoice,
                new String[]{"name"},
                new int[]{R.id.text1}));
        mTagsViewList.setAdapter(new SimpleAdapter(getActivity(),
                mTagsList, R.layout.abc_list_item_multichoice,
                new String[]{"name"},
                new int[]{R.id.text1}));
        mGragesViewList1.setAdapter(new SimpleAdapter(getActivity(),
                mSubGrages1List, R.layout.abc_list_item_singlechoice,
                new String[]{"name"},
                new int[]{R.id.text1}));
        mGragesViewList2.setAdapter(new SimpleAdapter(getActivity(),
                mSubGrages2List, R.layout.abc_list_item_singlechoice,
                new String[]{"name"},
                new int[]{R.id.text1}));
        mGragesViewList3.setAdapter(new SimpleAdapter(getActivity(),
                mSubGrages3List, R.layout.abc_list_item_singlechoice,
                new String[]{"name"},
                new int[]{R.id.text1}));
        getTagsList();
    }

    @Deprecated
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
                        mTotalSubjectsList.clear();
                        try {
                            JSONArray results = response.getJSONArray("results");
                            for (int i = 0; i < results.length(); i++) {
                                JSONObject obj = results.getJSONObject(i);
                                Map<String, Object> item = new HashMap<String, Object>();
                                item.put("id", obj.getLong("id"));
                                item.put("name", obj.getString("name"));
                                mTotalSubjectsList.add(item);
                            }
                        }catch (Exception e) {
                        }
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
                                item.put("id", obj.getLong("id"));
                                item.put("name", obj.getString("name"));
                                JSONArray subjects = obj.getJSONArray("subjects");
                                long[] subjectIds = new long[subjects.length()];
                                for (int s = 0; s < subjectIds.length; s++) {
                                    subjectIds[s] = subjects.getLong(s);
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
                                    subItem.put("id", subObj.getLong("id"));
                                    subItem.put("name", subObj.getString("name"));
                                    JSONArray subjects2 = subObj.getJSONArray("subjects");
                                    long[] subjectIds2 = new long[subjects2.length()];
                                    for (int s = 0; s < subjectIds2.length; s++) {
                                        subjectIds2[s] = subjects2.getLong(s);
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
                        mSubGrages1List.clear();
                        mSubGrages1List.addAll(subGrades1);
                        ((SimpleAdapter)mGragesViewList1.getAdapter()).notifyDataSetChanged();
                        List<Map<String,Object>> subGrades2 = (List<Map<String,Object>>)mGragesList.get(1).get("subset");
                        mSubGrages2List.clear();
                        mSubGrages2List.addAll(subGrades1);
                        ((SimpleAdapter)mGragesViewList2.getAdapter()).notifyDataSetChanged();
                        List<Map<String,Object>> subGrades3 = (List<Map<String,Object>>)mGragesList.get(2).get("subset");
                        mSubGrages3List.clear();
                        mSubGrages3List.addAll(subGrades1);
                        ((SimpleAdapter)mGragesViewList3.getAdapter()).notifyDataSetChanged();
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
        StringRequest jsonRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        TagListResult tagsResult = JsonUtil.parseStringData(response, TagListResult.class);
                        List<Tag> tags = tagsResult.getResults();
                        setTagsList(tags);
                        ((SimpleAdapter)mTagsViewList.getAdapter()).notifyDataSetChanged();
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
        Map grade = subGrades.get(position);
        mSelectedGradeId = (long)grade.get("id");
        long[] subjectIds = (long[])grade.get("subjects");
        mSubjectsList.clear();
        for (int s = 0; s < subjectIds.length; s++) {
            long sId = subjectIds[s];
            for (int i = 0; i < mTotalSubjectsList.size(); i++) {
                Map subj = mTotalSubjectsList.get(i);
                if ((long)subj.get("id") == sId) {
                    mSubjectsList.add(subj);
                    break;
                }
            }
        }
        ((SimpleAdapter)mSubjectsViewList.getAdapter()).notifyDataSetChanged();
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
        mSelectedSubjectId = (long)mSubjectsList.get(position).get("id");
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
        CheckedTextView ckt = (CheckedTextView)view;
        boolean wasSelected = ckt.isChecked(); // old state
        ckt.setChecked(!wasSelected);
        long tagId = (long)mTagsList.get(position).get("id");
        if (!wasSelected) { // must not use ckt.isChecked()
            mSelectedTagsId.add(tagId);
        } else {
            mSelectedTagsId.remove(tagId);
        }
        mTagsAll.setChecked(mSelectedTagsId.isEmpty());
    }

    @OnClick(R.id.filter_tags_all)
    protected void onClickTagsAll() {
        Log.d(TAG, "OnItemClick{tags}: all");
        if (mTagsAll.isChecked()) {
            return;
        }
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
        Fragment teacherListFragment = getFragmentManager().findFragmentByTag(TeacherListFragment.class.getName());
        if (teacherListFragment != null) {
            long [] selectedTags = new long[mSelectedTagsId.size()];
            for(int i=0; i< selectedTags.length; i++){
                selectedTags[i] = mSelectedTagsId.get(i);
            }
            TeacherListFilterActivity.open(this.getActivity(), mSelectedGradeId, mSelectedSubjectId, selectedTags);
        }
    }
}
