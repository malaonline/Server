package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
    @Bind(R.id.btn_ok)
    protected Button btnOk;

    public static SimpleAlertDialogFragment newInstance(String msg, String buttonText) {
        SimpleAlertDialogFragment frag = new SimpleAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", msg);
        args.putString("buttonText", buttonText);
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
        messageView.setText(message);
        btnOk.setText(buttonText);
    }

    @OnClick(R.id.btn_ok)
    protected void onClickBtnOk() {
        dismiss();
    }
}
