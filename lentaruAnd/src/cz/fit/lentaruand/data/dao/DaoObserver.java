package cz.fit.lentaruand.data.dao;

import java.util.Collection;

import android.content.ContentUris;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import cz.fit.lentaruand.utils.LentaConstants;

public abstract class DaoObserver<T> implements Dao.Observer<T> {
	private ContentObserver contentObserver;
	private Dao<T> dao;
	
	public DaoObserver(Handler handler) {
		initialize(handler);
	}

	private void initialize(Handler handler) {
		contentObserver = new ContentObserver(handler) {
			@Override
			public void onChange(boolean selfChange, Uri uri) {
				long id;
				
				try {
					id = ContentUris.parseId(uri);
				} catch (NumberFormatException e) {
					id = -1;
				}
				
				if (id >= 0) {
					T dataObject = dao.read(id);
					
					if (dataObject == null) {
						// something went wrong...
						Log.w(LentaConstants.LoggerAnyTag, "DaoObserver triggered. Read object is null for uri: " + uri + ".");
						return;
					}
					
					onDataChanged(selfChange, dataObject);
				} else {
					this.onChange(selfChange);
				}
			}

			@Override
			public void onChange(boolean selfChange) {
				Collection<T> dataObjects = dao.read();
				
				if (dataObjects == null || dataObjects.isEmpty()) {
					// something went wrong...
					Log.w(LentaConstants.LoggerAnyTag, "DaoObserver triggered. Read collection of objects is null or empty.");
					return;
				}
				
				onDataChanged(selfChange, dataObjects);
			}
		};
	}
	
	void setDao(Dao<T> dao) {
		this.dao = dao;
	}
	
	ContentObserver getContentObserver() {
		return contentObserver;
	}
}
