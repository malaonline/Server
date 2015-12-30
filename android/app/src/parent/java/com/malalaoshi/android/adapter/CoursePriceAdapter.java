package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.CoursePrice;
import com.malalaoshi.android.entity.Grade;

import java.util.List;

/**
 * 价格表adapter
 */
public class CoursePriceAdapter extends BaseAdapter{

    private List<CoursePrice> coursePrices;
    LayoutInflater layoutInflater;
    private OnClickItem onClickItem = null;
    public CoursePriceAdapter(Context context, List<CoursePrice> list){
        layoutInflater = LayoutInflater.from(context);
        coursePrices = list;
    }

    public void setOnClickItem(OnClickItem onClickItem){
        this.onClickItem = onClickItem;
    }

    @Override
    public int getCount() {
        return coursePrices.size();
    }

    @Override
    public Object getItem(int position) {
        return coursePrices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final CoursePrice data = coursePrices.get(position);
        final Grade grade = data.getGrade();
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.course_price_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_item_name);
            viewHolder.tvPrice = (TextView)convertView.findViewById(R.id.tv_item_price);
            viewHolder.tvRebate = (TextView)convertView.findViewById(R.id.tv_item_rebate);
            viewHolder.tvSignup = (Button)convertView.findViewById(R.id.btn_item_signup);
            viewHolder.tvSignup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClickItem!=null){
                        onClickItem.onClickItem(position, grade!=null?null:grade.getId());
                    }
                }
            });
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvName.setText(grade!=null?grade.getName():"");
        viewHolder.tvPrice.setText(data.getPrice()+"");
        viewHolder.tvRebate.setText("");
        return convertView;
    }
    class ViewHolder{
        public TextView tvName;
        public TextView tvPrice;
        public TextView tvRebate;
        public Button tvSignup;
    }

    public interface OnClickItem{
        void onClickItem(int position, Long gradeId);

    }
}
