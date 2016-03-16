package com.malalaoshi.android.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.FragmentGroupAdapter;
import com.malalaoshi.android.entity.Cource;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.CourseDetailFragment;
import com.malalaoshi.android.view.Indicator.RubberIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/3/14.
 */
public class CourseDetailDialog extends DialogFragment implements FragmentGroupAdapter.IFragmentGroup, ViewPager.OnPageChangeListener {

    public enum Type {
        NOPASS,
        PASS_NO_COMMENTED,
        PASS_COMMENTED
    }

    public static String ARGS_FRAGEMENT_COURSE = "courses";

    @Bind(R.id.tv_dialog_title)
    protected TextView tvCourseStatus;

    @Bind(R.id.ll_commit)
    protected LinearLayout llCommit;

    @Bind(R.id.tv_cancel)
    protected TextView tvCancel;

    @Bind(R.id.tv_commit)
    protected TextView tvCommit;

    @Bind(R.id.course_viewpager)
    protected ViewPager CourseViewPager;
    private FragmentGroupAdapter fragmentGroupAdapter;

    @Bind(R.id.course_rubber)
    protected RubberIndicator courseRubber;

    private int pagerIndex = 0;
    private List<Cource> listShortCourse;

    //具体数据内容页面
    private Map<Integer, Fragment> fragments = new HashMap<>();

    private CourseDetailDialog(){
    }

    public static CourseDetailDialog newInstance(ArrayList<Cource> courses) {
        CourseDetailDialog f = new CourseDetailDialog();
        Bundle args = new Bundle();
        args.putParcelableArrayList(ARGS_FRAGEMENT_COURSE, courses);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(false);          // 设置点击屏幕Dialog不消失
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        if (getArguments()!=null){
            listShortCourse = getArguments().getParcelableArrayList(ARGS_FRAGEMENT_COURSE);
        }else{
            listShortCourse = new ArrayList<>();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent arg2) {
                // TODO Auto-generated method stub 返回键关闭dialog
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dismiss();
                    return true;
                }
                return false;
            }
        });
        View view = inflater.inflate(R.layout.dialog_course, container, false);
        ButterKnife.bind(this, view);
        initViews();
        initData();
        return view;
    }

    private void initData() {
        loadData();
    }

    private void initViews() {
        fragmentGroupAdapter = new FragmentGroupAdapter(getContext(), getChildFragmentManager(), this);
        CourseViewPager.setAdapter(fragmentGroupAdapter);
        CourseViewPager.addOnPageChangeListener(this);
        if(listShortCourse.size()>1){
            courseRubber.setCount(listShortCourse.size(), 0);
        }else{
            courseRubber.setVisibility(View.GONE);
        }

        Cource cource = listShortCourse.get(0);
        if (!cource.is_passed()){
            setUIType(Type.NOPASS);
        }else{
            if (cource.is_commented()){
                setUIType(Type.PASS_COMMENTED);
            }else{
                setUIType(Type.PASS_NO_COMMENTED);
            }
        }

    }

    private void loadData() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        int width = getResources().getDimensionPixelSize(R.dimen.course_detail_dialog_width);
        int height = getResources().getDimensionPixelSize(R.dimen.course_detail_dialog_height);
        Window window;
        if (getDialog() != null) {
            window = getDialog().getWindow();
        } else {
            // This DialogFragment is used as a normal fragment, not a dialog
            window = getActivity().getWindow();
        }
        if (window != null) {
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = width;
            lp.height = height;//WindowManager.LayoutParams.WRAP_CONTENT;
            lp.gravity = Gravity.CENTER;
            window.setAttributes(lp);
            window.setBackgroundDrawableResource(android.R.color.transparent);
        }
    }


    @Override
    public Fragment createFragment(int position) {
        Fragment fragment = fragments.get(position);
        if (fragment == null) {
            fragment = CourseDetailFragment.newInstance(listShortCourse.get(position).getId() + "");
        }
        fragments.put(position, fragment);
        return fragment;
    }

    @Override
    public int getFragmentCount() {
        return listShortCourse.size();
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pagerIndex = position;
        courseRubber.setFocusPosition(position);
        Cource cource = listShortCourse.get(position);
        if (!cource.is_passed()){
            setUIType(Type.NOPASS);
        }else{
            if (true){
                setUIType(Type.PASS_NO_COMMENTED);
            }else{
                setUIType(Type.PASS_COMMENTED);
            }
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }


    private void setUIType(Type type) {
        switch (type){
            case NOPASS:
                setUINoPass();
                break;
            case PASS_NO_COMMENTED:
                setUIPassNoCommit();
                break;
            case PASS_COMMENTED:
                setUIPassCommit();
                break;
        }
    }

    private void setUIPassCommit() {
        tvCourseStatus.setBackground(getResources().getDrawable(R.drawable.bg_circle_pass));
        tvCourseStatus.setText("已上");
        llCommit.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.GONE);
        tvCommit.setText("查看评价");
    }

    private void setUIPassNoCommit() {
        tvCourseStatus.setBackground(getResources().getDrawable(R.drawable.bg_circle_pass));
        tvCourseStatus.setText("已上");
        llCommit.setVisibility(View.VISIBLE);
        tvCancel.setVisibility(View.GONE);
        tvCommit.setText("去评价");
    }

    private void setUINoPass() {
        tvCourseStatus.setBackground(getResources().getDrawable(R.drawable.bg_circle_unpass));
        tvCourseStatus.setText("待上");
        llCommit.setVisibility(View.GONE);
        tvCancel.setVisibility(View.VISIBLE);
        tvCommit.setText("查看评价");
    }

    @OnClick(R.id.tv_cancel)
    public void onClickCancel(View v){
        dismiss();
    }

    @OnClick(R.id.tv_cancel_dialog)
    public void onClickCancelDailog(View v){
        dismiss();
    }

    @OnClick(R.id.tv_commit)
    public void onClickCommit(View v){
        Cource currentCource = ((CourseDetailFragment) fragmentGroupAdapter.getItem(pagerIndex)).getCource();
        if (currentCource!=null){
            Teacher teacher = currentCource.getTeacher();
            Cource cource = listShortCourse.get(pagerIndex);
            CommentDialog commentDialog = CommentDialog.newInstance(teacher != null ? teacher.getName() : "", teacher != null ? teacher.getAvatar() : "", currentCource.getSubject(), Long.valueOf(currentCource.getId()), currentCource.getComment());
            commentDialog.show(getFragmentManager(), CommentDialog.class.getName());
            dismiss();
        }
    }

}
