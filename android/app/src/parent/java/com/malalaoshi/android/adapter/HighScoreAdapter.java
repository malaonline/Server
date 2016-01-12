package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.HighScore;

import java.util.List;

/**
 * 高分榜adapter
 */
public class HighScoreAdapter extends BaseAdapter {
    private List<HighScore> highScores;
    private LayoutInflater layoutInflater;
    private Context mContext;

    private final int LIST_TITLE = 0;
    private final int LIST_ITEM  = 1;
    public HighScoreAdapter(Context context, List<HighScore> list){
        layoutInflater = LayoutInflater.from(context);
        highScores = list;
        mContext = context;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return LIST_TITLE;
        }else{
            return LIST_ITEM;
        }
    }

    @Override
    public int getCount() {
        return highScores.size();
    }

    @Override
    public Object getItem(int position) {
        return highScores.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder viewHolder = null;
        if (convertView==null){
            if (type==LIST_TITLE){
                convertView = layoutInflater.inflate(R.layout.highscore_list_title,null);
                viewHolder = new ViewHolder();
                convertView.setTag(viewHolder);
            }else{
                convertView = layoutInflater.inflate(R.layout.highscore_list_item,null);
                viewHolder = new ViewHolder();
                viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_highscore_name);
                viewHolder.tvIncreasedScores = (TextView)convertView.findViewById(R.id.tv_highscore_increasedscores);
                viewHolder.tvSchool = (TextView)convertView.findViewById(R.id.tv_highscore_school);
                viewHolder.tvAdmitted_to = (TextView)convertView.findViewById(R.id.tv_highscore_admitted_to);
                convertView.setTag(viewHolder);
            }
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        if (type==LIST_TITLE){
            return convertView;
        }else{
            HighScore data = highScores.get(position);
            viewHolder.tvName.setText(data.getName());
            viewHolder.tvIncreasedScores.setText(data.getIncreased_scores().toString()+"");
            viewHolder.tvSchool.setText(data.getSchool_name());
            viewHolder.tvAdmitted_to.setText(data.getAdmitted_to());
        }
        return convertView;
    }
    class ViewHolder{
        public TextView tvName;
        public TextView tvIncreasedScores;
        public TextView tvSchool;
        public TextView tvAdmitted_to;
    }
}
