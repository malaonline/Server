package com.malalaoshi.android.comment;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseRecycleAdapter;
import com.malalaoshi.android.core.base.BaseRefreshFragment;
import com.malalaoshi.android.course.api.CourseInfoApi;
import com.malalaoshi.android.course.api.MoreCourseInfoApi;

/**
 * 我的评价
 * Created by tianwei on 16-6-12.
 */
public class MyCommentListFragment extends BaseRefreshFragment<CommentResult> {

    private String nextUrl;

    @Override
    public String getStatName() {
        return "我的评价";
    }

    @Override
    protected void afterCreateView() {
        setEmptyViewText("当前暂无评价\n上完课再来这里吧");
        setEmptyViewIcon(R.drawable.ic_empty_comment);
    }

    @Override
    public BaseRecycleAdapter createAdapter() {
        CommentAdapter adapter = new CommentAdapter(getContext());
        adapter.setFragmentManager(getFragmentManager());
        return adapter;
    }

    @Override
    protected CommentResult refreshRequest() throws Exception {
        return new CourseInfoApi().getCourseList();
    }

    @Override
    protected void refreshFinish(CommentResult response) {
        super.refreshFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected void loadMoreFinish(CommentResult response) {
        super.loadMoreFinish(response);
        if (response != null) {
            nextUrl = response.getNext();
        }
    }

    @Override
    protected CommentResult loadMoreRequest() throws Exception {
        return new MoreCourseInfoApi().getCourseList(nextUrl);
    }
}
