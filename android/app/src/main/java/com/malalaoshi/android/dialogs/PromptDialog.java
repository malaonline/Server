package com.malalaoshi.android.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/4/12.
 */
public class PromptDialog extends DialogFragment implements View.OnClickListener  {
    private static String ARGS_DIALOG_TYPE_KET = "dialog type";
    private static String ARGS_MESSAGE_TEXT_KET = "message";
    private static String ARGS_LEFT_TEXT_KET = "left text";
    private static String ARGS_RIGHT_TEXT_KET = "right text";
    private static String ARGS_DRAWABLE_ID_KET = "drawable id";
    private static String ARGS_CANCEL_TEXT_KET = "cancel text";
    private static String ARGS_CANCANCEL_KET = "cancelable";
    private static String ARGS_CANBACK_KET = "back";
    private static int ONE_BUTTON = 0;
    private static int TWO_BUTTON = 1;

    public interface OnCloseListener {
        void onLeftClick();

        void onRightClick();
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    @Bind(R.id.icon_view)
    protected ImageView iconView;

    @Bind(R.id.tv_message)
    protected TextView tvMeaasge;

    @Bind(R.id.btn_left)
    protected TextView leftView;

    @Bind(R.id.btn_right)
    protected TextView rightView;

    @Bind(R.id.btn_close)
    protected TextView btnClose;

    @Bind(R.id.ll_close)
    LinearLayout llClose;

    private OnCloseListener listener;

    private OnDismissListener onDismissListener;

    private boolean canBack = false;

    public static PromptDialog newInstance(int drawableId, String message, String leftText, String rightText, boolean cancelable, boolean backable) {
        PromptDialog f = new PromptDialog();
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_TYPE_KET, TWO_BUTTON);
        args.putInt(ARGS_DRAWABLE_ID_KET, drawableId);
        args.putString(ARGS_MESSAGE_TEXT_KET,message);
        args.putString(ARGS_LEFT_TEXT_KET,leftText);
        args.putString(ARGS_RIGHT_TEXT_KET,rightText);
        args.putBoolean(ARGS_CANCANCEL_KET,cancelable);
        args.putBoolean(ARGS_CANBACK_KET,backable);
        f.setArguments(args);
        return f;
    }

    public static PromptDialog newInstance(int drawableId, String message, String btnText, boolean cancelable, boolean backable) {
        PromptDialog f = new PromptDialog();
        Bundle args = new Bundle();
        args.putInt(ARGS_DIALOG_TYPE_KET, ONE_BUTTON);
        args.putInt(ARGS_DRAWABLE_ID_KET, drawableId);
        args.putString(ARGS_MESSAGE_TEXT_KET,message);
        args.putString(ARGS_CANCEL_TEXT_KET,btnText);
        args.putBoolean(ARGS_CANCANCEL_KET,cancelable);
        args.putBoolean(ARGS_CANBACK_KET,backable);
        f.setArguments(args);
        return f;
    }


    public void setOnCloseListener(OnCloseListener listener) {
        this.listener = listener;
    }
    public void setDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
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
        Bundle args = getArguments();
        if (args!=null) {
            setCancelable(args.getBoolean(ARGS_CANCANCEL_KET,false));
            canBack = args.getBoolean(ARGS_CANBACK_KET,false);
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
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                // TODO Auto-generated method stub 返回键关闭dialog
                if (keyCode == KeyEvent.KEYCODE_BACK&&canBack) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        View view = inflater.inflate(R.layout.dialog_prompt, container, false);
        ButterKnife.bind(this, view);
        Bundle args = getArguments();
        if (args!=null){
            int type = args.getInt(ARGS_DIALOG_TYPE_KET);
            iconView.setImageDrawable(getResources().getDrawable(args.getInt(ARGS_DRAWABLE_ID_KET,R.mipmap.ic_launcher)));
            tvMeaasge.setText(args.getString(ARGS_MESSAGE_TEXT_KET,"message"));
            if (type==TWO_BUTTON){
                btnClose.setVisibility(View.GONE);
                llClose.setVisibility(View.VISIBLE);
                leftView.setText(args.getString(ARGS_LEFT_TEXT_KET,""));
                rightView.setText(args.getString(ARGS_RIGHT_TEXT_KET,""));
                leftView.setOnClickListener(this);
                rightView.setOnClickListener(this);
            }else{
                llClose.setVisibility(View.GONE);
                btnClose.setVisibility(View.VISIBLE);
                btnClose.setText(args.getString(ARGS_CANCEL_TEXT_KET,""));
                btnClose.setOnClickListener(this);
            }
        }
        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_left:
            {
                dismiss();
                if (listener != null) {
                    listener.onLeftClick();
                }
            }
            break;
            case R.id.btn_right:
            {
                dismiss();
                if (listener != null){
                    listener.onRightClick();
                }
            }
            break;
            case R.id.btn_close:
            {
                dismiss();
                if (onDismissListener != null){
                    onDismissListener.onDismiss();
                }
            }
            break;
        }

    }
}
