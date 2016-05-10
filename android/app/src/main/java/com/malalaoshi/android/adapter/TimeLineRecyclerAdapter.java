package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.view.FlowLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/6.
 */
class TimeLineRecyclerAdapter  extends
        RecyclerView.Adapter<TimeLineRecyclerAdapter.TimeLineViewHolder> {

    List<Order> lists;
    Context mContext;

    public TimeLineRecyclerAdapter(List<Order> lists, Context mContext) {
        super();
        this.lists = lists;
        this.mContext = mContext;
    }

    /*
     * 覆盖方法
     */
    @Override
    public int getItemCount() {
        // TODO 自动生成的方法存根
        return lists.size();
    }

    /*
     * 覆盖方法
     */
    @Override
    public TimeLineViewHolder onCreateViewHolder(ViewGroup arg0, int arg1) {
        // TODO 自动生成的方法存根
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.timeline_list_item, arg0, false);
        TimeLineViewHolder holder = new TimeLineViewHolder(view);
        return holder;
    }

    /*
     * 覆盖方法
     */
    @Override
    public void onBindViewHolder(TimeLineViewHolder arg0, int arg1) {
        // TODO 自动生成的方法存根
        //最后一项时，竖线不再显示
        if (arg1 == lists.size() - 1) {
            arg0.update(arg1);
        }
    }

    class TimeLineViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.tv_date)
        TextView tvDate;
        @Bind(R.id.tv_week)
        TextView tvWeek;
        @Bind(R.id.iv_time_market)
        ImageView ivTimeMarket;
        @Bind(R.id.fl_time)
        FlowLayout flTime;
        View view;

        /**
         * @param itemView
         */
        public TimeLineViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }
        protected void update(int position){
            if(position >= lists.size()){
                return;
            }
            Resources resources = view.getContext().getResources();
            if (position==0){
                ivTimeMarket.setImageDrawable(resources.getDrawable(R.drawable.ic_back));
            }else if (position==lists.size()-1){
                ivTimeMarket.setImageDrawable(resources.getDrawable(R.drawable.ic_back));
            }else{
                ivTimeMarket.setImageDrawable(resources.getDrawable(R.drawable.ic_back));
            }

            tvDate.setText("5月2日");
            tvWeek.setText("(星期二)");
            //
            flTime.setVisibility(View.GONE);
        }


    }

}
