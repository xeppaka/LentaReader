package cz.fit.lentaruand.ui;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;

import cz.fit.lentaruand.Main;
import cz.fit.lentaruand.R;

public class ActionModeHelper implements ActionMode.Callback, AdapterView.OnItemLongClickListener {

	  SwipeNewsListFragment host; // здесь типа активити которая вызывает конструктор данного класса
	  ActionMode activeMode; // action mode, аналог контекстового меню
	  ListView newsList; // List View для управления переданного list view;
	  
	  
	public ActionModeHelper(SwipeNewsListFragment host,
			ListView newsList) {
		super();
		this.host = host;
		this.newsList = newsList;
	}

	@Override
	public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		MenuInflater menuInflater = host.getSherlockActivity().getSupportMenuInflater();
		menuInflater.inflate(R.menu.context_news_list, menu);
		mode.setTitle(R.string.action_mode_title);
		return true;
	}

	@Override
	public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		boolean result=
		        host.performAction(item.getItemId(),
		                           newsList.getCheckedItemPosition());

		    if (item.getItemId() == R.id.save || item.getItemId() == R.id.openNews) {
		      activeMode.finish();
		    }
		    
		    
		return result;
	}

	@Override
	public void onDestroyActionMode(ActionMode mode) {
		// TODO Auto-generated method stub
		activeMode=null;
	    newsList.clearChoices();
	    newsList.requestLayout();		
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> view, View row, int position,
			long id) {
		newsList.clearChoices();
	    newsList.setItemChecked(position, true);
	    
	    if (activeMode == null) {
	        activeMode=host.getSherlockActivity().startActionMode(this);
	      }

		return true;
	}

}
