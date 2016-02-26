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
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * PayResult dialog
 * Created by tianwei on 2/28/16.
 */
public class PayResultDialog extends DialogFragment implements View.OnClickListener {

    public enum Type {
        PAY_SUCCESS,
        CANCEL,
        INVALID,
        PAY_FAILED
    }

    private Type type;

    @Bind(R.id.icon_view)
    protected ImageView iconView;

    @Bind(R.id.tv_description)
    protected TextView desView;

    @Bind(R.id.tv_confirm)
    protected TextView okView;

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

    public void setType(Type type) {
        this.type = type;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_confirm) {
            dismiss();
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
        okView.setOnClickListener(this);
        switch (type) {
            case PAY_FAILED:
                iconView.setImageResource(R.drawable.ic_pay_failed);
                desView.setText("支付失败，请重试!");
                break;
            case PAY_SUCCESS:
                iconView.setImageResource(R.drawable.ic_pay_success);
                desView.setText("恭喜您支付成功！您的课表已经安排好，快去查看吧！");
                break;
            case INVALID:
                iconView.setImageResource(R.drawable.ic_pay_failed);
                desView.setText("微信支付要先安装微信");
                break;
            case CANCEL:
                iconView.setImageResource(R.drawable.ic_pay_failed);
                desView.setText("支付用户已取消");
                break;
        }
        return view;
    }
}
