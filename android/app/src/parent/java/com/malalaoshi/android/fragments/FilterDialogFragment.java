package com.malalaoshi.android.fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
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
import com.malalaoshi.android.adapter.FilterAdapter;
import com.malalaoshi.android.base.BaseDialogFragment;
import com.malalaoshi.android.core.stat.StatReporter;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.result.TagListResult;
import com.malalaoshi.android.util.JsonUtil;
import com.malalaoshi.android.view.FlowLayout;
import com.malalaoshi.android.view.Indicator.RubberIndicator;

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
public class FilterDialogFragment extends BaseDialogFragment implements View.OnClickListener {
    private static final String TAG = FilterDialogFragment.class.getSimpleName();
    private static final String API_SUBJECTS_URL = "/api/v1/subjects";
    private static final String API_GRADES_URL = "/api/v1/grades";
    private static final String API_TAGS_URL = "/api/v1/tags";
    private static final String[] FILTER_VIEW_TITLES = new String[]{"筛选年级", "筛选科目", "筛选风格"};

    @Bind(R.id.filter_bar_left)
    protected ImageView mLeftIcon;
    @Bind(R.id.filter_bar_title)
    protected TextView mTitleView;

    @Bind(R.id.filter_bar_right)
    protected ImageView mRightIcon;

    @Bind(R.id.iv_dialog_ic)
    protected ImageView mIvDialogIcon;

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

  /*  @Bind(R.id.filter_tags_list)
    protected GridView mTagsViewList;

    @Bind(R.id.filter_tags_all)
    protected CheckedTextView mTagsAll;*/

    @Bind(R.id.tags_loading)
    protected View mTagsLoading;

    @Bind(R.id.tags_load_again)
    protected View mTagsLoadAgain;

    @Bind(R.id.tags_load_again_msg)
    protected TextView mTagsLoadAgainMsg;

    @Bind(R.id.tags_container)
    protected View mTagsContainer;

    @Bind(R.id.flowlayout_tags)
    protected FlowLayout mTagFlow;

    @Bind(R.id.filter_rubber)
    protected RubberIndicator mRubberIndicator;

    //tag样式
    private int mTagStyles[] = {
            R.drawable.tag1_textview_bg,
            R.drawable.tag2_textview_bg,
            R.drawable.tag3_textview_bg,
            R.drawable.tag4_textview_bg,
            R.drawable.tag5_textview_bg,
            R.drawable.tag6_textview_bg,
            R.drawable.tag7_textview_bg,
            R.drawable.tag8_textview_bg,
            R.drawable.tag9_textview_bg};

    private List<TextView> mTagViews = new ArrayList<>();

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

    private Grade mSelectedGrade = new Grade();
    private Subject mSelectedSubject = new Subject();
    private ArrayList<Tag> mSelectedTags = new ArrayList<>();

    private View mAllTag;

    public static FilterDialogFragment newInstance() {
        return new FilterDialogFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        this.setCancelable(false);          // 设置点击屏幕Dialog不消失
        mTotalSubjectsList = new ArrayList<>();
        mSubjectsList = new ArrayList<>();
        mGragesList = new ArrayList<>();
        mTagsList = new ArrayList<>();
        mSubGrages1List = new ArrayList<>();
        mSubGrages2List = new ArrayList<>();
        mSubGrages3List = new ArrayList<>();
        setData();
    }

