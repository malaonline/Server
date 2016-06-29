package com.malalaoshi.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.malalaoshi.android.R;

/**
 * Created by kang on 16/6/29.
 */
public class ListDefaultView extends LinearLayout {

    private TextView txtView;
    private ImageView imageView;

    public ListDefaultView(Context context) {
        this(context,null);
    }

    public ListDefaultView(Context context, AttributeSet attrs) {
        this(context, attrs,-1);
    }

    public ListDefaultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view  = LayoutInflater.from(context).inflate(R.layout.view_default_list, this);
        initView(view);
    }


    private void initView(View view) {
        txtView = (TextView) view.findViewById(R.id.tv_error_txt);
        imageView = (ImageView) view.findViewById(R.id.iv_error_img);
    }

    public void setText(int rid) {
        txtView.setText(rid);
    }

    public void setText(String text) {
        txtView.setText(text);
    }

    public void setImage(int rid) {
        imageView.setImageResource(rid);
    }
}
