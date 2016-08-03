package com.malalaoshi.android.fragments;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.TeacherAdapter;
import com.malalaoshi.android.api.CollectionListApi;
import com.malalaoshi.android.api.MoreTeacherListApi;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.result.TeacherListResult;

/**
 * Created by kang on 16/8/3.
 */
public class CollectionListFragment  extends BaseRefreshFragment<TeacherListResult> {
    private String nextUrl;
    private TeacherAdapter adapter;

    @Override
    public String getStatName() {
        return "收藏页列表";
    }

    @Override
    protected BaseRecycleAdapter createAdapter() {
        adapter = new TeacherAdapter(getContext());
        return adapter;
    }

    @Override
    protected TeacherListResult refreshRequest() throws Exception {
        return new CollectionListApi().getTeacherList();
    }

    @Override
    protected void refreshFinish(TeacherListResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected TeacherListResult loadMoreRequest() throws Exception {
        return new MoreTeacherListApi().getTeacherList(nextUrl);
    }

    @Override
    protected void afterCreateView() {
        setEmptyViewText("您喜欢收藏的老师会在这里出现哦\n快去老师详情页收藏吧!");
        setEmptyViewIcon(R.drawable.ic_empty_collection);
    }

    public static CollectionListFragment newInstance() {
        CollectionListFragment f = new CollectionListFragment();
        return f;
    }
}
