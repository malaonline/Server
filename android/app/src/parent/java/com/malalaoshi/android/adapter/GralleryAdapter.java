package com.malalaoshi.android.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

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
      /*  View view = View.inflate(context, R.layout.grallery_list_item, null);
        int height = context.getResources().getDimensionPixelOffset(R.dimen.course_time_height);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, height);
        view.setLayoutParams(params);
*/
        SimpleDraweeView imageView = new SimpleDraweeView(context);
        int gralleryWidth = context.getResources().getDimensionPixelSize(R.dimen.grallery_width);
        int gralleryHeight = context.getResources().getDimensionPixelSize(R.dimen.grallery_height);
        imageView.setLayoutParams(new ViewGroup.MarginLayoutParams(
                gralleryWidth, gralleryHeight));
        ViewHolder holder = new ViewHolder(imageView);
        imageView.setTag(holder);
        return imageView;
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
            gralleryView = (SimpleDraweeView) view;
        }
    }
}
