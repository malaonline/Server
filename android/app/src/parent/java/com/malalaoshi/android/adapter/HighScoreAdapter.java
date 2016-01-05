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

    public HighScoreAdapter(Context context, List<HighScore> list){
        layoutInflater = LayoutInflater.from(context);
        highScores = list;
        mContext = context;
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
        if (position==0){
            viewHolder.tvName.setText(mContext.getResources().getString(R.string.highscore_name));
            viewHolder.tvIncreasedScores.setText(mContext.getResources().getString(R.string.highscore_increasedscores));
            viewHolder.tvSchool.setText(mContext.getResources().getString(R.string.highscore_school));
            viewHolder.tvAdmitted_to.setText(mContext.getResources().getString(R.string.highscore_admittedto));
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
