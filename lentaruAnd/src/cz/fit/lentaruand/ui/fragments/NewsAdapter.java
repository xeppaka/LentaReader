package cz.fit.lentaruand.ui.fragments;

import java.util.Collection;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.News;

public class NewsAdapter extends NewsObjectAdapter<News> {
	
	private static class ViewHolder {
		private final TextView newsTitle;
		
		public ViewHolder(TextView newsTitle) {
			this.newsTitle = newsTitle;
		}
		
		public TextView getNewsTitle() {
			return newsTitle;
		}
	}

	public NewsAdapter(Context context, Collection<News> newsObjects) {
		super(context, newsObjects);
	}

	public NewsAdapter(Context context) {
		super(context);
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
//
//	@Override
//	public int getItemViewType(int position) {
//		return 0;
//	}
//	
//	@Override
//	public int getViewTypeCount() {
//		return 1;
//	}
}
