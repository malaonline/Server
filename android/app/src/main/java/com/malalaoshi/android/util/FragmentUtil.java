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
        /*FragmentTransaction tx = fragmentManager.beginTransaction();
        if(pre != null){
            tx.hide(pre)
            .add(containerViewId, newFragment, fragmentTag)
            .addToBackStack(null);
        }else{
            tx.replace(containerViewId, newFragment, fragmentTag);
        }
        tx.commit();*/

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (pre!=null){
            if (!newFragment.isAdded()) {    // 先判断是否被add过
                transaction.hide(pre).add(containerViewId, newFragment).commit(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(pre).show(newFragment).commit(); // 隐藏当前的fragment，显示下一个
            }
        }else{
            transaction.add(containerViewId, newFragment).commit(); // add下一个到Activity中
        }



    }
    public static void opFragmentMainActivity(FragmentManager fragmentManager, Fragment pre, Fragment newFragment, String fragmentTag){
        FragmentUtil.openFragment(R.id.content_layout, fragmentManager, pre, newFragment, fragmentTag);
    }
}
