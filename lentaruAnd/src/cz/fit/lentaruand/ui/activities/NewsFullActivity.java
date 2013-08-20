package cz.fit.lentaruand.ui.activities;

import android.os.Bundle;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.fit.lentaruand.R;
import cz.fit.lentaruand.ui.fragments.NewsFullFragment;
import cz.fit.lentaruand.utils.LentaUtils;

public class NewsFullActivity extends SherlockFragmentActivity {
	private NewsFullFragment fullNewsFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaUtils.strictMode();
		
		setContentView(R.layout.full_news_activity);
		long newsId = getIntent().getLongExtra("newsId", -1);
		
		if (newsId >= 0) {
			fullNewsFragment = new NewsFullFragment(getApplicationContext(), newsId);
			getSupportFragmentManager().beginTransaction().replace(R.id.full_news_fragment_container, fullNewsFragment).commit();
		}
	}
}
