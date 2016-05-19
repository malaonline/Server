package com.malalaoshi.android.fragments;



import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by liwei on 16/5/17.
 */
public class MemberCounselingFragment extends Fragment {

    @Bind(R.id.iv_member_pic)
    protected ImageView memberPicture;

    @Bind(R.id.tv_member_sort)
    protected TextView memberSort;

    @Bind(R.id.tv_member_detail)
    protected TextView memberDetail;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_right,container,false);
        ButterKnife.bind(this,view);
        setDetail();
        return view;
    }


    private void setDetail() {
        memberPicture.setImageResource(R.drawable.ic_member_counseling);
        memberSort.setText("心理辅导");
        memberDetail.setText("免费获得专业心理咨询师1对1心理辅导,促进学员身心健康成长");
    }
}
