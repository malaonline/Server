package com.malalaoshi.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.core.utils.EmptyUtils;

/**
 * Created by kang on 16/6/14.
 */
public class GralleryAdapter extends MalaBaseAdapter<String> {
    public GralleryAdapter(Context context) {
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
        if (EmptyUtils.isEmpty(data)){
            data = "";
        }
        holder.gralleryView.setImageURI(Uri.parse(data));
    }

    private static final class ViewHolder {
        private SimpleDraweeView gralleryView;

        public ViewHolder(View view) {
            gralleryView = (SimpleDraweeView) view.findViewById(R.id.iv_grallery);
            //gralleryView = (SimpleDraweeView) view;
        }
    }
}
