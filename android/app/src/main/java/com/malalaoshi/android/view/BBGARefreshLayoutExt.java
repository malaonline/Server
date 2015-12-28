package com.malalaoshi.android.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import cn.bingoogolapple.refreshlayout.BGARefreshLayout;

/**
 * Created by zl on 15/12/25.
 */
public class BBGARefreshLayoutExt extends BGARefreshLayout{
    public BBGARefreshLayoutExt(Context context){
        super(context);
    }
    public BBGARefreshLayoutExt(Context context, AttributeSet attrs){
        super(context, attrs);
    }
    @Override
    public boolean onTouchEvent(MotionEvent event){
        super.onTouchEvent(event);
        System.out.println(this);
//        if(event.getAction() == 2){
//            super.beginRefreshing();
//        }
        return false;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev){
        System.out.println("外...............................................................父节点::::::::"+ev.getY());
        boolean xx = super.dispatchTouchEvent(ev);
        System.out.println("外:"+xx);
        System.out.println("...............................................................父节点end");
        if(ev.getActionMasked() == MotionEvent.ACTION_MOVE){
//            System.out.println("外.........移动.......");
            return false;
        }
        return xx;
    }
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean sup = super.onInterceptTouchEvent(event);
        System.out.println("外...==========" + sup);
        return sup;
    }
}
