package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;

import java.util.List;
import java.util.Map;


/**
 * Created by kang on 16/1/18.
 */
public class FilterAdapter extends BaseAdapter {
    private List<Map<String, Object>> list;
    private LayoutInflater layoutInflater;
    private Context context;
    private int layoutId;
    public FilterAdapter(Context context, List<Map<String, Object>> list, int layoutId){
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
        this.layoutId = layoutId;
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
        final Map<String, Object> data = list.get(position);
        if (convertView==null){
            convertView = layoutInflater.inflate(layoutId,null);
            viewHolder = new ViewHolder();
            viewHolder.imageView = (ImageView)convertView.findViewById(R.id.tv_filter_icon);
            viewHolder.textView = (TextView)convertView.findViewById(R.id.tv_filter_text);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.textView.setText((String) data.get("name"));
        return convertView;
    }
    class ViewHolder {
        public ImageView imageView;
        public TextView textView;
    }
}
