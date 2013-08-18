package cz.fit.lentaruand.ui.activities;

import android.os.Bundle;
import android.os.StrictMode;

import com.actionbarsherlock.app.SherlockFragmentActivity;

import cz.fit.lentaruand.R;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.ui.fragments.NewsFullFragment;
import cz.fit.lentaruand.utils.LentaConstants;

public class NewsFullActivity extends SherlockFragmentActivity {
	private NewsFullFragment fullNewsFragment;
	private News news;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if (LentaConstants.SDK_VER > 10 && LentaConstants.DEVELOPER_MODE) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectDiskReads().detectDiskWrites().detectNetwork() 
					.penaltyLog().build());
			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.detectLeakedSqlLiteObjects().detectLeakedClosableObjects()
					.penaltyLog().penaltyDeath().build());
		}
		
		setContentView(R.layout.full_news_activity);
		news = (News)getIntent().getSerializableExtra("NewsObject");
		fullNewsFragment =  new NewsFullFragment(getApplicationContext(), news);
		getSupportFragmentManager().beginTransaction().add(R.id.full_news_fragment_container, fullNewsFragment).commit();
	}
}
