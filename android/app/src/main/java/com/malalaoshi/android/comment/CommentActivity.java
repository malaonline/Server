package com.malalaoshi.android.comment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.View;

import com.malalaoshi.android.core.base.BaseTitleActivity;

/**
 * 我的评价
 * Created by tianwei on 16-6-12.
 */
public class CommentActivity extends BaseTitleActivity {

    @Override
    protected String getStatName() {
        return "我的评价";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTitleBar().setRightVisibility(View.GONE);
        replaceFragment(Fragment.instantiate(this, MyCommentListFragment.class.getName(), getIntent().getExtras()));
    }

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, CommentActivity.class);
        activity.startActivity(intent);
    }
}
