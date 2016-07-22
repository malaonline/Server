package com.malalaoshi.android.dialog;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.FragmentGroupAdapter;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.fragments.FilterGradeFragment;
import com.malalaoshi.android.fragments.FilterSubjectFragment;
import com.malalaoshi.android.fragments.FilterTagFragment;
import com.malalaoshi.android.view.SlideViewPager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.relex.circleindicator.CircleIndicator;

/**
 * Created by kang on 16/6/20.
 */
public class MultiSelectFilterDialog  extends DialogFragment implements FragmentGroupAdapter.IFragmentGroup, View.OnClickListener,
        FilterGradeFragment.OnGradeClickListener,
        FilterSubjectFragment.OnSubjectClickListener,
        FilterTagFragment.OnTagClickListener {
    private Map<Integer,Fragment> fragments = new HashMap<>();
    private int pageIndex = 0;

    @Bind(R.id.filter_viewpager)
    protected SlideViewPager viewPager;

    private FragmentGroupAdapter fragmentGroupAdapter;

    @Bind(R.id.filter_bar_left)
    protected ImageView ivLeft;

    private Drawable lefrDrawable = null;

    @Bind(R.id.filter_bar_right)
    protected ImageView ivRight;

    private Drawable rightDrawable = null;

    @Bind(R.id.iv_dialog_ic)
    protected ImageView ivTitleIcon;
    private Drawable titleDrawable = null;

    @Bind(R.id.filter_bar_title)
    protected TextView tvTitle;

    @Bind(R.id.circle_indicator)
    CircleIndicator circleIndicator;

    private String titleText = "年级筛选";

    private OnLeftClickListener  leftClickListener;
    private OnRightClickListener rightClickListener;

    private Grade grade;
    private Subject subject;
    private ArrayList<Tag> tags;

    public MultiSelectFilterDialog() {
    }

    public static MultiSelectFilterDialog newInstance() {
        return new MultiSelectFilterDialog();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_rounded_corners);
        View view = inflater.inflate(R.layout.dialog_multiselect_filter_layout, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.filter_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.filter_dialog_height);
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    private void initView() {

        //viewpager不可侧滑
        viewPager.setCanSlide(false);

        if (lefrDrawable !=null){
            ivLeft.setImageDrawable(lefrDrawable);
        }

        if (rightDrawable !=null){
            ivRight.setImageDrawable(rightDrawable);
        }

        if (titleDrawable !=null){
            ivTitleIcon.setImageDrawable(titleDrawable);
        };

        tvTitle.setText(titleText);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        fragmentGroupAdapter = new FragmentGroupAdapter(getContext(),getChildFragmentManager(),this);
        viewPager.setAdapter(fragmentGroupAdapter);
        viewPager.setOffscreenPageLimit(3);//缓存页面
        circleIndicator.setViewPager(viewPager);

    }

    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            switch (position){
                case 0:
                    fragment = FilterGradeFragment.newInstance();
                    ((FilterGradeFragment)fragment).setOnGradeClickListener(this);
                    break;
                case 1:
                    fragment = FilterSubjectFragment.newInstance();
                    ((FilterSubjectFragment)fragment).setOnSubjectClickListener(this);
                    break;
                case 2:
                    fragment = FilterTagFragment.newInstance();
                    ((FilterTagFragment)fragment).setOnTagClickListener(this);
                    break;
            }
        }
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getFragmentCount() {
        return 3;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.filter_bar_left:
                if (pageIndex==0){
                    dismiss();
                }
                moveFragmentToLeft();
                if (leftClickListener!=null){
                    leftClickListener.OnLeftClick(v);
                }
                break;
            case R.id.filter_bar_right:
                if (pageIndex==fragmentGroupAdapter.getCount()-1){
                    dismiss();
                }
                if (rightClickListener!=null){
                    rightClickListener.OnRightClick(v, grade, subject, tags);
                }
                break;
        }

    }

    protected void moveFragmentToLeft() {
        if (pageIndex>0){
            pageIndex--;
            viewPager.setCurrentItem(pageIndex);
            updateView(pageIndex);
        }
    }

    protected void moveFragmentToRight(){
        if (pageIndex<fragmentGroupAdapter.getCount()-1){
            pageIndex++;
            viewPager.setCurrentItem(pageIndex);
            updateView(pageIndex);
        }
    }

    protected void updateView(int index){
        if (index==0){
            ivLeft.setImageDrawable(getResources().getDrawable(R.drawable.close_btn));
            ivTitleIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_grade_dialog));
            ivRight.setVisibility(View.GONE);
            tvTitle.setText("筛选年级");
        } else if (index==1){
            ivRight.setVisibility(View.GONE);
            ivLeft.setImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
            ivTitleIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_subject_dialog));
            tvTitle.setText("筛选科目");
        } else{
            ivRight.setVisibility(View.VISIBLE);
            ivLeft.setImageDrawable(getResources().getDrawable(R.drawable.core__back_btn));
            ivTitleIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_tag_dialog));
            tvTitle.setText("筛选风格");
        }
    }

    public void setOnLeftClickListener(OnLeftClickListener listener){
        leftClickListener = listener;
    }

    public void setOnRightClickListener(OnRightClickListener listener){
        rightClickListener = listener;
    }

    @Override
    public void onGradeClick(Grade grade) {
        if (grade!=null){
            this.grade = grade;
        }
        moveFragmentToRight();
    }

    @Override
    public void onSubjectClick(Subject subject) {
        if (subject!=null){
            this.subject = subject;
        }
        moveFragmentToRight();
    }

    @Override
    public void onTagClick(ArrayList<Tag> tags) {
        if (tags!=null){
            this.tags = tags;
        }
    }

    public interface OnLeftClickListener{
        void OnLeftClick(View v);
    }

    public interface OnRightClickListener{
        void OnRightClick(View v, Grade grade, Subject subject, ArrayList<Tag> tags);
    }
}