    private void setData() {
        setSubjectsList();
        setGradeDatas();
       // setGradesList();
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

    private void setGradeDatas(){
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
        // 初中
        Grade middle = Grade.getGradeById(Grade.MIDDLE_ID);
        item = new HashMap<String, Object>();
        item.put("id", middle.getId());
        item.put("name", middle.getName());
        item.put("subjects", subjects2);
        item.put("subset", mSubGrages2List);
        mGragesList.add(item);
        // 高中
        Grade senior = Grade.getGradeById(Grade.SENIOR_ID);
        item = new HashMap<String, Object>();
        item.put("id", senior.getId());
        item.put("name", senior.getName());
        item.put("subjects", subjects2);
        item.put("subset", mSubGrages3List);
        mGragesList.add(item);

        // collect all grade
        for (Grade g: Grade.gradeList) {
            if (g.getSupersetId() == null) {
                continue;
            }
            if (g.getSupersetId() == Grade.PRIMARY_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name",  g.getName());
                item.put("subjects", subjects1);
                mSubGrages1List.add(item);
            }
            if (g.getSupersetId() == Grade.MIDDLE_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name",  g.getName());
                item.put("subjects", subjects2);
                mSubGrages2List.add(item);
            }
            if (g.getSupersetId() == Grade.SENIOR_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name",  g.getName());
                item.put("subjects", subjects2);
                mSubGrages3List.add(item);
            }
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
                item.put("name",  g.getName());
                item.put("subjects", subjects1);
                mSubGrages1List.add(item);
            }
            if (g.getSupersetId() == Grade.MIDDLE_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name",  g.getName());
                item.put("subjects", subjects2);
                mSubGrages2List.add(item);
            }
            if (g.getSupersetId() == Grade.SENIOR_ID) {
                item = new HashMap<String, Object>();
                item.put("id", g.getId());
                item.put("name",  g.getName());
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
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                // TODO Auto-generated method stub 返回键关闭dialog
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        return inflater.inflate(R.layout.dialog_filter, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        window.setLayout(width, height);//Here!
        ButterKnife.bind(this, getDialog());
        mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
        mSubjectsViewList.setAdapter(new FilterAdapter(getActivity(),
                mSubjectsList, R.layout.filter_subject_item));
        mGragesViewList1.setAdapter(new FilterAdapter(getActivity(),
                mSubGrages1List, R.layout.filter_grade_item));
        mGragesViewList2.setAdapter(new FilterAdapter(getActivity(),
                mSubGrages2List, R.layout.filter_grade_item));
        mGragesViewList3.setAdapter(new FilterAdapter(getActivity(),
                mSubGrages3List, R.layout.filter_grade_item));
        getTagsList();

        mRubberIndicator.setCount(3, 0);
    }


    public void initTagView(){
        List<Map<String, Object>> smallTags = new ArrayList<>();
        List<Map<String, Object>> bigTags = new ArrayList<>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("id", -1L);
        item.put("name", "不限");
        smallTags.add(item);
        for (int i=0;i<mTagsList.size();i++){
            Map<String, Object> tag = mTagsList.get(i);
            String name = (String) tag.get("name");
            if (name.length()<=4){
                smallTags.add(tag);
            }else{
                bigTags.add(tag);
            }
        }
        //

        LayoutInflater layoutInflater = LayoutInflater.from(getContext());
        int index = 0;
        //三列
        int len = smallTags.size()/3;
        int remainderSmall = smallTags.size()%3;
        for (int i=0;i<len;i++){
            View view = layoutInflater.inflate(R.layout.tag_three_item, mTagFlow,false);
            TextView textView1 = (TextView)view.findViewById(R.id.text1);
            if(i==0){  //第一个标签:"不限"
                mAllTag = textView1;
                textView1.setTextColor(getResources().getColor(R.color.colorWhite));
            }
            TextView textView2 = (TextView)view.findViewById(R.id.text2);
            TextView textView3 = (TextView)view.findViewById(R.id.text3);
            if (index>=mTagStyles.length) index = 1;
            textView1.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
            index++;
            if (index>=mTagStyles.length) index = 1;
            textView2.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
            index++;
            if (index>=mTagStyles.length) index = 1;
            textView3.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
            index++;
            textView1.setText((String) smallTags.get(3 * i).get("name"));
            textView1.setTag(smallTags.get(3 * i));
            textView1.setOnClickListener(this);
            textView2.setText((String) smallTags.get(3 * i + 1).get("name"));
            textView2.setOnClickListener(this);
            textView2.setTag(smallTags.get(3 * i + 1));
            textView3.setText((String) smallTags.get(3 * i + 2).get("name"));
            textView3.setOnClickListener(this);
            textView3.setTag(smallTags.get(3 * i + 2));
            mTagViews.add(textView1);
            mTagViews.add(textView2);
            mTagViews.add(textView3);
            mTagFlow.addView(view);
        }
        len = bigTags.size()/2;
        int remainderBig = bigTags.size()%2;
        for (int i=0;i<len;i++){
            View view = layoutInflater.inflate(R.layout.tag_two_items, mTagFlow,false);
            TextView textView1 = (TextView)view.findViewById(R.id.text1);
            TextView textView2 = (TextView)view.findViewById(R.id.text2);
            if (index>=mTagStyles.length) index = 1;
            textView1.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
            index++;
            if (index>=mTagStyles.length) index = 1;
            textView2.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
            index++;
            textView1.setOnClickListener(this);
            textView2.setOnClickListener(this);
            textView1.setText((String) bigTags.get(2 * i).get("name"));
            textView1.setTag(bigTags.get(2 * i));
            textView2.setText((String) bigTags.get(2 * i + 1).get("name"));
            textView2.setTag(bigTags.get(2 * i + 1));
            mTagViews.add(textView1);
            mTagViews.add(textView2);
            mTagFlow.addView(view);
        }

        if (remainderSmall>0){
            for (int i=0;i<remainderSmall;i++){
                TextView textView1 = (TextView)layoutInflater.inflate(R.layout.tag_textview, mTagFlow,false);
                ViewGroup.LayoutParams layoutParams = textView1.getLayoutParams();
                layoutParams.width = (mTagFlow.getMeasuredWidth()-mTagFlow.getPaddingLeft()*4)/3;
                textView1.setLayoutParams(layoutParams);
                textView1.setText((String) smallTags.get(smallTags.size() - remainderSmall + i).get("name"));
                if (index>=mTagStyles.length) index = 1;
                textView1.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
                index++;
                textView1.setOnClickListener(this);
                textView1.setTag(smallTags.get(smallTags.size() - remainderSmall + i));
                mTagFlow.addView(textView1);
                mTagViews.add(textView1);
            }
        }
        if (remainderBig>0){
            TextView textView1 = (TextView)layoutInflater.inflate(R.layout.tag_textview, mTagFlow,false);
            ViewGroup.LayoutParams layoutParams = textView1.getLayoutParams();
            layoutParams.width = (mTagFlow.getMeasuredWidth()-mTagFlow.getPaddingLeft()*3)/2;
            textView1.setLayoutParams(layoutParams);
            textView1.setText((String) bigTags.get(bigTags.size() - 1).get("name"));
            if (index>=mTagStyles.length) index = 1;
            textView1.setBackground(getActivity().getResources().getDrawable(mTagStyles[index]));
            index++;
            textView1.setOnClickListener(this);
            textView1.setTag(bigTags.get(bigTags.size() - 1));
            mTagFlow.addView(textView1);
            mTagViews.add(textView1);
        }
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
                        initTagView();
                        mTagsLoading.setVisibility(View.GONE);
                        mTagsLoadAgain.setVisibility(View.GONE);
                        mTagsContainer.setVisibility(View.VISIBLE);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "get tags list error", error);
                if (!MalaApplication.getInstance().isNetworkOk()) {
                    mTagsLoadAgainMsg.setText("网络已断开，请更改网络配置后加载");
                }
                mTagsLoading.setVisibility(View.GONE);
                mTagsLoadAgain.setVisibility(View.VISIBLE);
                mTagsContainer.setVisibility(View.GONE);
            }
        });
        requestQueue.add(jsonRequest);
    }

    @OnClick(R.id.filter_bar_left)
    protected void onClickLeftIcon() {
        //关闭
        if (mFilterViews.getDisplayedChild()==0){
            dismiss();
        }else {
            mRubberIndicator.moveToLeft();
            mFilterViews.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in));
            mFilterViews.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out));
            mFilterViews.showPrevious();
            mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
            if (mFilterViews.getDisplayedChild()==0) {
                mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.close_btn));
                mIvDialogIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_grade_dialog));
            }else if (mFilterViews.getDisplayedChild()==1){
                mIvDialogIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_subject_dialog));
            }else{
                mIvDialogIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_tag_dialog));
            }
        }
        mRightIcon.setVisibility(View.GONE);
    }

    @OnItemClick(R.id.filter_grages_list1)
    protected void onClickGradesList1(AdapterView<?> parent, View view, int position, long id) {
        selectGrade(1, position, view);
        StatReporter.filterGrade();
    }

    @OnItemClick(R.id.filter_grages_list2)
    protected void onClickGradesList2(AdapterView<?> parent, View view, int position, long id) {
        selectGrade(2, position, view);
        StatReporter.filterGrade();
    }

    @OnItemClick(R.id.filter_grages_list3)
    protected void onClickGradesList3(AdapterView<?> parent, View view, int position, long id) {
        selectGrade(3, position, view);
        StatReporter.filterGrade();
    }

    private void selectGrade(int stage, int position, View view) {
        mRubberIndicator.moveToRight();
        mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
        mIvDialogIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_subject_dialog));
        Log.d(TAG, "OnItemClick{" + stage + "}: " + position);

        ImageView imageView = (ImageView)view.findViewById(R.id.tv_filter_icon);
        if (mLastSelectedGradeView!=null) {
            mLastSelectedGradeView.setSelected(false);
        }
        imageView.setSelected(true);

        mLastSelectedGradeView = imageView;
        List<Map<String,Object>> subGrades = (List<Map<String,Object>>)mGragesList.get(stage-1).get("subset");
        Map grade = subGrades.get(position);
        mSelectedGradeId = (long)grade.get("id");
        mSelectedGrade.setId((long)grade.get("id"));
        mSelectedGrade.setName((String)grade.get("name"));

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
        ((FilterAdapter)mSubjectsViewList.getAdapter()).notifyDataSetChanged();
        gotoNextFilterView();
    }

    @OnItemClick(R.id.filter_subjects_list)
    protected void onClickSubjectsList(AdapterView<?> parent, View view, int position, long id) {
        mRubberIndicator.moveToRight();
        mLeftIcon.setImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
        mIvDialogIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_tag_dialog));
        mRightIcon.setVisibility(View.VISIBLE);

        ImageView imageView = (ImageView)view.findViewById(R.id.tv_filter_icon);
        if (mLastSelectedSubjectView!=null) {
            mLastSelectedSubjectView.setSelected(false);
        }
        imageView.setSelected(true);
        mLastSelectedSubjectView = imageView;
        mSelectedSubjectId = (long)mSubjectsList.get(position).get("id");
        mSelectedSubject.setId((long)mSubjectsList.get(position).get("id"));
        mSelectedSubject.setName((String)mSubjectsList.get(position).get("name"));

        gotoNextFilterView();
        StatReporter.filterSubject();
    }

    private void gotoNextFilterView() {
        mFilterViews.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
        mFilterViews.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
        mFilterViews.showNext();
        mTitleView.setText(FILTER_VIEW_TITLES[mFilterViews.getDisplayedChild()]);
    }

    //筛选
    @OnClick(R.id.filter_bar_right)
    protected void onClickBtnSearch() {
        StatReporter.filterFinish();
        dismiss();
        if (mAllTag!=null&&mAllTag.isSelected()){
            TeacherListFilterActivity.open(this.getActivity(), mSelectedGrade, mSelectedSubject, null);
            return;
        }
        //
        int index = 0;
        for (int i = 0; i < mTagViews.size(); i++) {
            View view = mTagViews.get(i);
            if (view.isSelected()) {
                Map<String, Object> tag = (Map<String, Object>) view.getTag();
                Tag tag1 = new Tag((Long)tag.get("id"),(String)tag.get("name"));
                mSelectedTags.add(tag1);
                index++;
            }
        }
       // Context context, Grade grade, Subject subject, ArrayList<Tag> tags
        TeacherListFilterActivity.open(this.getActivity(), mSelectedGrade, mSelectedSubject, mSelectedTags);
    }

    @OnClick(R.id.tags_load_again)
    protected void onClickTagsLoadAgain() {
        if (MalaApplication.getInstance().isNetworkOk()) {
            mTagsLoading.setVisibility(View.VISIBLE);
            mTagsLoadAgain.setVisibility(View.GONE);
            mTagsContainer.setVisibility(View.GONE);
            getTagsList();
        }
        //getTagsList();
    }

    @Override
    public void onClick(View v) {
            TextView text = (TextView)v;
        if (mAllTag==text){
            if (!mAllTag.isSelected()){
                for (int i=0;i<mTagViews.size();i++){
                    if (mTagViews.get(i).isSelected()){
                        mTagViews.get(i).setSelected(false);
                        mTagViews.get(i).setTextColor(getResources().getColor(R.color.text_color_dlg));
                    }
                }
            }
            StatReporter.ClickAllTag();
        }else{
            if (text.isSelected()){
                text.setTextColor(getResources().getColor(R.color.text_color_dlg));
            }else{
                text.setTextColor(getResources().getColor(R.color.colorWhite));
                if (mAllTag.isSelected()){
                    mAllTag.setSelected(false);
                }
            }
            StatReporter.ClickFilterTag(text.getText().toString());
        }
        v.setSelected(!v.isSelected());
    }
}
