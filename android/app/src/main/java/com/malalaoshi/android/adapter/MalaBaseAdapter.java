package com.malalaoshi.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Mala base adapter
 * Created by tianwei on 1/24/16.
 */
public abstract class MalaBaseAdapter<T> extends BaseAdapter {

    private List<T> list;
    protected Context context;

    public MalaBaseAdapter(Context context) {
        this.context = context;
        list = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<T> getList() {
        return list;
    }

    public void add(T t) {
        if (t != null) {
            list.add(t);
        }
    }

    public void add(int position, T t) {
        if (position >= 0 && position < list.size()) {
            list.add(t);
        }
    }

    public void addAll(Collection<T> entities) {
        if (entities != null && entities.size() > 0)
            list.addAll(entities);
    }

    public void addAll(int position, Collection<T> entities) {
        if (position >= 0 && position < list.size() && entities != null && entities.size() > 0) {
            list.addAll(entities);
        }
    }

    public void clear() {
        list.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = createView(position, parent);
        }
        fillView(position, convertView, list.get(position));
        return convertView;
    }

    protected abstract View createView(int position, ViewGroup parent);

    protected abstract void fillView(int position, View convertView, T data);
}
