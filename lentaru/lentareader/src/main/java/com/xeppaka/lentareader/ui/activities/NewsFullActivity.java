package com.xeppaka.lentareader.ui.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.xeppaka.lentareader.R;
import com.xeppaka.lentareader.ui.fragments.NewsFullFragment;
import com.xeppaka.lentareader.utils.LentaDebugUtils;

public class NewsFullActivity extends ActionBarActivity {
	private NewsFullFragment fullNewsFragment;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

        LentaDebugUtils.strictMode();

        setTitle(null);
        //getActionBar().setIcon(R.drawable.lenta_icon);
        getSupportActionBar().setLogo(R.drawable.ab_lenta_icon);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		setContentView(R.layout.full_news_activity);
		long newsId = getIntent().getLongExtra("newsId", -1);
		
		if (newsId >= 0) {
			fullNewsFragment = new NewsFullFragment(newsId);
			getSupportFragmentManager().beginTransaction().replace(R.id.full_news_fragment_container, fullNewsFragment).commit();
		}
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
