package com.xeppaka.lentareader.data.dao.daoobjects;

import android.database.ContentObserver;
import android.os.Handler;

import com.xeppaka.lentareader.data.dao.Dao;
import com.xeppaka.lentareader.data.dao.DaoObservable;

public abstract class DaoObserver<T> implements DaoObservable.Observer<T> {
	private ContentObserver contentObserver;
	private Dao<T> dao;
	
	public DaoObserver(Handler handler) {
        contentObserver = new ContentObserver(handler) {
            /**
             * this code added at API level 16
             */

//			@Override
//			public void onChange(boolean selfChange, Uri uri) {
//				long id;
//
//				try {
//					id = ContentUris.parseId(uri);
//				} catch (NumberFormatException e) {
//					id = -1;
//				}
//
//				if (id >= 0) {
//					T dataObject = dao.read(id);
//
//					if (dataObject == null) {
//						// something went wrong...
//						Log.w(LentaConstants.LoggerAnyTag, "DaoObserver triggered. Read object is null for uri: " + uri + ".");
//						return;
//					}
//
//					onDataChanged(selfChange, dataObject);
//				} else {
//					this.onChange(selfChange);
//				}
//			}

            @Override
            public void onChange(boolean selfChange) {
                onDataChanged(selfChange);
            }
        };
	}

//	void setDao(Dao<T> dao) {
//		this.dao = dao;
//	}
//

	ContentObserver getContentObserver() {
		return contentObserver;
	}
}
