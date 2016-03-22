package com.malalaoshi.android.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.decoration.DividerItemDecoration;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.view.TitleBarView;

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

    @Bind(R.id.rv_gallerys)
    protected RecyclerView recyclerViewGallery;

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
        //设置布局管理器
        recyclerViewGallery.setLayoutManager(new GridLayoutManager(this,3));
        //设置adapter
        recyclerViewGallery.setAdapter(new GalleryAdapter());
        //设置Item增加、移除动画
        recyclerViewGallery.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerViewGallery.addItemDecoration(new DividerItemDecoration(
                this, DividerItemDecoration.HORIZONTAL_LIST));
    }

    @Override
    public void onTitleLeftClick() {
        this.finish();
    }

    @Override
    public void onTitleRightClick() {

    }

    class GalleryAdapter extends RecyclerView.Adapter{
        private ImageLoader imageLoader;
        GalleryAdapter(){
            imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new GalleryViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, null));
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((GalleryViewHolder)holder).update(position);
        }

        @Override
        public int getItemCount() {
            return photoUrls.length;
        }

        class GalleryViewHolder extends RecyclerView.ViewHolder{

            NetworkImageView networkImageView;
            public GalleryViewHolder(View itemView) {
                super(itemView);
                networkImageView = (NetworkImageView) itemView.findViewById(R.id.networkImageView);

            }

            public void update(final int position){
                String imgUrl = photoUrls[position];
                if (imgUrl != null && !imgUrl.isEmpty()) {
                    networkImageView.setDefaultImageResId(R.drawable.ic_default_teacher_avatar);
                    networkImageView.setErrorImageResId(R.drawable.ic_default_teacher_avatar);
                    networkImageView.setImageUrl(imgUrl, imageLoader);
                }else{
                    networkImageView.setDefaultImageResId(R.drawable.ic_default_teacher_avatar);
                    networkImageView.setErrorImageResId(R.drawable.ic_default_teacher_avatar);
                    networkImageView.setImageUrl("", imageLoader);
                }
                networkImageView.setOnClickListener(new View.OnClickListener() {
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
    }
}
