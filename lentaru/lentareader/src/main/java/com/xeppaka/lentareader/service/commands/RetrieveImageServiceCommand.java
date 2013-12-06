package com.xeppaka.lentareader.service.commands;

import android.content.ContentResolver;
import android.os.ResultReceiver;

import com.xeppaka.lentareader.data.NewsObject;

@Deprecated
public abstract class RetrieveImageServiceCommand<T extends NewsObject> extends RunnableServiceCommand {
	private long newsId;
	private T newsObject;
	private ContentResolver contentResolver;
	
	public RetrieveImageServiceCommand(int requestId, T newsObject, ContentResolver contentResolver, ResultReceiver resultReceiver) {
		super(requestId, resultReceiver);
		
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
