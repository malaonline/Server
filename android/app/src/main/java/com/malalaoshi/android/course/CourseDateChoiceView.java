package com.malalaoshi.android.course;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.entity.CourseDateEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Course date choice view
 * Created by tianwei on 3/6/16.
 */
public class CourseDateChoiceView extends LinearLayout {

    public interface OnCourseDateChoiceListener {
        void onCourseDateChoice(List<CourseDateEntity> sections);
    }

    public interface onCourseNoteClickListener {
        void onClick();
    }

    private OnCourseDateChoiceListener listener;
    private onCourseNoteClickListener noteClickListener;
    @Bind(R.id.grid_view)
    protected GridView gridView;

    @Bind(R.id.tv_section1)
    protected TextView section1;
    @Bind(R.id.tv_section2)
    protected TextView section2;
    @Bind(R.id.tv_section3)
    protected TextView section3;
    @Bind(R.id.tv_section4)
    protected TextView section4;
    @Bind(R.id.tv_section5)
    protected TextView section5;

    private GridViewAdapter adapter;

    private final String[] titles;

    public CourseDateChoiceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        View view = View.inflate(context, R.layout.view_course_date_choice, null);
        addView(view);
        ButterKnife.bind(this, view);
        adapter = new GridViewAdapter(context);
        gridView.setAdapter(adapter);
        titles = getResources().getStringArray(R.array.week);
    }

    public void setData(List<CourseDateEntity> list) {
        adapter.clear();
        for (int i = 0; i < titles.length; i++) {
            CourseDateEntity entity = new CourseDateEntity();
            entity.setIsTitle(true);
            entity.setStart(titles[i]);
            adapter.add(entity);
        }
        adapter.addAll(list);
        adapter.notifyDataSetChanged();
        section1.setText(getSectionTitle(list.get(0)));
        section2.setText(getSectionTitle(list.get(7)));
        section3.setText(getSectionTitle(list.get(14)));
        section4.setText(getSectionTitle(list.get(21)));
        section5.setText(getSectionTitle(list.get(28)));
    }

    private String getSectionTitle(CourseDateEntity entity) {
        return entity.getStart() + "\n" + entity.getEnd();
    }

    public void setOnCourseDateChoiceListener(OnCourseDateChoiceListener listener) {
        this.listener = listener;
    }

    public void setOnCourseNoteClickListener(onCourseNoteClickListener listener) {
        this.noteClickListener = listener;
    }

    @OnClick(R.id.iv_note)
    protected void onNoteClick() {
        if (noteClickListener != null) {
            noteClickListener.onClick();
        }
    }

    private class GridViewAdapter extends MalaBaseAdapter<CourseDateEntity> {
        public GridViewAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = View.inflate(context, R.layout.view_course_date_choice_item, null);
            ViewHolder holder = new ViewHolder();
            holder.contentView = (TextView) view.findViewById(R.id.tv_content);
            holder.boughtView = view.findViewById(R.id.iv_bought);
            view.setTag(holder);
            return view;
        }

        public void choiceChanged() {
            List<CourseDateEntity> list = new ArrayList<>();
            for (CourseDateEntity entity : getList()) {
                if (entity.isChoice()) {
                    list.add(entity);
                }
            }
            if (listener != null) {
                listener.onCourseDateChoice(list);
            }
        }

        @Override
        protected void fillView(int position, final View convertView, final CourseDateEntity data) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            if (data.isTitle()) {
                convertView.setBackgroundColor(Color.parseColor("#88bcde"));
                holder.contentView.setText(data.getStart());
                return;
            } else {
                holder.contentView.setText("");
            }
            if (data.isAvailable()) {
                if (data.isChoice()) {
                    convertView.setBackgroundColor(convertView.getContext().getResources().getColor(R.color.color_blue_c5deee));
                } else {
                    convertView.setBackgroundColor(Color.TRANSPARENT);
                }
            } else {
                convertView.setBackgroundColor(Color.parseColor("#ededed"));
            }
            holder.boughtView.setVisibility(data.isBought() ? VISIBLE : INVISIBLE);
            convertView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!data.isAvailable() || data.isTitle()) {
                        return;
                    }
                    data.setChoice(!data.isChoice());
                    choiceChanged();
                    notifyDataSetChanged();
                }
            });
        }

        private class ViewHolder {
            private TextView contentView;
            private View boughtView;
        }
    }
}
