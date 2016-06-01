package com.malalaoshi.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.view.ViewGroup.LayoutParams;

import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.Schedule;

import java.util.List;

/**
 * Created by kang on 15/12/29.
 */
public class ScheduleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private List<Schedule> mSchedules;

    private static final int TYPE_ITEM = 0;
    private static final int TYPE_FOOTER = 1;

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

    public ScheduleAdapter(Context context, List<Schedule> schedules){
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        mSchedules = schedules;

    }

    @Override
    public int getItemViewType(int position) {
        if (position + 1 == getItemCount()) {
            return TYPE_FOOTER;
        } else {
            return TYPE_ITEM;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_list_item, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new ItemViewHolder(view);
        } else if (viewType == TYPE_FOOTER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_footerview, null);
            view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
            return new FooterViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder){
            Schedule schedule = mSchedules.get(position);
            ((ItemViewHolder)holder).tvCourse.setText(schedule.getSubject());
            ((ItemViewHolder)holder).tvClassTime.setText(schedule.getTime());
            ((ItemViewHolder)holder).tvClassAddress.setText(schedule.getAddress());
            String avatarUrl = schedule.getAvatar();
            if (!EmptyUtils.isEmpty(avatarUrl)){
                ((ItemViewHolder) holder).ivTeacherAvatar.setImageURI(Uri.parse(avatarUrl));
            }

            /*  if (schedule.getStatus()==0){
                ((ItemViewHolder)holder).tvConfirmTime.
                ((ItemViewHolder)holder).tvCourseStart
                ((ItemViewHolder)holder).tvCourseEnd
            }*/

        }else if(holder instanceof FooterViewHolder){
            FooterViewHolder footViewHolder=(FooterViewHolder)holder;
            if (position<=0){
                ((FooterViewHolder) holder).view.setVisibility(View.GONE);
            }else{
                ((FooterViewHolder) holder).view.setVisibility(View.VISIBLE);
            }

            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    footViewHolder.textView.setText("上拉加载更多...");
                    footViewHolder.progressBar.setVisibility(View.GONE);
                    break;
                case LOADING_MORE:
                    footViewHolder.textView.setText("加载中...");
                    footViewHolder.progressBar.setVisibility(View.VISIBLE);
                    break;
                case NODATA_LOADING:
                    footViewHolder.textView.setText("到底了,没有更多数据了!");
                    footViewHolder.progressBar.setVisibility(View.GONE);
                    break;
                case GONE_LOADING:
                    ((FooterViewHolder) holder).view.setVisibility(View.GONE);
                    break;
            }
        }
    }

    //添加数据
    public void addItem(List<Schedule> newDatas) {
        newDatas.addAll(mSchedules);
        mSchedules.removeAll(mSchedules);
        mSchedules.addAll(newDatas);
        notifyDataSetChanged();
    }

    public void addMoreItem(List<Schedule> newDatas) {
        mSchedules.addAll(newDatas);
        notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return mSchedules.size() + 1;
    }

    class FooterViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView textView;
        ContentLoadingProgressBar progressBar;
        public FooterViewHolder(View view) {
            super(view);
            this.view = view;
            textView = (TextView) view.findViewById(R.id.load_textview);
            progressBar = (ContentLoadingProgressBar) view.findViewById(R.id.progressbar);
        }

    }


    class ItemViewHolder extends RecyclerView.ViewHolder{

        TextView tvCourse,tvClassTime,tvClassAddress,tvConfirmTime,tvCourseStart,tvCourseEnd;
        SimpleDraweeView ivTeacherAvatar;
        View mView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
            tvCourse = (TextView) mView.findViewById(R.id.tv_course);
            tvClassTime = (TextView) mView.findViewById(R.id.tv_class_time);
            tvClassAddress = (TextView) mView.findViewById(R.id.tv_class_address);
            ivTeacherAvatar = (SimpleDraweeView) mView.findViewById(R.id.iv_teacher_avatar);
            tvConfirmTime = (TextView) mView.findViewById(R.id.tv_confirm_time);
            tvCourseStart = (TextView) mView.findViewById(R.id.tv_course_start);
            tvCourseEnd = (TextView) mView.findViewById(R.id.tv_course_end);
        }
    }

}
