package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.core.image.MalaImageView;

/**
 * Created by kang on 16/6/14.
 */
public class GalleryAdapter extends MalaBaseAdapter<String> {
    public GalleryAdapter(Context context) {
        super(context);
    }

    @Override
    protected View createView(int position, ViewGroup parent) {
        View view = View.inflate(context, R.layout.grallery_list_item, null);
        ViewHolder holder = new ViewHolder(view);
        view.setTag(holder);
        return view;
    }

    @Override
    protected void fillView(int position, View convertView, String data) {
        ViewHolder holder = (ViewHolder) convertView.getTag();
        holder.gralleryView.loadImage(data, R.drawable.ic_default_img);
    }

    private static final class ViewHolder {
        private MalaImageView gralleryView;

        public ViewHolder(View view) {
            gralleryView = (MalaImageView) view.findViewById(R.id.iv_grallery);
        }
    }
}
