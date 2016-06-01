package com.malalaoshi.android.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;

import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

/**
 * Created by kang on 16/6/1.
 */
public class FrescoUtil {

    public static void initRectangleView(Context context,SimpleDraweeView sdv, int placeholderResId, int failureResId){
        if (sdv==null){
            return;
        }
        //获取GenericDraweeHierarchy对象
        GenericDraweeHierarchy hierarchy = new GenericDraweeHierarchyBuilder(context.getResources())
                //设置淡入淡出动画持续时间
                .setFadeDuration(1000)
                //设置占位图及它的缩放类型
                .setPlaceholderImage(ContextCompat.getDrawable(context, placeholderResId), ScalingUtils.ScaleType.FOCUS_CROP)
                 //设置失败图及其缩放类型
                .setFailureImage(ContextCompat.getDrawable(context, failureResId), ScalingUtils.ScaleType.FOCUS_CROP)
               //构建
                .build();
        //设置GenericDraweeHierarchy
        sdv.setHierarchy(hierarchy);
    }
}
