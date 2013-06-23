package cz.fit.lentaruand.ui.fragments;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import cz.fit.lentaruand.data.NewsObject;

public abstract class NewsObjectAdapter<T extends NewsObject> extends BaseAdapter {

	protected List<T> newsObjects;
	protected LayoutInflater inflater;

	public NewsObjectAdapter(Context context) {
		this(context, Collections.<T>emptyList());
	}
	
	public NewsObjectAdapter(Context context, Collection<T> newsObjects) {
		if (newsObjects == null) {
			throw new IllegalArgumentException("Argument newsObjects must not be null.");
		}
		
		this.newsObjects = new ArrayList<T>();
		this.newsObjects.addAll(newsObjects);
		
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return newsObjects.size();
	}

	@Override
	public T getItem(int position) {
		return newsObjects.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}
	
	public void setNewsObjects(Collection<T> newsObjects) {
		if (newsObjects == null) {
			throw new IllegalArgumentException("Argument newsObjects must not be null.");
		}
		
		this.newsObjects.clear();
		this.newsObjects.addAll(newsObjects);
	}
	
	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return newsObjects.isEmpty();
	}
}
