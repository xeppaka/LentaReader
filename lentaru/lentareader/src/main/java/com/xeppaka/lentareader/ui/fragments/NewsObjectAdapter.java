package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.xeppaka.lentareader.data.NewsObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class NewsObjectAdapter<T extends NewsObject> extends BaseAdapter {
    protected List<T> currentObjects = Collections.emptyList();
	protected LayoutInflater inflater;

	public NewsObjectAdapter(Context context) {
        inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
        return currentObjects.size();
	}

	@Override
	public T getItem(int position) {
        return currentObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).getId();
	}
	
	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isEmpty() {
		return currentObjects.isEmpty();
	}

    public void setNewsObjects(List<T> newsObjects) {
        currentObjects = newsObjects;
        notifyDataSetChanged();
    }
}
