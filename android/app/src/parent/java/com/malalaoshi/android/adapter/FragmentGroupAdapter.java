package com.malalaoshi.android.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kang on 16/1/15.
 */
public class FragmentGroupAdapter extends FragmentPagerAdapter{
    private List<View> tabs = new ArrayList<View>();
    private Context context;
    private IFragmentGroup fragment;

    public FragmentGroupAdapter(Context context, FragmentManager fm, IFragmentGroup fragment) {
        super(fm);
        this.context = context;
        this.fragment = fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment.createFragment(position);
    }

    @Override
    public int getCount() {
        return fragment.getFragmentCount();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragment.getPageTitle(position);
    }

    public interface IFragmentGroup{
        Fragment createFragment(int position);
        int getFragmentCount();
        CharSequence getPageTitle(int position);
    }


}
