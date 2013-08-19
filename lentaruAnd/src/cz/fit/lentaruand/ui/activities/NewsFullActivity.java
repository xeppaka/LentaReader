package cz.fit.lentaruand.ui.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.ui.fragments.NewsFullFragment;
import cz.fit.lentaruand.utils.LentaUtils;

public class NewsFullActivity extends SherlockFragmentActivity {
	private NewsFullFragment fullNewsFragment;
	private News news;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaUtils.strictMode();
		
		setContentView(R.layout.full_news_activity);
		news = (News)getIntent().getSerializableExtra("NewsObject");
		fullNewsFragment = new NewsFullFragment(getApplicationContext(), news);
		getSupportFragmentManager().beginTransaction().replace(R.id.full_news_fragment_container, fullNewsFragment).commit();
	}
}
