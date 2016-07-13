package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.core.utils.EmptyUtils;

import jp.wasabeef.glide.transformations.CropCircleTransformation;

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
        Glide.with(context)
                .load(data)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.ic_default_img)
                .centerCrop()
                .crossFade()
                .into(holder.gralleryView);

    }

    private static final class ViewHolder {
        private ImageView gralleryView;

        public ViewHolder(View view) {
            gralleryView = (ImageView) view.findViewById(R.id.iv_grallery);
        }
    }
}
