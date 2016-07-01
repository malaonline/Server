package com.malalaoshi.android.core.base;

import android.content.Context;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * 基础adapter
 * Created by tianwei on 16-6-12.
 */
public abstract class BaseRecycleAdapter<T extends RecyclerView.ViewHolder, D> extends RecyclerView.Adapter<T> {

    protected Context context;

    private List<D> dataList;

    public BaseRecycleAdapter(Context context) {
        dataList = new ArrayList<>();
        this.context = context;
    }

    public List<D> getDataList() {
        return dataList;
    }

    public D getItem(int position) {
        return dataList.get(position);
    }

    public void clear(){
        dataList.clear();
        notifyDataSetChanged();
    }

    public void addData(List<D> data) {
        if (data == null) {
            return;
        }
        dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void insertData(List<D> data) {
        if (data == null) {
            return;
        }
        dataList.addAll(0,data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

}
