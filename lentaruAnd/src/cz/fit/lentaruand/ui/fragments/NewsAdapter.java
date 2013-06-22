package cz.fit.lentaruand.ui.fragments;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.News;

public class NewsAdapter extends BaseAdapter {
	
	private List<News> news;
	private LayoutInflater inflater;
	
	private static class ViewHolder {
		private final TextView newsTitle;
		
		public ViewHolder(TextView newsTitle) {
			this.newsTitle = newsTitle;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}
	}
	
	public void setNews(List<News> news) {
		this.news = news;
	}
	
	public NewsAdapter(Context context, List<News> news) {
		this.news = news;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		return news.size();
	}

	@Override
	public News getItem(int position) {
		return news.get(position);
	}

	@Override
	public long getItemId(int position) {
		return getItem(position).hashCode();
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		TextView newsTitleTextView;
		
		if (convertView == null) {
			view = inflater.inflate(R.layout.brief_news_view, null);
			newsTitleTextView = (TextView)view.findViewById(R.id.brief_news_title);
			
			view.setTag(new ViewHolder(newsTitleTextView));
		} else {
			view = convertView;
			ViewHolder holder = (ViewHolder)view.getTag();
			
			newsTitleTextView = holder.getNewsTitle();
		}

		News news = getItem(position);
		
		newsTitleTextView.setText(news.getTitle());
		return view;
	}

	@Override
	public int getViewTypeCount() {
		return 1;
	}

	@Override
	public boolean hasStableIds() {
		return false;
	}

	@Override
	public boolean isEmpty() {
		return news.isEmpty();
	}

	@Override
	public boolean areAllItemsEnabled() {
		return true;
	}

	@Override
	public boolean isEnabled(int position) {
		return true;
	}
}
