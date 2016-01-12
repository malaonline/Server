package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.TeacherDetailActivity;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.util.LocationUtil;

import java.util.List;

/**
 * Created by kang on 16/1/5.
 */
public class SchoolAdapter extends BaseAdapter {

    private List<School> schools;
    LayoutInflater layoutInflater;
    private ImageLoader imageLoader;

    public SchoolAdapter(Context context, List<School> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.schools = list;
        this.imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
    }

    public void setSchools(List<School> schools) {
        this.schools = schools;
    }

    public List<School> getSchools() {
        return schools;
    }

    @Override
    public int getCount() {
        return schools.size();
    }

    @Override
    public Object getItem(int position) {
        return schools.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        final School data = schools.get(position);
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.school_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.ivSchoolPic = (NetworkImageView)convertView.findViewById(R.id.iv_school_pic);
            viewHolder.tvSchoolName = (TextView)convertView.findViewById(R.id.tv_school_name);
            viewHolder.tvSchoolAddress = (TextView)convertView.findViewById(R.id.tv_school_address);
            viewHolder.tvSchoolDistance = (TextView)convertView.findViewById(R.id.tv_school_distance);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        String imgUrl = data.getThumbnail();
        if (imgUrl != null && !imgUrl.equals("")) {

            viewHolder.ivSchoolPic.setDefaultImageResId(R.mipmap.ic_launcher);
            viewHolder.ivSchoolPic.setErrorImageResId(R.mipmap.ic_launcher);
            viewHolder.ivSchoolPic.setImageUrl(imgUrl, imageLoader);

        }
        viewHolder.tvSchoolName.setText(data.getName());
        viewHolder.tvSchoolAddress.setText(data.getAddress());
        double distance = data.getRegion();
        if (distance>=0.0D){
            String dis;
            if (distance<=100){
                dis = String.format("%.2f", distance);
                viewHolder.tvSchoolDistance.setText(dis+"m");
            }else{
                dis = String.format("%.2f", distance/1000);
                viewHolder.tvSchoolDistance.setText(dis+"km");
            }
        }
        return convertView;
    }
    class ViewHolder {
        public NetworkImageView ivSchoolPic;
        public TextView tvSchoolName;
        public TextView tvSchoolAddress;
        public TextView tvSchoolDistance;
    }
}
