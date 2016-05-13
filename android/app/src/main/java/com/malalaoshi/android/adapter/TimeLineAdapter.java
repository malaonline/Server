package com.malalaoshi.android.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.Order;
import com.malalaoshi.android.entity.SchoolUI;
import com.malalaoshi.android.util.LocationUtil;
import com.malalaoshi.android.view.FlowLayout;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by kang on 16/5/6.
 */
public class TimeLineAdapter extends BaseAdapter {
    private List<Order> list;
    LayoutInflater layoutInflater;

    public TimeLineAdapter(Context context, List<Order> list) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        for (int i=0;i<15;i++){
            list.add(new Order());
        }
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final Order data = list.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.timeline_list_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.update(position);
        return convertView;
    }

    class ViewHolder {
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
        public ViewHolder(View itemView) {
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        protected void update(int position) {
            if (position >= list.size()) {
                return;
            }
            Resources resources = view.getContext().getResources();
            if (position == 0) {
                ivTimeMarket.setImageDrawable(resources.getDrawable(R.drawable.ic_back));
            } else if (position == list.size() - 1) {
                ivTimeMarket.setImageDrawable(resources.getDrawable(R.drawable.ic_back));
            } else {
                ivTimeMarket.setImageDrawable(resources.getDrawable(R.drawable.ic_back));
            }

            tvDate.setText("12月31日");
            tvWeek.setText("星期二");
            //
            flTime.setVisibility(View.GONE);
            flTime.setFocusable(false);
            flTime.removeAllViews();

            int buttomPadding = resources.getDimensionPixelSize(R.dimen.item_text_padding);
            int leftPadding = resources.getDimensionPixelSize(R.dimen.item_text_left_padding);
            for (int i = 0; i < 4; i++) {
                TextView textView = new TextView(view.getContext());
                ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setLayoutParams(layoutParams);
                textView.setPadding(leftPadding, 0, 0, buttomPadding);
                textView.setText("10:30-12:30");
                //textView.setTextSize(resources);
                textView.setTextColor(resources.getColor(R.color.tab_text_normal_color));
                flTime.addView(textView, i);
            }
        }
    }
}