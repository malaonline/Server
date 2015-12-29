package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.entity.GHighScore;

import java.util.List;

/**
 * 高分榜adapter
 */
public class HighScoreAdapter extends BaseAdapter {
    private List<GHighScore> highScores;
    LayoutInflater layoutInflater;
    public HighScoreAdapter(Context context, List<GHighScore> list){
        layoutInflater = LayoutInflater.from(context);
        highScores = list;
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
        ViewHolder viewHolder = null;
        GHighScore data = highScores.get(position);
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.highscore_list_item,null);
            viewHolder = new ViewHolder();
            viewHolder.tvName = (TextView)convertView.findViewById(R.id.tv_highscore_name);
            viewHolder.tvIncreasedScores = (TextView)convertView.findViewById(R.id.tv_highscore_increasedscores);
            viewHolder.tvSchool = (TextView)convertView.findViewById(R.id.tv_highscore_school);
            viewHolder.tvAdmitted_to = (TextView)convertView.findViewById(R.id.tv_highscore_admitted_to);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.tvName.setText(data.getName());
        viewHolder.tvIncreasedScores.setText(data.getIncreased_scores()+"");
        viewHolder.tvSchool.setText(data.getSchool_name());
        viewHolder.tvAdmitted_to.setText(data.getAdmitted_to());
        return convertView;
    }
    class ViewHolder{
        public TextView tvName;
        public TextView tvIncreasedScores;
        public TextView tvSchool;
        public TextView tvAdmitted_to;
    }
}
