package com.malalaoshi.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;

/**
 * Created by kang on 16/6/29.
 */
public class DefaultView extends RelativeLayout implements View.OnClickListener {

    private TextView tvDefaultTip;
    private TextView tvDefaultOper;
    public DefaultView(Context context) {
        this(context,null);
    }

    public DefaultView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public DefaultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view  = LayoutInflater.from(context).inflate(R.layout.view_default, this);
        tvDefaultTip = (TextView) findViewById(R.id.tv_default_tip);
        tvDefaultOper = (TextView) findViewById(R.id.tv_default_oper);
        tvDefaultOper.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {

    }

    public void setText(String text) {
        tvDefaultTip.setText(text);
    }

    public void setButtonText(String text) {
        tvDefaultOper.setText(text);
    }

    public void setImage(int rid) {
        tvDefaultTip.setCompoundDrawables(null,getResources().getDrawable(rid),null,null);
    }
}
