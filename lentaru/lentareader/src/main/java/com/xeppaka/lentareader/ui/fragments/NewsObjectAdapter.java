package com.xeppaka.lentareader.ui.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.xeppaka.lentareader.data.NewsObject;
import com.xeppaka.lentareader.data.dao.daoobjects.ImageDao;

import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class NewsObjectAdapter<T extends NewsObject> extends BaseAdapter {
    protected List<T> currentObjects = Collections.emptyList();
    protected Set<Long> expandedItems;
    protected ImageDao imageDao;
	protected LayoutInflater inflater;

	public NewsObjectAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        imageDao = ImageDao.newInstance();
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

    public void setNewsObjects(List<T> newsObjects, Set<Long> expandedItems) {
        this.currentObjects = newsObjects;
        this.expandedItems = expandedItems;
    }

    public List<T> getCurrentObjects() {
        return currentObjects;
    }

    public Set<Long> getExpandedItems() {
        return expandedItems;
    }

    public ImageDao getImageDao() {
        return imageDao;
    }

    public int size() {
        return currentObjects.size();
    }
}
