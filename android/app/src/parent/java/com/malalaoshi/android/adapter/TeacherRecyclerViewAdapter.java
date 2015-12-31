package com.malalaoshi.android.adapter;

import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.TeacherDetailActivity;
import com.malalaoshi.android.entity.Grade;
import com.malalaoshi.android.entity.Subject;
import com.malalaoshi.android.entity.Tag;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.Number;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Teacher} and makes a call to the
 * specified {@link TeacherListFragment.OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class TeacherRecyclerViewAdapter extends RecyclerView.Adapter<TeacherRecyclerViewAdapter.ViewHolder>{
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;
    private static final int TYPE_NONE_VALUE = 2;

    public static final int TEACHER_LIST_PAGE_SIZE = 20;

    private final TeacherListFragment.OnListFragmentInteractionListener mListener;

    public boolean loading = false;

    private  List<Teacher> teachersList;

    public boolean hasLoadMoreView = false;

    public boolean canLoadMore = true;

    private ImageLoader mImageLoader;

    public TeacherRecyclerViewAdapter(List<Teacher> items, TeacherListFragment.OnListFragmentInteractionListener listener){
        teachersList = items;
        mListener = listener;
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = null;

        switch(viewType){
            case TYPE_LOAD_MORE:
                hasLoadMoreView = true;
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_refresh_footer, parent, false));
            case TYPE_NONE_VALUE:
                return new NoValueViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.nothing, parent, false));
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_list_body, parent, false);
                if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
                    ((CardView)view).setPreventCornerOverlap(false);
                }
                return new NormalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position){
        holder.update(position);
    }

    @Override
    public int getItemCount(){
        if(teachersList != null){
            if(teachersList.size() >= TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE && canLoadMore){
                if(teachersList.size() % 2 == 0){
                    return teachersList.size() + 1;
                }else{
                    return teachersList.size() + 2;
                }
            }else{
                return teachersList.size();
            }
        }else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position){
        int type = TYPE_NORMAL;
        if(teachersList != null && canLoadMore && teachersList.size() >= TeacherRecyclerViewAdapter.TEACHER_LIST_PAGE_SIZE){
            if(position == getItemCount() - 1){
                type = TYPE_LOAD_MORE;
            }else if(teachersList.size() % 2 != 0 && position == getItemCount() - 2){
                type = TYPE_NONE_VALUE;
            }
        }
        return type;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        protected ViewHolder(View itemView){
            super(itemView);
        }

        protected void update(int position){}
    }

    public class NoValueViewHolder extends ViewHolder{
        protected NoValueViewHolder(View itemView){
            super(itemView);
        }
    }

    public class LoadMoreViewHolder extends ViewHolder{
        @Bind(R.id.item_load_more_icon_loading)
        protected View iconLoading;

        protected LoadMoreViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void update(int position){
        }

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
            if(position >= teachersList.size()){
                return;
            }
            teacher = teachersList.get(position);
            name.setText(teacher.getName());
            String sub = teacher.getSubject();
            if(sub != null){
                subject.setText(sub);
            }
            String gradeStr = teacher.getGrades_shortname();
            if(gradeStr != null){
                gradeView.setText(gradeStr);
            }
            String tagStr = Tag.generateTagViewString(teacher.getTags());
            if(tagStr != null){
                tagView.setText(tagStr);
            }

            if (teacher.getAvatar() != null && !teacher.getAvatar().isEmpty()) {
                mImageLoader.get(teacher.getAvatar(), ImageLoader.getImageListener(avatar, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));
            }

            Double minPrice = teacher.getMin_price();
            String minPriceStr = minPrice == null ? "0" : Number.dfDecimal0.format(minPrice);
            Double maxPrice = teacher.getMax_price();
            String maxPriceStr = maxPrice == null ? "0" : Number.dfDecimal0.format(maxPrice);
            String currencyUnit = priceView.getContext().getString(R.string.currency_unit);
            priceView.setText(minPriceStr + "-" + maxPriceStr+ currencyUnit);
        }

        @OnClick(R.id.teacher_list_item_body)
        protected void onItemClick(){
            TeacherDetailActivity.open(this.view.getContext(), teacher!=null?teacher.getId():null);
        }
    }
}
