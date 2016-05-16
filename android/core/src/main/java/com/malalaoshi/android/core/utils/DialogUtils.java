package com.malalaoshi.android.core.utils;

import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

/**
 * DialogUtils
 * Created by tianwei on 5/15/16.
 */
public class DialogUtils {

    private DialogUtils() {
    }

    public static int getDialogWidth() {
        return (int) ((float) Math.min(MiscUtil.getScreenWidth(), MiscUtil.getScreenHeight()));
    }

    public static void showDialog(FragmentManager fm, DialogFragment dialog, String dialogTag) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        try {
            dialog.show(ft, dialogTag);
        } catch (Exception e) {
            Log.e("Mala", "show dialog error: " + e.getMessage());
        }
    }

    public static void dismissDialog(FragmentManager fm, String dialogTag) {
        FragmentTransaction ft = fm.beginTransaction();
        Fragment prev = fm.findFragmentByTag(dialogTag);
        if (prev != null) {
            ft.remove(prev);
        }
        try {
            ft.commitAllowingStateLoss();
        } catch (Exception e) {
            Log.e("Mala", "show dialog error: " + e.getMessage());
        }
    }
}

