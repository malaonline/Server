package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.adapter.TeacherAdapter;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.api.MoreTeacherListApi;
import com.malalaoshi.android.api.TeacherListApi;
import com.malalaoshi.android.core.event.BusEvent;
import com.malalaoshi.android.result.TeacherListResult;

import de.greenrobot.event.EventBus;

/**
 * Created by kang on 16/6/22.
 */
public class TeacherListFragment extends BaseRefreshFragment<TeacherListResult> {

    private static String ARGS_GRADE_ID = "grade id";
    private static String ARGS_SUBJECT_ID = "subject id";
    private static String ARGS_TAGS_ID = "tags id";

    private Long gradeId;
    private Long subjectId;
    private long[] tagIds;

    private String nextUrl;

    private TeacherAdapter adapter;

    public static TeacherListFragment newInstance() {
        TeacherListFragment fragment = new TeacherListFragment();
        return fragment;
    }


    public static TeacherListFragment newInstance(Long gradeId, Long subjectId, long[] tagIds) {
        TeacherListFragment fragment = new TeacherListFragment();
        Bundle args = new Bundle();
        args.putLong(ARGS_GRADE_ID, gradeId);
        args.putLong(ARGS_SUBJECT_ID, subjectId);
        args.putLongArray(ARGS_TAGS_ID, tagIds);
        fragment.setArguments(args);
        return fragment;
    }

    public Long getGradeId() {
        return gradeId;
    }

    public void setGradeId(Long gradeId) {
        this.gradeId = gradeId;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public long[] getTagIds() {
        return tagIds;
    }

    public void setTagIds(long[] tagIds) {
        this.tagIds = tagIds;
    }

    public void refresh(){
        MalaContext.postOnMainThread(new Runnable() {
            @Override
            public void run() {
                autoRefresh();
            }
        });
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new TeacherAdapter(getContext());
        return adapter;
    }

    @Override
    protected TeacherListResult refreshRequest() throws Exception {
        return new TeacherListApi().getTeacherList(gradeId, subjectId, tagIds);
    }

    @Override
    protected TeacherListResult loadMoreRequest() throws Exception {
        return new MoreTeacherListApi().getTeacherList(nextUrl);
    }

    @Override
    protected void refreshFinish(TeacherListResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void loadMoreFinish(TeacherListResult response) {
        super.loadMoreFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void afterCreateView() {
        init();
    }

    private void init() {
        Bundle bundle = getArguments();
        if (bundle!=null){
            gradeId = bundle.getLong(ARGS_GRADE_ID);
            subjectId = bundle.getLong(ARGS_SUBJECT_ID);
            tagIds = bundle.getLongArray(ARGS_TAGS_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        EventBus.getDefault().register(this);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    public void onEventMainThread(BusEvent event) {
        switch (event.getEventType()) {
            case BusEvent.BUS_EVENT_RELOAD_TEACHERLIST_DATA:
                refresh();
                Log.d("TeacherListFragment","start loadDataBackground");
                break;
        }
    }

    @Override
    public String getStatName() {
        return "教师列表";
    }

}
