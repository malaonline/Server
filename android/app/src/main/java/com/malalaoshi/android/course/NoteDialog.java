package com.malalaoshi.android.course;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.MalaContext;
import com.malalaoshi.android.core.base.BaseDialog;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.utils.MiscUtil;

/**
 * 说明dialog
 * Created by tianwei on 5/15/16.
 */
public class NoteDialog extends BaseDialog implements View.OnClickListener {

    public interface OnConfirmClickListener {
        void onClick();
    }

    private OnConfirmClickListener listener;
    private String title;
    private String content;
    private TextView titleView;
    private TextView contentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_note, container, false);
        view.findViewById(R.id.tv_confirm).setOnClickListener(this);
        titleView = (TextView) view.findViewById(R.id.tv_title);
        contentView = (TextView) view.findViewById(R.id.tv_content);
        setTitle(title);
        setContent(content);
        return view;
    }

    @Override
    protected int getWidth() {
        return (int) (super.getWidth() * 0.9);
    }

    @Override
    protected int getHeight() {
        return MiscUtil.dp2px(280);
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        this.title = title;
        if (isAdded() && !isDetached() && EmptyUtils.isNotEmpty(title)) {
            titleView.setText(title);
        }
    }

    public void setTitle(int title) {
        this.title = MalaContext.getContext().getResources().getString(title);
        setTitle(this.title);
    }

    public void setContent(int content) {
        this.content = MalaContext.getContext().getResources().getString(content);
        setContent(this.content);
    }

    public void setContent(String content) {
        this.content = content;
        if (isAdded() && !isDetached() && EmptyUtils.isNotEmpty(content)) {
            contentView.setText(content);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_confirm) {
            dismiss();
            if (listener != null) {
                listener.onClick();
            }
        }
    }
}
