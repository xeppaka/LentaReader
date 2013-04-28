package cz.fit.lentaruand;

import android.os.Bundle;
import android.view.Menu;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.fragments.NewsFullFragment;

public class FullNewsActivity extends SherlockFragmentActivity {

	
	private NewsFullFragment fullNewsFragment;
	private News news;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_full_news);
		news = (News)getIntent().getSerializableExtra("NewsObject");
		fullNewsFragment =  new NewsFullFragment(getApplicationContext(), news);
		getSupportFragmentManager().beginTransaction().add(R.id.full_news_fragment_container, fullNewsFragment).commit();

	}
}
