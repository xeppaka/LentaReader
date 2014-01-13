package com.xeppaka.lentareader.ui.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.ui.fragments.NewsFullFragment;
import com.xeppaka.lentareader.utils.LentaDebugUtils;

public class NewsFullActivity extends FragmentActivity {
	private NewsFullFragment fullNewsFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LentaDebugUtils.strictMode();
		
		setContentView(R.layout.full_news_activity);
		long newsId = getIntent().getLongExtra("newsId", -1);
		
		if (newsId >= 0) {
			fullNewsFragment = new NewsFullFragment(newsId);
			getSupportFragmentManager().beginTransaction().replace(R.id.full_news_fragment_container, fullNewsFragment).commit();
		}
	}
}
