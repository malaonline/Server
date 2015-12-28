package com.malalaoshi.android;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by zl on 15/12/25.
 */
public class RecyclerViewEvent extends RecyclerView{
    public int mScrollY = 0;
    private boolean mEating = false;

    public RecyclerViewEvent(Context context){
        this(context, null);
    }

    public RecyclerViewEvent(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public RecyclerViewEvent(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
        addOnScrollListener(new OnScrollListener(){
            @Override 
            public void onScrollStateChanged(RecyclerView recyclerView, int newState){
                super.onScrollStateChanged(recyclerView, newState);
                switch (newState){
                    case SCROLL_STATE_IDLE:
                        mEating = false;
                        break;
                    case SCROLL_STATE_DRAGGING:
                        mEating = true;
                        break;
                }
            }


            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);
                mScrollY += dy;
            }
        });
    }


    @Override 
    public boolean dispatchTouchEvent(MotionEvent ev){
        return super.dispatchTouchEvent(ev);
    }


    /**
     * @return false, if sdk_int < 21. else return getClipToPadding();
     */
    private boolean getClipToPaddingCompat(){
        if(Build.VERSION.SDK_INT < 21){
            return getLayoutManager() != null && getLayoutManager().getClipToPadding();
        }else{
            return getClipToPadding();
        }
    }
}
