package com.malalaoshi.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseDialogFragment;
import com.malalaoshi.android.util.ImageCache;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by liumengjun on 12/22/15.
 */
public class OrderSuccessDialogFragment extends BaseDialogFragment {
    private static final String TAG = OrderSuccessDialogFragment.class.getSimpleName();

    public static final String ARG_AVATAR = "avatar_url";
    public static final String ARG_COURSE_TIME = "course_time";

    private String mAvatarUrl;
    private String mCourseTime;
    private ImageLoader mImageLoader;

    @Bind(R.id.avatar_view)
    protected ImageView mAvatarView;
    @Bind(R.id.course_time)
    protected TextView mCourseTimeView;
    @Bind(R.id.btn_view_schedule)
    protected Button mBtnViewSchedule;

    public static OrderSuccessDialogFragment newInstance(String avatarUrl, String courseTime) {
        OrderSuccessDialogFragment fragment = new OrderSuccessDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_AVATAR, avatarUrl);
        args.putString(ARG_COURSE_TIME, courseTime);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mAvatarUrl = getArguments().getString(ARG_AVATAR);
            mCourseTime = getArguments().getString(ARG_COURSE_TIME);
        }
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(getActivity()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_order_success, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ButterKnife.bind(this, view);
        if (mAvatarUrl!=null) {
            mImageLoader.get(mAvatarUrl, ImageLoader.getImageListener(mAvatarView, 0, 0));
        }
        mCourseTimeView.setText(mCourseTime);
    }

    @OnClick(R.id.btn_view_schedule)
    protected void onClickBtnViewSchedule() {
        dismiss();
    }
}
