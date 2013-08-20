package cz.fit.lentaruand.ui.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.dao.AsyncDao;
import cz.fit.lentaruand.data.dao.AsyncDao.DaoReadSingleListener;
import cz.fit.lentaruand.data.dao.BitmapReference.BitmapLoadListener;
import cz.fit.lentaruand.data.dao.ImageDao;
import cz.fit.lentaruand.data.dao.NewsDao;

public class NewsFullFragment extends Fragment /*implements LoaderManager.LoaderCallbacks<News>*/ {

	private Context context;
	private long newsId;

	public NewsFullFragment(Context context, long newsId){
		this.context = context;
		this.newsId = newsId;
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
		//getLoaderManager().initLoader(0, null, this).forceLoad();
		
		AsyncDao<News> nd = NewsDao.getInstance(getActivity().getContentResolver());
		nd.readAsync(newsId, new DaoReadSingleListener<News>() {
			@Override
			public void finished(News result) {
				showNews(result);
			}
		});
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	private void showNews(News news) {
		final TextView titleView = (TextView) getActivity().findViewById(R.id.full_news_title);
		TextView contentView = (TextView) getActivity().findViewById(R.id.full_news_content);
		final ImageView imageView = (ImageView) getActivity().findViewById(R.id.full_news_image);
		
		if (news.getFullText() != null) {
			contentView.setText(Html.fromHtml(news.getFullText()));
			contentView.setMovementMethod(LinkMovementMethod.getInstance());
		}
		
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
//
//	@Override
//	public Loader<News> onCreateLoader(int id, Bundle args) {
//		return new AsyncFullNewsLoader(context, news);
//	}
//
//	@Override
//	public void onLoaderReset(Loader<News> loader) {
//	}
//
//	@Override
//	public void onLoadFinished(Loader<News> arg0, News arg1) {
//		showNews(arg1);
//	}
}
