package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseDialogFragment;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liumengjun on 12/16/15.
 */
public class SimpleAlertDialogFragment extends BaseDialogFragment {

    @Bind(R.id.message)
    protected TextView messageView;
    @Bind(R.id.tv_ok)
    protected TextView tvOk;
    @Bind(R.id.iv_dlg_icon)
    protected ImageView ivDlgIcon;

    public static SimpleAlertDialogFragment newInstance(String msg, String buttonText, int resIconId) {
        SimpleAlertDialogFragment frag = new SimpleAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", msg);
        args.putString("buttonText", buttonText);
        args.putInt("icon", resIconId);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_simple, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        String message = getArguments().getString("message");
        String buttonText = getArguments().getString("buttonText");
        int  resId = getArguments().getInt("icon");
        ivDlgIcon.setImageDrawable(getResources().getDrawable(resId));
        messageView.setText(message);
        tvOk.setText(buttonText);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.alter_dialog_message_width);
        int height = getResources().getDimensionPixelSize(R.dimen.alter_dialog_message_height);
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    @OnClick(R.id.tv_ok)
    protected void onClickBtnOk() {
        dismiss();
    }
}
