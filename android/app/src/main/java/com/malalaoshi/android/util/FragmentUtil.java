package com.malalaoshi.android.util;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

import com.malalaoshi.android.R;

/**
 * Created by zl on 15/11/30.
 */
public final class FragmentUtil{
    public static void openFragment(int containerViewId,FragmentManager fragmentManager, Fragment pre, Fragment newFragment, String fragmentTag){
        FragmentTransaction tx = fragmentManager.beginTransaction();
        if(pre != null){
            tx.hide(pre)
            .add(containerViewId, newFragment, fragmentTag)
            .addToBackStack(null);
        }else{
            tx.replace(containerViewId, newFragment, fragmentTag);
        }
        tx.commit();
    }
    public static void opFragmentMainActivity(FragmentManager fragmentManager, Fragment pre, Fragment newFragment, String fragmentTag){
        FragmentUtil.openFragment(R.id.content_layout, fragmentManager, pre, newFragment, fragmentTag);
    }
}
