package com.malalaoshi.android.adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.malalaoshi.android.R;
import com.malalaoshi.android.common.FragmentManage;
import com.malalaoshi.android.fragments.TeacherDetailFragment;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.entity.Teacher;

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
        @Bind(R.id.fragment_teacher_list_item_id)
        protected TextView id;

        @Bind(R.id.fragment_teacher_list_item_name)
        protected TextView name;

        private com.malalaoshi.android.entity.Teacher teacher;

        private View view;

        protected NormalViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        @Override
        protected void update(int position){
            teacher = mValues.get(position);
            id.setText(teacher.getId());
            name.setText(teacher.getName());
        }

        @OnClick(R.id.fragment_teacher_list_item)
        protected void onItemClick(){
            Toast.makeText(this.view.getContext(), "click:"+this.name.getText(), Toast.LENGTH_SHORT).show();
            FragmentManager frm = ((Activity)this.view.getContext()).getFragmentManager();
            FragmentManage.opFragmentMainActivity(frm, frm.findFragmentByTag(TeacherListFragment.fragmentTag), new TeacherDetailFragment(), TeacherDetailFragment.fragmentTag);
        }
    }
}
