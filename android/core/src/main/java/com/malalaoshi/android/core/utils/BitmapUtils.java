package com.malalaoshi.android.core.utils;


import android.annotation.TargetApi;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import com.malalaoshi.android.core.MalaContext;
import com.squareup.picasso.Picasso;

import java.io.File;

/**
 * 高斯模糊
 * Created by tianwei on 16-6-27.
 */
public class BitmapUtils {

    public static Bitmap blurBitmap(String url) {
        String path = "";
        if (url.contains("?")) {
            path = url.split("\\?")[0];
        }
        File cacheFile = DataUtils.getImage(path);
        if (cacheFile.exists() && cacheFile.isFile()) {
            return BitmapFactory.decodeFile(cacheFile.getAbsolutePath());
        }
        try {
            Bitmap bitmap = Picasso.with(MalaContext.getContext()).load(Uri.parse(url)).get();
            Bitmap result = blurBitmap(bitmap);
            bitmap.recycle();
            //保存cache
            if (result != null) {
                DataUtils.saveFile(result, path);
            }
            return result;
        } catch (Exception e) {
            Log.i("Mala", "Picasso load image failed: " + e.getMessage());
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap blurBitmap(Bitmap sentBitmap) {
        if (Build.VERSION_CODES.JELLY_BEAN_MR1 > Build.VERSION.SDK_INT) {
            return null;
        }
        try {
            Bitmap bitmap = sentBitmap.copy(sentBitmap.getConfig(), true);

            final RenderScript rs = RenderScript.create(MalaContext.getContext());
            final Allocation input = Allocation
                    .createFromBitmap(rs, sentBitmap, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
            final Allocation output = Allocation.createTyped(rs, input.getType());
            final ScriptIntrinsicBlur script;
            script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
            script.setRadius(5.0f);
            script.setInput(input);
            script.forEach(output);
            output.copyTo(bitmap);
            return bitmap;
        } catch (Exception e) {
            Log.i("mala", "Blur bitmap failed: ");
        }
        return null;
    }
}
