package com.malalaoshi.android.core.image.glide;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.malalaoshi.android.core.R;
import com.orhanobut.hawk.Hawk;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;

/**
 * Glide utils
 * 动态URL： https://s3.cn-north-1.amazonaws.com.cn/dev-upload/avatars/img2_tb65fFw.jpg?X-Amz-Date=20160821T041623Z&X-Amz-Signature=4b9ec24a31c7a9a212f95b2afc2d89bc62c4bba74dff5d58ed5ddadff97d6ee0&X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-SignedHeaders=host&X-Amz-Credential=AKIAP22CWKUZDOMHLFGA%2F20160821%2Fcn-north-1%2Fs3%2Faws4_request&X-Amz-Expires=86400
 * Created by tianwei on 8/21/16.
 */
public class GlideUtils {

    private static final String SPLIT_FLAG = "\\?";

    private static final int IMG_NORMAL = 0;
    private static final int IMG_CIRCLE = 1;
    private static final int IMG_BLUR = 2;

    /**
     * 如果请求过，从缓存拿到地址把图片取出来，不然还是用原地址取。
     */
    private static void load(final RequestModel data) {
        String url = data.getUrl();
        final Context context = data.getContext();
        // cacheKey是去掉了参数的url
        final String cacheKey = getCacheUrl(url);
        //检查缓存
        if (!TextUtils.isEmpty(cacheKey)) {
            String cacheUrl = Hawk.get(cacheKey);
            if (!TextUtils.isEmpty(cacheUrl)) {
                url = cacheUrl;
            }
        }
        Log.i("MALA", url + " ");
        final String finalRequestUrl = url;
        if (TextUtils.isEmpty(finalRequestUrl)) {
            return;
        }
        DrawableRequestBuilder<String> builder = Glide.with(context)
                .load(finalRequestUrl)
                .listener(new RequestListener<String, GlideDrawable>() {
                    @Override
                    public boolean onException(Exception e
                            , String model
                            , Target<GlideDrawable> target
                            , boolean isFirstResource) {
                        if (!finalRequestUrl.equals(data.getUrl())) {
                            //缓存加载失败了，再请求一次
                            Log.d("MALA", "load cache image failed");
                            Hawk.remove(cacheKey);
                            load(data);
                        }
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideDrawable resource
                            , String model
                            , Target<GlideDrawable> target
                            , boolean isFromMemoryCache, boolean isFirstResource) {
                        //加载成功，写入缓存表
                        if (finalRequestUrl.equals(data.getUrl())) {
                            Hawk.put(cacheKey, finalRequestUrl);
                        } else {
                            Log.d("MALA", "load cache image success");
                        }
                        return false;
                    }
                });
        if (data.getType() == IMG_CIRCLE) {
            builder.bitmapTransform(new CropCircleTransformation(context))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .crossFade()
                    .placeholder(data.getDefImage())
                    .into(data.getImageView());
        } else if (data.getType() == IMG_BLUR) {
            builder.bitmapTransform(new BlurTransformation(data.getContext()))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.core__teacher_banner)
                    .centerCrop()
                    .crossFade()
                    .into(data.getImageView());
        } else {
            builder.placeholder(data.getDefImage())
                    .crossFade()
                    .into(data.getImageView());
        }
    }

    private static String getCacheUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return "";
        }
        String[] urls = url.split(SPLIT_FLAG);
        return urls.length > 0 ? urls[0] : "";
    }

    public static void loadImage(Context context, String url, ImageView imageView) {
        RequestModel model = new RequestModel(context, url, imageView, IMG_NORMAL);
        load(model);
    }

    public static void loadImage(Context context, String url, ImageView imageView, int defImage) {
        RequestModel model = new RequestModel(context, url, imageView, IMG_NORMAL);
        model.setDefImage(defImage);
        load(model);
    }

    public static void loadCircleImage(Context context, String url, ImageView imageView, int defaultImg) {
        RequestModel model = new RequestModel(context, url, imageView, IMG_CIRCLE);
        model.setDefImage(defaultImg);
        load(model);
    }

    public static void loadBlurImage(Context context, String url, ImageView imageView, int defaultImg) {
        RequestModel model = new RequestModel(context, url, imageView, IMG_BLUR);
        model.setDefImage(defaultImg);
        load(model);
    }

    public static void loadBitmapImage(Context context, String url, ImageView imageView, int defImage, int errImage) {
        // cacheKey是去掉了参数的url
        final String cacheKey = getCacheUrl(url);
        String requestUrl = "";
        //检查缓存
        if (!TextUtils.isEmpty(cacheKey)) {
            String cacheUrl = Hawk.get(cacheKey);
            requestUrl = TextUtils.isEmpty(cacheUrl) ? url : cacheUrl;
        }
        Glide.with(context)
                .load(requestUrl)
                .asBitmap()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(defImage)
                .error(errImage)
                .into(imageView);
    }

    private static final class RequestModel {
        private Context context;
        private String url;
        private ImageView imageView;
        private int defImage;
        private int errImage;
        private int type;

        public RequestModel(Context context, String url, ImageView imageView, int type) {
            setContext(context);
            setUrl(url);
            setImageView(imageView);
            setType(type);
        }

        public Context getContext() {
            return context;
        }

        public void setContext(Context context) {
            this.context = context;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public void setImageView(ImageView imageView) {
            this.imageView = imageView;
        }

        public int getDefImage() {
            return defImage;
        }

        public void setDefImage(int defImage) {
            this.defImage = defImage;
        }

        public int getErrImage() {
            return errImage;
        }

        public void setErrImage(int errImage) {
            this.errImage = errImage;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }
    }
}
