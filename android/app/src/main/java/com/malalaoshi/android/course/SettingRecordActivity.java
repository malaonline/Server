package com.malalaoshi.android.course;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.malalaoshi.android.R;
import com.malalaoshi.android.adapter.MalaBaseAdapter;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.entity.SettingRecordUI;
import com.malalaoshi.android.view.TitleBarView;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 个人评测建档
 * Created by tianwei on 3/6/16.
 */
public class SettingRecordActivity extends BaseActivity {

    @Bind(R.id.title_view)
    protected TitleBarView titleBarView;
    @Bind(R.id.list_view)
    protected ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_record);
        ButterKnife.bind(this);
        initData();
    }

    private void initData() {
        RecordAdapter adapter = new RecordAdapter(this);
        final String[] titles = getResources().getStringArray(R.array.setting_record_title);
        final String[] descriptions = getResources().getStringArray(R.array.setting_record_description);
        final int[] imgs = new int[]{R.drawable.ic_record_1, R.drawable.ic_record_2, R.drawable.ic_record_3};
        for (int i = 0; i < 3; i++) {
            SettingRecordUI record = new SettingRecordUI();
            record.setTitle(titles[i]);
            record.setDescription(descriptions[i]);
            record.setImg(imgs[i]);
            adapter.add(record);
        }
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO add result to activity result
            }
        });
    }

    private class RecordAdapter extends MalaBaseAdapter<SettingRecordUI> {
        public RecordAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            View view = View.inflate(context, R.layout.view_setting_record_item, null);
            ViewHolder holder = new ViewHolder();
            holder.titleView = (TextView) view.findViewById(R.id.tv_title);
            holder.desView = (TextView) view.findViewById(R.id.tv_des);
            holder.imageView = (ImageView) view.findViewById(R.id.iv_img);
            view.setTag(holder);
            return view;
        }

        @Override
        protected void fillView(int position, View convertView, SettingRecordUI data) {
            ViewHolder holder = (ViewHolder) convertView.getTag();
            holder.imageView.setImageResource(data.getImg());
            holder.titleView.setText(data.getTitle());
            holder.desView.setText(data.getDescription());
        }

        private class ViewHolder {
            TextView titleView;
            TextView desView;
            ImageView imageView;
        }
    }
}
