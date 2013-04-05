package cz.fit.lentaruand;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.data.dao.NewsDao;
import cz.fit.lentaruand.data.db.LentaDbHelper;
import cz.fit.lentaruand.parser.exceptions.PageParseException;
import cz.fit.lentaruand.site.LentaNewsDownloader;

public class Main extends Activity {

	private TextView tv;
	private Button createNews;
	private Button deleteNews;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		tv = (TextView) findViewById(R.id.textView1);
		createNews = (Button) findViewById(R.id.button1);
		createNews.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new DownloadRssTask().execute(new DownloadRssTaskData(Rubrics.RUSSIA, NewsType.NEWS));
			}
		});
		
		deleteNews = (Button) findViewById(R.id.button2);
		deleteNews.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	private static class DownloadRssTaskData {
		Rubrics rubric;
		NewsType newsType;
		
		public DownloadRssTaskData(Rubrics rubric, NewsType newsType) {
			this.rubric = rubric;
			this.newsType = newsType;
		}

		public Rubrics getRubric() {
			return rubric;
		}
		
		public NewsType getNewsType() {
			return newsType;
		}
	}
	
	private class DownloadRssTask extends AsyncTask<DownloadRssTaskData, Integer, Collection<News>> {
		@Override
		protected Collection<News> doInBackground(DownloadRssTaskData... params) {
			LentaNewsDownloader lnd = new LentaNewsDownloader();
			
			try {
				Collection<News> news = lnd.downloadRubricBrief(Rubrics.WORLD);
				Iterator<News> it = news.iterator();
				
				while (it.hasNext()) {
					lnd.downloadFull(it.next());
				}
				
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (PageParseException e) {
				e.printStackTrace();
			}
			
			return Collections.emptyList();
		}

		@Override
		protected void onPostExecute(Collection<News> result) {
			Iterator<News> it = result.iterator();

			SQLiteDatabase db = new LentaDbHelper(Main.this.getApplicationContext()).getWritableDatabase();
			
			while (it.hasNext()) {
				NewsDao.create(db, it.next());
			}
			
			tv.setText("Finished");
		}
	}
}
