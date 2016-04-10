package com.malalaoshi.android.core.base;


import android.support.v4.app.Fragment;

import com.malalaoshi.android.core.stat.StatReporter;

/**
 * Base fragment
 * Created by tianwei on 3/5/16.
 */
public abstract class BaseFragment extends Fragment {
    public abstract String getStatName();

    @Override
    public void onResume() {
        super.onResume();
        StatReporter.onResume(getStatName());
    }

    @Override
    public void onPause() {
        super.onPause();
        StatReporter.onPause();
    }
}
