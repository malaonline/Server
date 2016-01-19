package com.malalaoshi.android.decoration;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.View;

import com.malalaoshi.android.R;

/**
 * Created by zl on 15/12/10.
 */
public class TeacherListGridItemDecoration extends RecyclerView.ItemDecoration{
    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};
    private Drawable mDivider;

    private Context mContext;
    //行间距
    private int mSpace = 0;

    //阴影宽度
    int cardElevation = -1;

    private int mLeftPadding = -1;
    private int mRightPadding = -1;
    private int mTopPadding = -1;
    private int mBottomPadding = -1;

    /**
     *构造函数
     * @param context 上下文
     * @param space   各个Item的行间距
     */
    public TeacherListGridItemDecoration(Context context, int space){
        mContext = context;
        this.mSpace = space;
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public void setItemSpace(int space) {
        this.mSpace = space;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state){
//        drawHorizontal(c, parent);
//        drawVertical(c, parent);
    }

    private int getSpanCount(RecyclerView parent){
        int spanCount = -1;
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            spanCount = ((GridLayoutManager) layoutManager).getSpanCount();
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            spanCount = ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        }
        return spanCount;
    }

    public void drawHorizontal(Canvas c, RecyclerView parent){
        int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int left = child.getLeft() - params.leftMargin;
            final int right = child.getRight() + params.rightMargin + mDivider.getIntrinsicWidth();
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
    public void drawVertical(Canvas c, RecyclerView parent){
        final int childCount = parent.getChildCount();
        for(int i = 0; i < childCount; i++){
            final View child = parent.getChildAt(i);

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams)child.getLayoutParams();
            final int top = child.getTop() - params.topMargin;
            final int bottom = child.getBottom() + params.bottomMargin;
            final int left = child.getRight() + params.rightMargin;
            final int right = left + mDivider.getIntrinsicWidth();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }

    private boolean isLastColumn(RecyclerView parent, int pos, int spanCount, int childCount){
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            if((pos + 1) % spanCount == 0){
                return true;
            }
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if(orientation == StaggeredGridLayoutManager.VERTICAL){
                if((pos + 1) % spanCount == 0){
                    return true;
                }
            }else{
                childCount = childCount - childCount % spanCount;
                if(pos >= childCount)
                    return true;
            }
        }
        return false;
    }

    private boolean isLastRaw(RecyclerView parent, int pos, int spanCount, int childCount){
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();
        if(layoutManager instanceof GridLayoutManager){
            childCount = childCount - childCount % spanCount;
            if(pos >= childCount)
                return true;
        }else if(layoutManager instanceof StaggeredGridLayoutManager){
            int orientation = ((StaggeredGridLayoutManager) layoutManager).getOrientation();
            if(orientation == StaggeredGridLayoutManager.VERTICAL){
                childCount = childCount - childCount % spanCount;
                if(pos >= childCount)
                    return true;
            }else{
                if((pos + 1) % spanCount == 0){
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state){
       /* if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP){
            //获取阴影的宽度
            if (cardElevation==-1){
                cardElevation = parent.getContext().getResources().getDimensionPixelSize(R.dimen.teacher_list_card_elevation);
            }

            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();
            int itemPosition = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

            //是否是最后一行int left, int top, int right, int bottom
            int amendSpace = mSpace - 2*cardElevation;
            if(isLastRaw(parent, itemPosition, spanCount, childCount)){
                if(isLastColumn(parent, itemPosition, spanCount, childCount)){
                    outRect.set( amendSpace/2,  amendSpace, 0, amendSpace);
                }else{
                    outRect.set(0, amendSpace, amendSpace/2, amendSpace);
                }
            }else if(isLastColumn(parent, itemPosition, spanCount, childCount)){
                outRect.set( amendSpace/2,  amendSpace, 0, 0);
            }else{
                outRect.set(0, amendSpace, amendSpace/2, 0);
            }
        }else{
            int spanCount = getSpanCount(parent);
            int childCount = parent.getAdapter().getItemCount();
            int itemPosition = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();

            if(isLastRaw(parent, itemPosition, spanCount, childCount)){
                if(isLastColumn(parent, itemPosition, spanCount, childCount)){
                    outRect.set( mSpace/2 ,  mSpace, 0, mSpace);
                }else{
                    outRect.set(0, mSpace, mSpace/2 , mSpace);
                }
            }else if(isLastColumn(parent, itemPosition, spanCount, childCount)){
                outRect.set( mSpace/2 ,  mSpace, 0, 0);
            }else{
                outRect.set(0, mSpace, mSpace/2 , 0);
            }
        }*/
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int itemPosition = ((GridLayoutManager.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        int top = 0;
        Log.e("aaaaaaaa",""+itemPosition);
        if (itemPosition==0){
            top += mSpace/2;
            outRect.set( mSpace/2 ,  mSpace, top, mSpace);
        }else{

        }
    }

}
