package com.malalaoshi.android.adapter;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.malalaoshi.android.R;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by kang on 16/5/6.
 */
public abstract class BaseRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private static final int TYPE_NORMAL = 0;
    private static final int TYPE_LOAD_MORE = 1;

    //上拉加载更多
    public static final int  PULLUP_LOAD_MORE=0;
    //正在加载中
    public static final int  LOADING_MORE=1;
    //没有更多数据,到底了
    public static final int NODATA_LOADING = 2;
    //没有更多数据,到底了
    public static final int GONE_LOADING = 3;
    //上拉加载更多状态-默认为0
    private int load_more_status=0;

    public static final int TEACHER_LIST_PAGE_SIZE = 10;


    private ImageLoader mImageLoader;


    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = null;

        switch(viewType){
            case TYPE_LOAD_MORE:
                return new LoadMoreViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.normal_refresh_footer, null));
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_item, null);
                return new NormalViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position){
        int type = TYPE_NORMAL;
        if (position==getItemCount()-1){
            type = TYPE_LOAD_MORE;
        }
        return type;
    }

    /**
     * //上拉加载更多
     * PULLUP_LOAD_MORE=0;
     * //正在加载中
     * LOADING_MORE=1;
     * //加载完成已经没有更多数据了
     * NO_MORE_DATA=2;
     * @param status
     */
    public void setMoreStatus(int status){
        load_more_status=status;
        notifyDataSetChanged();
    }

    public int getMoreStatus(){
        return load_more_status;
    }

    public class LoadMoreViewHolder extends BaseViewHolder {
        @Bind(R.id.item_load_more_icon_loading)
        protected View iconLoading;

        @Bind(R.id.iv_normal_refresh_footer_chrysanthemum)
        protected ImageView ivProgress;

        @Bind(R.id.tv_normal_refresh_footer_status)
        protected TextView tvStatusText;

        protected LoadMoreViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        protected void update(int position){
            if (position<=2){
                iconLoading.setVisibility(View.GONE);
            }else{
                iconLoading.setVisibility(View.VISIBLE);
            }
            AnimationDrawable animationDrawable = null;
            switch (load_more_status){
                case PULLUP_LOAD_MORE:
                    tvStatusText.setText("上拉加载更多...");
                    ivProgress.setVisibility(View.GONE);
                    animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
                    animationDrawable.stop();
                    break;
                case LOADING_MORE:
                    tvStatusText.setText("加载中...");
                    ivProgress.setVisibility(View.VISIBLE);
                    animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
                    animationDrawable.start();
                    break;
                case NODATA_LOADING:
                    tvStatusText.setText("到底了,没有更多数据了!");
                    ivProgress.setVisibility(View.GONE);
                    animationDrawable = (AnimationDrawable) ivProgress.getDrawable();
                    animationDrawable.stop();
                    break;
                case GONE_LOADING:
                    //iconLoading.setVisibility(View.GONE);
                    break;
            }

        }

    }


    public static class BaseViewHolder extends RecyclerView.ViewHolder {

        protected BaseViewHolder(View itemView) {
            super(itemView);
        }

        protected void update(int position){}
    }

    public class NormalViewHolder extends BaseRecyclerViewAdapter.BaseViewHolder {


        protected View view;

        protected NormalViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this, itemView);
            this.view = itemView;
        }

        @Override
        protected void update(int position){

        }
    }
}
