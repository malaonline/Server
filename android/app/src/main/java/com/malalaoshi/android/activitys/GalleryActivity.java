package com.malalaoshi.android.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.MalaApplication;
import com.malalaoshi.android.R;
import com.malalaoshi.android.base.BaseActivity;
import com.malalaoshi.android.util.ImageCache;
import com.malalaoshi.android.view.ZoomImageView;

import java.util.ArrayList;

/**
 * Created by zk on 16/01/12.
 * 图片预览
 * Extra:
 * key: GALLERY_URLS            values: mImgUrls照片urls
 * key: GALLERY_CURRENT_INDEX   values: 显示第几页
 */
public class GalleryActivity extends BaseActivity {
	private static String TAG = "GalleryActivity";
	public static String GALLERY_URLS = "gallery_urls";
	public static String GALLERY_DES = "pic_des";
	public static String GALLERY_CURRENT_INDEX = "gallery_current_index";

	private ViewPager myVp;
	//图片Url
	private String[] mImgUrls;
	//图片描述
	private String[] mImgDes;
	//当前页索引
	private int mCurrentItem;
	//图片View
	private ImageView[] mImaViews;
	//页码
	private TextView mTextView;
	//照片描述
	private TextView mTVPicDes;
	private ImageLoader imageLoader;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gallery);
		initDatas();
		initViews();
		setEvent();
	}

	private void setEvent() {
		myVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageSelected(int position) {
				int count = position + 1;

				mTextView.setText(count + "/" + mImgUrls.length);
				if (mImgDes!=null&&mImgDes.length>position){
					mTVPicDes.setText(mImgDes[position]);
				}else{
					mTVPicDes.setText("");
				}
				Log.i(TAG, "current item:" + count);
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void initViews() {
		mTextView = (TextView)findViewById(R.id.tv_gallery_number);
		mTVPicDes = (TextView)findViewById(R.id.tv_pic_des);

		myVp = (ViewPager) findViewById(R.id.myVp);
		myVp.setAdapter(new PagerAdapter() {

			@Override
			public Object instantiateItem(ViewGroup container, int position) {

				ZoomImageView imageView = new ZoomImageView(GalleryActivity.this);

				//ScaleImageView imageView = new ScaleImageView(getApplicationContext());
				//imageView.setImageResource(mImgs[position]);
				String imgUrl = mImgUrls[position];
				if (imgUrl != null && !imgUrl.equals("")) {
					imageLoader.get(imgUrl, ImageLoader.getImageListener(imageView, R.drawable.user_detail_header_bg, R.drawable.user_detail_header_bg));
				}
				container.addView(imageView);
				mImaViews[position] = imageView;
				return imageView;
			}

			@Override
			public void destroyItem(ViewGroup container, int position,
									Object object) {
				container.removeView(mImaViews[position]);
				mImaViews[position] = null;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mImaViews.length;
			}
		});

		//显示描述
		if (mImgDes!=null&&mImgDes.length>0){
			mTVPicDes.setText(mImgDes[mCurrentItem]);
			mTextView.setGravity(Gravity.RIGHT);
		} else {
			mTVPicDes.setText("");
		}

		//显示页码
		if (mImgUrls.length>0){
			int count = 0;
			if (mImgUrls.length>mCurrentItem){
				count = mCurrentItem+1;
			}
			mTextView.setText(count + "/" + mImgUrls.length);
		} else {
			mTextView.setText("0/0");
		}
		//指定显示页
		myVp.setCurrentItem(mCurrentItem);
	}

	private void initDatas() {
		Intent intent = getIntent();
		mImgUrls = intent.getStringArrayExtra(GALLERY_URLS);
		mCurrentItem = intent.getIntExtra(GALLERY_CURRENT_INDEX, 0);
		mImgDes = intent.getStringArrayExtra(GALLERY_DES);

		if (mImgDes==null){
			mImgDes = new String[0];
		}

		if (mImgUrls==null){
			mImgUrls = new String[0];
		}
		mImaViews = new ImageView[mImgUrls.length];
		imageLoader = new ImageLoader(MalaApplication.getHttpRequestQueue(), ImageCache.getInstance(MalaApplication.getInstance()));
	}
}
