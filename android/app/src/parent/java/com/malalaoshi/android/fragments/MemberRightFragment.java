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
public class MemberRightFragment extends Fragment {

    private int aMemberPicID;
    private String aMemberSort;
    private String aMemberDetail;

    public static final String MEMBER_SORT = "member_sort";
    public static final String MEMBER_DETAIL = "menber_detail";
    public static final String MEMBER_PICID = "menber_picid";

    @Bind(R.id.iv_member_pic)
    protected ImageView memberPicture;

    @Bind(R.id.tv_member_sort)
    protected TextView memberSort;

    @Bind(R.id.tv_member_detail)
    protected TextView memberDetail;

    public static MemberRightFragment newInstance(int memberPicID,String memberSort,String memberDetail){
        MemberRightFragment fragment = new MemberRightFragment();
        Bundle args = new Bundle();
        args.putInt(MEMBER_PICID,memberPicID);
        args.putString(MEMBER_SORT,memberSort);
        args.putString(MEMBER_DETAIL,memberDetail);
        fragment.setArguments(args);
        return fragment;

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_member_right,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this,view);
        setDetail();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            aMemberPicID = getArguments().getInt(MEMBER_PICID);
            aMemberSort = getArguments().getString(MEMBER_SORT);
            aMemberDetail = getArguments().getString(MEMBER_DETAIL);
        }
    }

    private void setDetail() {
        memberPicture.setImageResource(aMemberPicID);
        memberSort.setText(aMemberSort);
        memberDetail.setText(aMemberDetail);
    }
}
