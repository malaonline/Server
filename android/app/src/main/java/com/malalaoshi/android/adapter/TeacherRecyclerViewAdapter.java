package com.malalaoshi.android.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.fragments.TeacherFragment;
import com.malalaoshi.android.entity.Teacher;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Teacher} and makes a call to the
 * specified {@link TeacherFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TeacherRecyclerViewAdapter extends RecyclerView.Adapter<TeacherRecyclerViewAdapter.ViewHolder> {

    public static final List<Teacher> mValues = new ArrayList<Teacher>();
    private final TeacherFragment.OnListFragmentInteractionListener mListener;

    public TeacherRecyclerViewAdapter(List<Teacher> items, TeacherFragment.OnListFragmentInteractionListener listener) {
        mValues.addAll(items);
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_teacher, parent, false);
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
        @Bind(R.id.fragment_teacher_id)
        protected TextView id;

        @Bind(R.id.fragment_teacher_name)
        protected TextView name;

        private com.malalaoshi.android.entity.Teacher teacher;

        protected NormalViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void update(int position){
            teacher = mValues.get(position);
            id.setText(teacher.getId());
            name.setText(teacher.getName());
        }
    }
}
