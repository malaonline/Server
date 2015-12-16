package com.malalaoshi.android.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by liumengjun on 12/16/15.
 */
public class SimpleAlertDialogFragment extends DialogFragment {
    public static SimpleAlertDialogFragment newInstance(String title, String msg, String buttonText) {
        SimpleAlertDialogFragment frag = new SimpleAlertDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putString("message", msg);
        args.putString("buttonText", buttonText);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        String message = getArguments().getString("message");
        String buttonText = getArguments().getString("buttonText");
        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
        if (title!=null && !title.isEmpty()) {
            db.setTitle(title);
        }
        db.setMessage(message);
        if (buttonText==null || buttonText.isEmpty()) {
            buttonText = "OK";
        }
        db.setPositiveButton(buttonText,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                }
        );
        return db.create();
    }
}
