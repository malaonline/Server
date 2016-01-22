package com.malalaoshi.android.dialog;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/1/20.
 */
public class Filterdialog extends DialogFragment implements FragmentGroupAdapter.IFragmentGroup, View.OnClickListener {
    public static String ARGMENTS_DIALOG_WIDTH = "dialog width";
    private int width;
    public static String ARGMENTS_DIALOG_HEIGHT = "dialog height";
    private int height;
    private List<Fragment> fragments;
    public static String ARGMENTS_DIALOG_PAGEINDEX = "pageIndex";
    private int pageIndex;

    @Bind(R.id.filter_viewpager)
    protected ViewPager viewPager;

    @Bind(R.id.filter_bar_left)
    protected ImageView ivLeft;
    private int initLeftVisiable = View.GONE;
    private boolean initLeftVisibleChange = false;
    private Drawable initLefrDrawable = null;

    @Bind(R.id.filter_bar_right)
    protected ImageView ivRight;
    private int initRightVisiable = View.GONE;
    private boolean initRightVisibleChange = false;
    private Drawable initRightDrawable  = null;

    @Bind(R.id.iv_dialog_ic)
    protected ImageView ivTitleIcon;
    private Drawable initTitleDrawable = null;

    @Bind(R.id.filter_bar_title)
    protected TextView tvTitle;
    private String titleText = "年级筛选";

    private OnLeftClickListener  leftClickListener;
    private OnRightClickListener rightClickListener;

    public Filterdialog() {
    }

    public void setFragments(List<Fragment> fragments) {
        if (fragments==null) throw new NullPointerException("fragments is null");
        this.fragments = fragments;
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
        View view = inflater.inflate(R.layout.dialog_filter_layout, container, false);
        ButterKnife.bind(this, view);
        width = getArguments().getInt(ARGMENTS_DIALOG_WIDTH,400);
        height = getArguments().getInt(ARGMENTS_DIALOG_HEIGHT, 500);
        pageIndex = getArguments().getInt(ARGMENTS_DIALOG_PAGEINDEX,0);

        if (initLeftVisibleChange){
            ivLeft.setVisibility(initLeftVisiable);
        }
        if (initLefrDrawable!=null){
            ivLeft.setImageDrawable(initLefrDrawable);
        }
        if (initRightVisibleChange){
            ivRight.setVisibility(initRightVisiable);
        }
        if (initRightDrawable !=null){
            ivRight.setImageDrawable(initRightDrawable);
        }
        if (initTitleDrawable !=null){
            ivTitleIcon.setImageDrawable(initTitleDrawable);
        };
        tvTitle.setText(titleText);
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
        viewPager.setAdapter(new FragmentGroupAdapter(getContext(),getChildFragmentManager(),this));
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        window.setLayout(width, height);
    }

    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getFragmentCount() {
        return fragments.size();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
        case R.id.filter_bar_left:
            if (leftClickListener!=null){
                leftClickListener.OnLeftClick(v);
            }
            break;
        case R.id.filter_bar_right:
            if (rightClickListener!=null){
                rightClickListener.OnRightClick(v);
            }
            break;
        }

    }

    public void setTitleText(CharSequence text){
        if (tvTitle==null){
            titleText = (String) text;
        }else{
            tvTitle.setText(text);
        }
    }

    public void setTileIconImageDrawable(Drawable drawable){
        if (ivTitleIcon==null){
            initTitleDrawable = drawable;
        }
        else{
            ivTitleIcon.setImageDrawable(drawable);
        }
    }


    public void setLeftBtnVisable(int visibility){
        if (ivLeft==null){
            initLeftVisibleChange = true;
            initLeftVisiable = visibility;
        }else{
            ivLeft.setVisibility(visibility);
        }
    }

    public void setRightBtnVisable(int visibility){
        if (ivRight==null){
            initRightVisibleChange = true;
            initRightVisiable = visibility;
        }else{
            ivRight.setVisibility(visibility);
        }
    }

    public void setLeftBtnImageDrawable(Drawable drawable){
        if (ivLeft==null){
            initLefrDrawable = drawable;
        }
        else{
            ivLeft.setImageDrawable(drawable);
        }
    }

    public void setRightBtnImageDrawable(Drawable drawable){
        if (ivRight==null){
            initRightDrawable = drawable;
        }
        else{
            ivRight.setImageDrawable(drawable);
        }
    }

    public void setOnLeftClickListener(OnLeftClickListener listener){
        leftClickListener = listener;
    }

    public void setOnRightClickListener(OnRightClickListener listener){
        rightClickListener = listener;
    }

    public interface OnLeftClickListener{
        void OnLeftClick(View v);
    }

    public interface OnRightClickListener{
        void OnRightClick(View v);
    }
}

class FragmentGroupAdapter extends FragmentPagerAdapter {
    private IFragmentGroup fragment;

    public FragmentGroupAdapter(Context context, FragmentManager fm, IFragmentGroup fragment) {
        super(fm);
        this.fragment = fragment;
    }

    @Override
    public Fragment getItem(int position) {
        return fragment.createFragment(position);
    }

    @Override
    public int getCount() {
        return fragment.getFragmentCount();
    }


    public interface IFragmentGroup{
        Fragment createFragment(int position);
        int getFragmentCount();
    }
}