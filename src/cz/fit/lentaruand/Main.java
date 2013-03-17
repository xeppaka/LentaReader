package cz.fit.lentaruand;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import javax.xml.xpath.XPathExpressionException;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import cz.fit.lentaruand.data.NewsType;
import cz.fit.lentaruand.data.Rubrics;
import cz.fit.lentaruand.rss.RssItem;
import cz.fit.lentaruand.rss.RssReader;

public class Main extends Activity {

	private TextView tv;
	private Button b;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		tv = (TextView) findViewById(R.id.textView1);
		b = (Button) findViewById(R.id.button1);
		b.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new DownloadRssTask().execute(new DownloadRssTaskData(Rubrics.RUSSIA, NewsType.NEWS));
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
	
	private class DownloadRssTask extends AsyncTask<DownloadRssTaskData, Integer, Collection<RssItem>> {
		@Override
		protected Collection<RssItem> doInBackground(DownloadRssTaskData... params) {
			try {
				return RssReader.readItems(params[0].getRubric(), params[0].getNewsType());
			} catch (XPathExpressionException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return Collections.emptyList();
		}

		@Override
		protected void onPostExecute(Collection<RssItem> result) {
			StringBuilder sb = new StringBuilder();
			for (RssItem item : result) {
				sb.append(item.getLink()).append("\n");
			}
			
			tv.setText(sb.toString());
		}
	}
}
