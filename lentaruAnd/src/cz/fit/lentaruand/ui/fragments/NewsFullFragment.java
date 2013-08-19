package cz.fit.lentaruand.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.asyncloaders.AsyncFullNewsLoader;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.AsyncDao.DaoReadSingleListener;
import cz.fit.lentaruand.data.dao.BitmapReference.BitmapLoadListener;
import cz.fit.lentaruand.data.dao.ImageDao;
import cz.fit.lentaruand.data.dao.NewsDao;

public class NewsFullFragment extends Fragment implements LoaderManager.LoaderCallbacks<News> {

	private Context context;
	private News news;

	public NewsFullFragment(Context context, News news){
		this.context = context;
		this.news = news;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.full_news_fragment, null);
	}

	public NewsFullFragment() {
		super();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		getLoaderManager().initLoader(0, null, this).forceLoad();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	public void showNews(News news) {
		AsyncDao<News> nd = NewsDao.getInstance(getActivity().getContentResolver());		
		
		final TextView titleView = (TextView) getActivity().findViewById(R.id.full_news_title);
		TextView contentView = (TextView) getActivity().findViewById(R.id.full_news_content);
		final ImageView imageView = (ImageView) getActivity().findViewById(R.id.full_news_image);
		
		if (news.getFullText() != null) {
			contentView.setText(Html.fromHtml(news.getFullText()));
			contentView.setMovementMethod(LinkMovementMethod.getInstance());
		}
		
		nd.readAsync(news.getId(), new DaoReadSingleListener<News>() {
			@Override
			public void finished(News news) {
				titleView.setText(news.getTitle());
				
				news.getImage().getBitmapAsync(new BitmapLoadListener() {
					@Override
					public void onBitmapLoaded(Bitmap bitmap) {
						if (bitmap == null) {
							bitmap = ImageDao.getNotAvailableImage().getBitmap();
						}
						
						imageView.setImageBitmap(bitmap);
					}
				});
			}
		});
	}

	@Override
	public Loader<News> onCreateLoader(int id, Bundle args) {
		return new AsyncFullNewsLoader(context, news);
	}

	@Override
	public void onLoaderReset(Loader<News> loader) {
	}

	@Override
	public void onLoadFinished(Loader<News> arg0, News arg1) {
		showNews(arg1);
	}
}
