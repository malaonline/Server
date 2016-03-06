package com.malalaoshi.android.course;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.entity.CourseDateUI;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Course date choice view
 * Created by tianwei on 3/6/16.
 */
public class CourseDateChoiceView extends LinearLayout {
    public interface OnCourseDateChoicedListener {
        void onCourseDateChoice(String[] times);
    }

    private OnCourseDateChoicedListener listener;
    private List<CourseDateUI> dateList;
    @Bind(R.id.grid_view)
    protected GridView gridView;

    private GridViewAdapter adapter;

    public CourseDateChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_course_date_choice, null);
        addView(view);
        ButterKnife.bind(this, view);
        dateList = new ArrayList<>();
        adapter = new GridViewAdapter(context);
        gridView.setAdapter(adapter);
        initData();
    }

    private void initData() {
        for (int i = 0; i < 35; i++) {
            dateList.add(new CourseDateUI(CourseDateUI.DisplayType.VALID));
        }
        adapter.addAll(dateList);
    }

    public void setOnCourseDateChoicedListener(OnCourseDateChoicedListener listener) {
        this.listener = listener;
    }

    private class GridViewAdapter extends MalaBaseAdapter<CourseDateUI> {
        public GridViewAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            return View.inflate(context, R.layout.view_course_date_choice_item, null);
        }

        @Override
        protected void fillView(int position, final View convertView, final CourseDateUI data) {
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (data.getType() == CourseDateUI.DisplayType.VALID) {
                        convertView.setBackgroundColor(
                                v.getContext().getResources().getColor(R.color.theme_blue_light));
                        data.setType(CourseDateUI.DisplayType.CHOICE);

                    } else if (data.getType() == CourseDateUI.DisplayType.CHOICE) {
                        convertView.setBackgroundColor(Color.TRANSPARENT);
                        data.setType(CourseDateUI.DisplayType.VALID);
                    }
                }
            });
        }

        private void courseChanged() {
            if (CourseDateChoiceView.this.listener == null) {
                return;
            }
            int index = 0;
            List<String> list = new ArrayList<>();
            for (CourseDateUI data : getList()) {

            }
        }

    }
}
