package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.BaseEntity;

import java.util.List;

/**
 * Created by kang on 16/1/26.
 */
public class SingleChoiceAdapter extends BaseAdapter {
    private List<BaseEntity> list;
    private LayoutInflater layoutInflater;

    public SingleChoiceAdapter(Context context, List<BaseEntity> list) {
        layoutInflater = LayoutInflater.from(context);
        this.list = list;
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
        final BaseEntity data = list.get(position);
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.single_choice_list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.textView = (TextView) convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(data.getName());
        return convertView;
    }

    class ViewHolder {
        public TextView textView;
    }
}

