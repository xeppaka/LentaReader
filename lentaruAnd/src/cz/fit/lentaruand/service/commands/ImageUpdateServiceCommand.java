package cz.fit.lentaruand.service.commands;

import android.content.ContentResolver;
import android.os.ResultReceiver;
import cz.fit.lentaruand.data.NewsObject;

public abstract class ImageUpdateServiceCommand<T extends NewsObject> extends RunnableServiceCommand {
	private long newsId;
	private T newsObject;
	private ContentResolver contentResolver;
	
	public ImageUpdateServiceCommand(int requestId, T newsObject, ContentResolver contentResolver, ResultReceiver resultReceiver, boolean reportError) {
		super(requestId, resultReceiver, reportError);
		
		if (newsObject == null) {
			throw new NullPointerException("newsObject is null.");
		}
		
		if (contentResolver == null) {
			throw new NullPointerException("contentResolver is null.");
		}
		
		this.newsObject = newsObject;
		this.contentResolver = contentResolver;
	}
	
	public ContentResolver getContentResolver() {
		return contentResolver;
	}
	
	public long getNewsId() {
		return newsId;
	}

	public T getNewsObject() {
		return newsObject;
	}
}
