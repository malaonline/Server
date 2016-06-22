package com.malalaoshi.android.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.fragments.FilterGradeFragment;
import com.malalaoshi.android.fragments.FilterSubjectFragment;
import com.malalaoshi.android.fragments.FilterTagFragment;
import com.malalaoshi.android.util.FragmentUtil;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/6/20.
 */
public class RadioFilterDialog   extends DialogFragment implements View.OnClickListener {
    public static String ARGS_FILTER_TYPE = "filter type";
    public static int FILTER_TYPE_GRADE = 0;
    public static int FILTER_TYPE_SUBJECT = 1;
    public static int FILTER_TYPE_TAGS = 2;

    public static String ARGS_FILTER_TYPE_GRADE = "grade";
    public static String ARGS_FILTER_TYPE_SUBJECT = "subject";
    public static String ARGS_FILTER_TYPE_TAGS = "tags";

    private int filterType = 0;
    private Fragment filterFragment;

    @Bind(R.id.filter_bar_left)
    protected ImageView ivLeft;


    @Bind(R.id.filter_bar_right)
    protected ImageView ivRight;

    @Bind(R.id.iv_dialog_ic)
    protected ImageView ivTitleIcon;

    @Bind(R.id.filter_bar_title)
    protected TextView tvTitle;

    private OnLeftClickListener  leftClickListener;
    private OnRightClickListener rightClickListener;

    private FilterGradeFragment.OnGradeClickListener onGradeClickListener;
    private FilterSubjectFragment.OnSubjectClickListener onSubjectClickListener;
    private FilterTagFragment.OnTagClickListener onTagClickListener;

    private Grade grade;
    private Subject subject;
    private ArrayList<Tag> tags;

    public RadioFilterDialog() {
    }

    public static RadioFilterDialog newInstance(Grade grade,FilterGradeFragment.OnGradeClickListener onGradeClickListener) {
        RadioFilterDialog radioFilterDialog = new RadioFilterDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_FILTER_TYPE,FILTER_TYPE_GRADE);
        bundle.putParcelable(ARGS_FILTER_TYPE_GRADE,grade);
        radioFilterDialog.setArguments(bundle);
        radioFilterDialog.setOnGradeClickListener(onGradeClickListener);
        return radioFilterDialog;
    }

    public static RadioFilterDialog newInstance(Grade grade, Subject subject,FilterSubjectFragment.OnSubjectClickListener onSubjectClickListener) {
        RadioFilterDialog radioFilterDialog = new RadioFilterDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_FILTER_TYPE,FILTER_TYPE_SUBJECT);
        bundle.putParcelable(ARGS_FILTER_TYPE_SUBJECT,subject);
        bundle.putParcelable(ARGS_FILTER_TYPE_GRADE,grade);
        radioFilterDialog.setArguments(bundle);
        radioFilterDialog.setOnSubjectClickListener(onSubjectClickListener);
        return radioFilterDialog;
    }

    public static RadioFilterDialog newInstance(ArrayList<Tag> tags,FilterTagFragment.OnTagClickListener onTagClickListener) {
        RadioFilterDialog radioFilterDialog = new RadioFilterDialog();
        Bundle bundle = new Bundle();
        bundle.putInt(ARGS_FILTER_TYPE,FILTER_TYPE_TAGS);
        bundle.putParcelableArrayList(ARGS_FILTER_TYPE_TAGS,tags);
        radioFilterDialog.setArguments(bundle);
        radioFilterDialog.setOnTagClickListener(onTagClickListener);
        return radioFilterDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
        Bundle bundle = getArguments();
        if (bundle!=null){
            init(bundle);
        }
    }

    private void init(Bundle bundle) {
        filterType = bundle.getInt(ARGS_FILTER_TYPE);
        if (filterType == FILTER_TYPE_GRADE){
            grade = bundle.getParcelable(ARGS_FILTER_TYPE_GRADE);
        }else if (filterType == FILTER_TYPE_SUBJECT){
            grade = bundle.getParcelable(ARGS_FILTER_TYPE_GRADE);
            subject = bundle.getParcelable(ARGS_FILTER_TYPE_SUBJECT);
        }else if (filterType == FILTER_TYPE_TAGS){
            tags = bundle.getParcelableArrayList(ARGS_FILTER_TYPE_TAGS);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.bg_rounded_corners);
        View view = inflater.inflate(R.layout.dialog_radio_filter_layout, container, false);
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

        if (filterType == FILTER_TYPE_GRADE){
            Long gradeId = null;
            if (grade!=null){
                gradeId = grade.getId();
            }
            filterFragment = FilterGradeFragment.newInstance(gradeId);
            ((FilterGradeFragment)filterFragment).setOnGradeClickListener(onGradeClickListener);
            FragmentUtil.openFragment(R.id.fl_content, getChildFragmentManager(), null, filterFragment, FilterGradeFragment.class.getSimpleName());
            updateView(View.GONE,R.drawable.ic_grade_dialog,"筛选年级");
        }else if (filterType == FILTER_TYPE_SUBJECT){
            Long gradeId = null;
            if (grade!=null){
                gradeId = grade.getId();
            }
            Long subjectId = null;
            if (grade!=null){
                subjectId = subject.getId();
            }
            filterFragment = FilterSubjectFragment.newInstance(gradeId,subjectId);
            ((FilterSubjectFragment)filterFragment).setOnSubjectClickListener(onSubjectClickListener);
            FragmentUtil.openFragment(R.id.fl_content, getChildFragmentManager(), null, filterFragment, FilterSubjectFragment.class.getSimpleName());
            updateView(View.GONE,R.drawable.ic_subject_dialog,"筛选科目");
        }else if (filterType == FILTER_TYPE_TAGS){
            long[] tagIds = null;
            if (tags!=null&&tags.size()>0){
                tagIds = new long[tags.size()];
                int i=0;
                for (Tag tag:tags){
                    tagIds[i] = tag.getId();
                    i++;
                }
            }
            filterFragment = FilterTagFragment.newInstance(tagIds);
            ((FilterTagFragment)filterFragment).setOnTagClickListener(onTagClickListener);
            FragmentUtil.openFragment(R.id.fl_content, getChildFragmentManager(), null, filterFragment, FilterTagFragment.class.getSimpleName());
            updateView(View.VISIBLE,R.drawable.ic_tag_dialog,"筛选风格");
        }
        ivLeft.setOnClickListener(this);
        ivRight.setOnClickListener(this);
    }

    protected void updateView(int rightVisibility, int rightDrawResId, String title){
        ivLeft.setVisibility(View.GONE);
        ivTitleIcon.setImageDrawable(getResources().getDrawable(rightDrawResId));
        ivRight.setVisibility(rightVisibility);
        tvTitle.setText(title);
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

    public void setOnTagClickListener(FilterTagFragment.OnTagClickListener onTagClickListener) {
        this.onTagClickListener = onTagClickListener;
    }

    public void setOnGradeClickListener(FilterGradeFragment.OnGradeClickListener onGradeClickListener) {
        this.onGradeClickListener = onGradeClickListener;
    }

    public void setOnSubjectClickListener(FilterSubjectFragment.OnSubjectClickListener onSubjectClickListener) {
        this.onSubjectClickListener = onSubjectClickListener;
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
