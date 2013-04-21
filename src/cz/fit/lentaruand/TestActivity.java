package cz.fit.lentaruand;

import java.util.Collections;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import cz.fit.lentaruand.data.News;
import cz.fit.lentaruand.fragments.NewsBriefListFragment;

public class TestActivity extends FragmentActivity {
	private NewsBriefListFragment briefFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		briefFragment = new NewsBriefListFragment(getApplicationContext(), Collections.<News>emptyList());
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, briefFragment).commit();
	}
}
