package com.malalaoshi.android.pay;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * PayResult dialog
 * Created by tianwei on 2/28/16.
 */
public class PayTimeAllocateDialog extends DialogFragment implements View.OnClickListener {

    public interface OnCloseListener {
        void onLeftClick();

        void onRightClick();
    }

    @Bind(R.id.btn_left)
    protected View leftView;

    @Bind(R.id.tv_right)
    protected View rightView;

    private OnCloseListener listener;

    public void setOnDismissListener(OnCloseListener listener) {
        this.listener = listener;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // fix a nasty android support pacakge bug, see:
        // http://stackoverflow.com/questions/7469082/getting-exception-illegalstateexception
        // -can-not-perform-this-action-after-onsa
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog);
        setCancelable(true);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_left) {
            dismiss();
            if (listener != null) {
                listener.onLeftClick();
            }
        } else if (v.getId() == R.id.btn_right) {
            dismiss();
            listener.onRightClick();
        }
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
            int width = getDialogWidth();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = width;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    public int getDialogWidth() {
        return (int) (Math.min(getDisplayMetrics().widthPixels, getDisplayMetrics().heightPixels) * 0.9);
    }

    public static DisplayMetrics getDisplayMetrics() {
        return MalaApplication.getInstance().getApplicationContext().getResources().getDisplayMetrics();
    }

    @Override
    public void dismiss() {
        if (getFragmentManager() != null) {
            // Avoid NPE
            super.dismiss();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_pay_result, container, false);
        ButterKnife.bind(this, view);
        leftView.setOnClickListener(this);
        rightView.setOnClickListener(this);
        return view;
    }
}
