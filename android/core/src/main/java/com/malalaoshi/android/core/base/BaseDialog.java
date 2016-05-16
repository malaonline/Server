package com.malalaoshi.android.core.base;

/**
 * Base dialog
 * Created by tianwei on 5/15/16.
 */

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.malalaoshi.android.core.utils.DialogUtils;

/**
 * Base fragment dialog
 * Created by tianwei on 16-3-29.
 */
public class BaseDialog extends DialogFragment {

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
    }

    @Override
    public int show(FragmentTransaction ft, String tag) {
        if (ft != null) {
            return super.show(ft, tag);
        }
        return -1;
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window;
        if (getDialog() != null) {
            window = getDialog().getWindow();
        } else {
            // This DialogFragment is used as a normal fragment, not a dialog
            window = getActivity().getWindow();
        }
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = getWidth();
            lp.height = getHeight();
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    protected int getHeight() {
        return WindowManager.LayoutParams.WRAP_CONTENT;
    }

    protected int getWidth() {
        return DialogUtils.getDialogWidth();
    }

    @Override
    public void dismiss() {
        // Avoid NPE
        if (getFragmentManager() != null) {
            try {
                super.dismiss();
            } catch (Exception e) {
                Log.d("AddCarRemindDialog", "dismiss dialog error");
            }
        }
    }
}
