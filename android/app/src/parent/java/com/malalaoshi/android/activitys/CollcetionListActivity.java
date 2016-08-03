package com.malalaoshi.android.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.fragments.CollectionListFragment;
import com.malalaoshi.android.util.FragmentUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/8/3.
 */
public class CollcetionListActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    @Bind(R.id.title_view)
    protected TitleBarView titleView;

    public static void launch(Activity activity) {
        Intent intent = new Intent(activity, CollcetionListActivity.class);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commen_list);
        ButterKnife.bind(this);
        titleView.setOnTitleBarClickListener(this);
        titleView.setTitle("我的收藏");
        if (savedInstanceState==null){
            CollectionListFragment collectionListFragment  = CollectionListFragment.newInstance();
            FragmentUtil.openFragment(R.id.fl_pager_fragment, getSupportFragmentManager(), null, collectionListFragment, CollectionListFragment.class.getName());
        }
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    @Override
    protected String getStatName() {
        return "我的收藏";
    }
}
