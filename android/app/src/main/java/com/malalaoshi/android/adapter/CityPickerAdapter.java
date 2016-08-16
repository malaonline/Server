package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.entity.City;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.util.LocationUtil;

/**
 * Created by kang on 16/1/5.
 */
public class CityPickerAdapter extends MalaBaseAdapter<City> {

    public CityPickerAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View convertView = View.inflate(context, R.layout.item_city_gridview, null);
        ViewHolder viewHolder = new ViewHolder();
        viewHolder.tvCityName = (TextView) convertView.findViewById(R.id.tv_city_name);
        convertView.setTag(viewHolder);
        return convertView;
    }

    @Override
    protected void fillView(int position, View convertView, City data) {
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();

        viewHolder.tvCityName.setText(data.getName());
    }

    class ViewHolder {
        public TextView tvCityName;
    }
}
