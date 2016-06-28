package com.malalaoshi.android.core.utils;

import android.graphics.Bitmap;
import android.util.Log;

import com.malalaoshi.android.core.MalaContext;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;

/**
 * 数据保存
 * Created by tianwei on 16-6-28.
 */
public class DataUtils {

    private static final String IMAGE_SUFFIX = ".jpg";

    /**
     * @param url 图片url
     */
    public static File getImage(String url) {
        return new File(getCacheDir(), getMD5(url) + IMAGE_SUFFIX);
    }

    public static void saveFile(Bitmap bm, String url) throws IOException {
        try {
            String name = getMD5(url).replace("-", "");
            if (EmptyUtils.isEmpty(name)) {
                return;
            }
            name += IMAGE_SUFFIX;
            File file = new File(getCacheDir(), name);
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
            bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            bos.flush();
            bos.close();
        } catch (Exception e) {
            Log.i("Mala", "getImage error: " + e.getMessage());
        }
    }

    private static File getCacheDir() {
        return MalaContext.getContext().getExternalCacheDir();
    }

    public static String getMD5(String val) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(val.getBytes());
            byte[] m = md5.digest();
            return getString(m).replace("-", "");
        } catch (Exception e) {
            Log.i("Mala", "getMD5 error: " + e.getMessage());
        }
        return "";
    }

    private static String getString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte c : b) {
            sb.append(c);
        }
        return sb.toString();
    }
}
