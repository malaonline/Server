package com.malalaoshi.android.adapter;

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
import com.malalaoshi.android.entity.Teacher;
import com.malalaoshi.android.fragments.TeacherListFragment;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.Number;
import com.malalaoshi.android.util.StringUtil;
import com.malalaoshi.android.view.CircleNetworkImage;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;



public class TeacherRecyclerViewAdapter extends RecyclerView.Adapter<TeacherRecyclerViewAdapter.ViewHolder>{
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //没有更多数据,到底了
    public static final int NODATA_LOADING = 2;
    //没有更多数据,到底了
    public static final int GONE_LOADING = 3;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;

    public static final int TEACHER_LIST_PAGE_SIZE = 10;

    private  List<Teacher> teachersList;

    private ImageLoader mImageLoader;

    public TeacherRecyclerViewAdapter(List<Teacher> items){
        teachersList = items;
        mImageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = null;

        switch(viewType){
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_refresh_footer, null));
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.teacher_list_item, null);
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
            return teachersList.size()+1;
        }else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position){
        int type = TYPE_NORMAL;
        if (position==getItemCount()-1){
            type = TYPE_LOAD_MORE;
        }
        return type;
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void setMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    public int getMoreStatus(){
        return load_more_status;
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

        @Bind(R.id.iv_normal_refresh_footer_chrysanthemum)
        protected ImageView ivProgress;

        @Bind(R.id.tv_normal_refresh_footer_status)
        protected TextView tvStatusText;

        protected LoadMoreViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void update(int position){
            if (position<=2){
                iconLoading.setVisibility(View.GONE);
            }else{
                iconLoading.setVisibility(View.VISIBLE);
            }

            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    tvStatusText.setText("上拉加载更多...");
                    ivProgress.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    tvStatusText.setText("加载中...");
                    ivProgress.setVisibility(View.VISIBLE);
                    break;
                case NODATA_LOADING:
                    tvStatusText.setText("到底了,没有更多数据了!");
                    ivProgress.setVisibility(View.GONE);
                    break;
                case GONE_LOADING:
                    //iconLoading.setVisibility(View.GONE);
                    break;
            }

        }

    }

    public class NormalViewHolder extends ViewHolder{
        @Bind(R.id.teacher_list_item_name)
        protected TextView name;

        @Bind(R.id.teacher_list_item_level)
        protected TextView level;

        @Bind(R.id.teacher_list_item_avater)
        protected CircleNetworkImage avater;

        @Bind(R.id.teacher_list_item_price)
        protected TextView price;

        @Bind(R.id.teacher_list_item_tags)
        protected TextView tags;

        @Bind(R.id.teacher_list_item_subjects)
        protected TextView subjects;

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
            level.setText(teacher.getLevel());
            name.setText(teacher.getName());
            String sub = teacher.getSubject();
            String gradeStr = teacher.getGrades_shortname();
            if(gradeStr != null&&!gradeStr.equals("")&&sub!=null&&!sub.equals("")){
                subjects.setText(gradeStr+" · "+ sub);
            }else {
                if (gradeStr == null||gradeStr.equals("")){
                    subjects.setText(sub);
                }else if (sub==null||sub.equals("")){
                    subjects.setText(gradeStr);
                }
            }
            String tagStr = StringUtil.join(teacher.getTags());
            if(tagStr != null){
                tags.setText(tagStr);
            }
            String imgUrl = teacher.getAvatar();
            if (imgUrl != null && !imgUrl.isEmpty()) {
                avater.setDefaultImageResId(R.drawable.default_avatar);
                avater.setErrorImageResId(R.drawable.default_avatar);
                avater.setImageUrl(imgUrl, mImageLoader);
            }else{
                avater.setDefaultImageResId(R.drawable.default_avatar);
                avater.setErrorImageResId(R.drawable.default_avatar);
                avater.setImageUrl("", mImageLoader);
            }

            String priceRange = "价格异常";
            Double minPrice = teacher.getMin_price();
            Double maxPrice = teacher.getMax_price();
            if(minPrice!=null&&maxPrice!=null){
                priceRange = Number.subZeroAndDot(minPrice*0.01d)+"-"+ Number.subZeroAndDot(maxPrice*0.01d);
            }
            price.setText(priceRange);

        }

        @OnClick(R.id.teacher_list_item_body)
        protected void onItemClick(){
            TeacherDetailActivity.open(this.view.getContext(), teacher!=null?teacher.getId():null);
        }
    }
}
