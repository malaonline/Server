package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.malalaoshi.android.R;
import com.malalaoshi.android.core.base.BaseActivity;
import com.malalaoshi.android.core.base.MalaBaseAdapter;
import com.malalaoshi.android.core.utils.EmptyUtils;
import com.malalaoshi.android.core.view.TitleBarView;
import com.malalaoshi.android.util.DensityUtil;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Created by kang on 16/3/22.
 */
public class GalleryPreviewActivity extends BaseActivity implements TitleBarView.OnTitleBarClickListener {
    public static String GALLERY_URLS = "gallery_urls";

    private String[] photoUrls = null;

    @Bind(R.id.titleBar)
    protected TitleBarView titleBarView;

    @Bind(R.id.lv_gallerys)
    protected GridView listViewGallery;

    private GalleryAdapter galleryAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_perview);
        ButterKnife.bind(this);
        initData();
        initViews();
        setEvent();
    }

    private void initData() {
        Intent intent = getIntent();
        photoUrls = intent.getStringArrayExtra(GALLERY_URLS);
        if (photoUrls==null){
            photoUrls = new String[0];
        }
    }

    private void setEvent() {
        titleBarView.setOnTitleBarClickListener(this);
    }

    private void initViews() {
        galleryAdapter = new GalleryAdapter(this);
        List<String> photos = Arrays.asList(photoUrls);
        galleryAdapter.addAll(photos);
        listViewGallery.setAdapter(galleryAdapter);
        //设置布局管理器
        /*recyclerViewGallery.setLayoutManager(new GridLayoutManager(this,1));
        //设置adapter
        recyclerViewGallery.setAdapter(new GalleryAdapter());
        //设置Item增加、移除动画
        recyclerViewGallery.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerViewGallery.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL_LIST));*/
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }


    class GalleryAdapter2 extends BaseAdapter{

        private LayoutInflater layoutInflater;
        GalleryAdapter2(Context context){
            layoutInflater = LayoutInflater.from(context);
        }
        @Override
        public int getCount() {
            int count = photoUrls.length/3;
            return photoUrls.length%3==0?count:count+1;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            Holder holder = null;
            if(convertView==null) {
                convertView = layoutInflater.inflate(R.layout.gallery_list_item, null);
                holder=new Holder(convertView);
                convertView.setTag(holder);
            }else{
                holder=(Holder) convertView.getTag();
            }
            holder.update(position);
            return convertView;
        }

        class Holder{
            ImageView[] imageViews = new ImageView[3];
            public Holder(View itemView) {
                imageViews[0] = (ImageView) itemView.findViewById(R.id.networkImageView1);
                imageViews[1] = (ImageView) itemView.findViewById(R.id.networkImageView2);
                imageViews[2] = (ImageView) itemView.findViewById(R.id.networkImageView3);
            }
            public void update(final int position){
                imageViews[0].setVisibility(View.INVISIBLE);
                imageViews[1].setVisibility(View.INVISIBLE);
                imageViews[2].setVisibility(View.INVISIBLE);
                int count = (photoUrls.length - position*3)>3?3:photoUrls.length - position*3;
                for (int i=0;i<count;i++){
                    imageViews[i].setVisibility(View.VISIBLE);
                    final int newPos = position*3+i;
                    //重置图高度
                    int width = imageViews[i].getMeasuredWidth();
                    ViewGroup.LayoutParams layoutParamses =  imageViews[i].getLayoutParams();
                    layoutParamses.height = width;
                    imageViews[i].setLayoutParams(layoutParamses);

                    //加载图片
                    updateNetworkImageView(imageViews[i],photoUrls[newPos]);
                    imageViews[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(GalleryPreviewActivity.this, GalleryActivity.class);
                            intent.putExtra(GalleryActivity.GALLERY_URLS, photoUrls);
                            intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, newPos);
                            startActivity(intent);
                        }
                    });
                }
            }

            private void updateNetworkImageView(ImageView imageView,String url){
                Glide.with(GalleryPreviewActivity.this)
                        .load(url)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.ic_default_photo)
                        .error(R.drawable.ic_default_photo)
                        .crossFade()
                        .into(imageView);
            }
        }


    }


    class GalleryAdapter extends MalaBaseAdapter<String>{

        public GalleryAdapter(Context context) {
            super(context);
        }

        @Override
        protected View createView(int position, ViewGroup parent) {
            ImageView imageView = new ImageView(context);
            ViewGroup.LayoutParams layoutParamses = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
            int width = DensityUtil.getScreemWidth(context);
            width = (width - 2*context.getResources().getDimensionPixelSize(R.dimen.grallery_preview_divider))/3;
            layoutParamses.width = width;
            layoutParamses.height = width;
            imageView.setLayoutParams(layoutParamses);
            return imageView;
        }

        @Override
        protected void fillView(final int position, View convertView, String data) {
            Glide.with(GalleryPreviewActivity.this)
                    .load(data)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.ic_default_photo)
                    .crossFade()
                    .centerCrop()
                    .into((ImageView) convertView);
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(GalleryPreviewActivity.this, GalleryActivity.class);
                    intent.putExtra(GalleryActivity.GALLERY_URLS, photoUrls);
                    intent.putExtra(GalleryActivity.GALLERY_CURRENT_INDEX, position);
                    startActivity(intent);
                }
            });
        }
    }

    @Override
    protected String getStatName() {
        return "相册预览";
    }
}
