package com.malalaoshi.android.adapter;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.TeacherDetailActivity;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.util.Number;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Teacher} and makes a call to the
 * specified {@link TeacherListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TeacherRecyclerViewAdapter extends RecyclerView.Adapter<TeacherRecyclerViewAdapter.ViewHolder> {

    public static final List<Teacher> mValues = new ArrayList<Teacher>();
    private final TeacherListFragment.OnListFragmentInteractionListener mListener;

    public TeacherRecyclerViewAdapter(List<Teacher> items, TeacherListFragment.OnListFragmentInteractionListener listener) {
        mValues.addAll(items);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.teacher_list_body, parent, false);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            ((CardView)view).setPreventCornerOverlap(false);
        }
        return new NormalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.update(position);
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ViewHolder(View itemView) {
            super(itemView);
        }

        protected void update(int position) {}
    }

    public class NormalViewHolder extends ViewHolder{
        @Bind(R.id.teacher_list_item_avatar)
        protected ImageView avatar;

        @Bind(R.id.teacher_list_item_price)
        protected TextView priceView;

        @Bind(R.id.teacher_list_item_name)
        protected TextView name;

        @Bind(R.id.teacher_list_item_grade_view)
        protected TextView gradeView;

        @Bind(R.id.teacher_list_item_subject)
        protected TextView subject;

        @Bind(R.id.teacher_list_item_tags)
        protected TextView tagView;

        protected com.malalaoshi.android.entity.Teacher teacher;

        protected View view;

        protected NormalViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        @Override
        protected void update(int position){
            teacher = mValues.get(position);
            name.setText(teacher.getName());
            Subject sub = Subject.getSubjectFromListById(teacher.getSubject(), Subject.subjectList);
            if(sub != null){
                subject.setText(sub.getName());
            }
            String gradeStr = Grade.generateGradeViewString(teacher.getGrades());
            if(gradeStr != null){
                gradeView.setText(gradeStr);
            }
            String tagStr = Tag.generateTagViewString(teacher.getTags(), Tag.tags);
            if(tagStr != null){
                tagView.setText(tagStr);
            }

            Double minPrice = teacher.getMinPrice();
            String minPriceStr = minPrice == null ? "0" : Number.dfDecimal0.format(minPrice);
            Double maxPrice = teacher.getMaxPrice();
            String maxPriceStr = maxPrice == null ? "0" : Number.dfDecimal0.format(maxPrice);
            priceView.setText(minPriceStr + "-" + maxPriceStr);
        }

        @OnClick(R.id.teacher_list_item_body)
        protected void onItemClick(){
            TeacherDetailActivity.open(this.view.getContext(), null);
        }
    }
}
