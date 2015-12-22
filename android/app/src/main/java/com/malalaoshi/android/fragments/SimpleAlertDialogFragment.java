package com.malalaoshi.android.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liumengjun on 12/16/15.
 */
public class SimpleAlertDialogFragment extends DialogFragment {

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
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return dialog;
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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        int width = getResources().getDimensionPixelSize(R.dimen.dialog_width);
        window.setLayout(width, window.getAttributes().height);//Here!
    }

    @OnClick(R.id.btn_ok)
    protected void onClickBtnOk() {
        dismiss();
    }
}
