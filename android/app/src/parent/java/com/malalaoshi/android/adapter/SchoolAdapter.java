package com.malalaoshi.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.entity.School;
import com.malalaoshi.android.util.LocationUtil;

import java.util.List;

/**
 * Created by kang on 16/1/5.
 */
public class SchoolAdapter extends BaseAdapter {

    private List<School> schools;
    private LayoutInflater layoutInflater;

    public SchoolAdapter(Context context, List<School> list){
        this.layoutInflater = LayoutInflater.from(context);
        this.schools = list;
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
            viewHolder.ivSchoolPic = (SimpleDraweeView)convertView.findViewById(R.id.iv_school_pic);
            viewHolder.tvSchoolName = (TextView)convertView.findViewById(R.id.tv_school_name);
            viewHolder.tvSchoolAddress = (TextView)convertView.findViewById(R.id.tv_school_address);
            viewHolder.tvSchoolDistance = (TextView)convertView.findViewById(R.id.tv_school_distance);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        String imgUrl = data.getThumbnail();
        if (!EmptyUtils.isEmpty(imgUrl)){
            viewHolder.ivSchoolPic.setImageURI(Uri.parse(imgUrl));
        }
        viewHolder.tvSchoolName.setText(data.getName());
        viewHolder.tvSchoolAddress.setText(data.getAddress());
        Double distance = data.getDistance();
        if (distance!=null&&distance>=0.0D){
            String dis = LocationUtil.formatDistance(distance);
            viewHolder.tvSchoolDistance.setText(dis);
        }
        return convertView;
    }
    class ViewHolder {
        public SimpleDraweeView ivSchoolPic;
        public TextView tvSchoolName;
        public TextView tvSchoolAddress;
        public TextView tvSchoolDistance;
    }
}
