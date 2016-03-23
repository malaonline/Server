package com.malalaoshi.android.activitys;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.view.TitleBarView;


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

    //@Bind(R.id.rv_gallerys)
    //protected RecyclerView recyclerViewGallery;

    @Bind(R.id.lv_gallerys)
    protected ListView listViewGallery;

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

        listViewGallery.setAdapter(new GalleryAdapter2(this));
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
        private ImageLoader imageLoader;
        private LayoutInflater layoutInflater;
        GalleryAdapter2(Context context){
            layoutInflater = LayoutInflater.from(context);
            imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
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
            NetworkImageView[] networkImageViews = new NetworkImageView[3];
            public Holder(View itemView) {
                networkImageViews[0] = (NetworkImageView) itemView.findViewById(R.id.networkImageView1);
                networkImageViews[1] = (NetworkImageView) itemView.findViewById(R.id.networkImageView2);
                networkImageViews[2] = (NetworkImageView) itemView.findViewById(R.id.networkImageView3);
            }
            public void update(final int position){

                int count = photoUrls.length - position*3;
                for (int i=0;i<count;i++){
                    final int newPos = position*3+i;
                    //重置图高度
                    int width = networkImageViews[i].getMeasuredWidth();
                    ViewGroup.LayoutParams layoutParamses =  networkImageViews[i].getLayoutParams();
                    layoutParamses.height = width;
                    networkImageViews[i].setLayoutParams(layoutParamses);

                    //加载图片
                    updateNetworkImageView(networkImageViews[i],photoUrls[newPos]);
                    networkImageViews[i].setOnClickListener(new View.OnClickListener() {
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

            private void updateNetworkImageView(NetworkImageView imageView,String url){
                if (url != null && !url.isEmpty()) {
                    imageView.setDefaultImageResId(R.drawable.ic_default_photo);
                    imageView.setErrorImageResId(R.drawable.ic_default_photo);
                    imageView.setImageUrl(url, imageLoader);
                }else{
                    imageView.setDefaultImageResId(R.drawable.ic_default_photo);
                    imageView.setErrorImageResId(R.drawable.ic_default_photo);
                    imageView.setImageUrl("", imageLoader);
                }
            }
        }


    }


    class GalleryAdapter extends RecyclerView.Adapter{
        private ImageLoader imageLoader;
        GalleryAdapter(){
            imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_list_item, null);
            return new GalleryViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((GalleryViewHolder)holder).update(position);
        }

        @Override
        public int getItemCount() {
            int count = photoUrls.length/3;
            return photoUrls.length%3==0?count:count+1;
        }

        class GalleryViewHolder extends RecyclerView.ViewHolder{

            NetworkImageView[] networkImageViews = new NetworkImageView[3];
            View parent;
            public GalleryViewHolder(View itemView) {
                super(itemView);
                parent = itemView;
                networkImageViews[0] = (NetworkImageView) itemView.findViewById(R.id.networkImageView1);
                networkImageViews[1] = (NetworkImageView) itemView.findViewById(R.id.networkImageView2);
                networkImageViews[2] = (NetworkImageView) itemView.findViewById(R.id.networkImageView3);
            }

            public void update(final int position){

                int count = photoUrls.length - position*3;
                for (int i=0;i<count;i++){
                    //重置图高度
                    int width = networkImageViews[i].getMeasuredWidth();

                    ViewGroup.LayoutParams layoutParamses = networkImageViews[i].getLayoutParams();
                    layoutParamses.height = width;
                    networkImageViews[i].setLayoutParams(layoutParamses);


                    //加载图片
                    updateNetworkImageView(networkImageViews[i],photoUrls[position*3+i]);
                    networkImageViews[i].setOnClickListener(new View.OnClickListener() {
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

            private void updateNetworkImageView(NetworkImageView imageView,String url){
                if (url != null && !url.isEmpty()) {
                    imageView.setDefaultImageResId(R.drawable.ic_default_photo);
                    imageView.setErrorImageResId(R.drawable.ic_default_photo);
                    imageView.setImageUrl(url, imageLoader);
                }else{
                    imageView.setDefaultImageResId(R.drawable.ic_default_photo);
                    imageView.setErrorImageResId(R.drawable.ic_default_photo);
                    imageView.setImageUrl("", imageLoader);
                }
            }
        }
    }
}
