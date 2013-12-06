package com.xeppaka.lentareader.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.ResultReceiver;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

@Deprecated
public class ServiceIntentBuilder {
	public static final int NO_REQUEST_ID = -1;
	
	private Context context;
	private LentaService.Action action;
	private NewsType newsType;
	private int requestId;
	private ResultReceiver resultReceiver;
	private Rubrics rubric;

	public enum ServiceIntentKey {
		SERVICE_ACTION,
		NEWS_TYPE,
		ITEM_ID,
		REQUEST_ID,
		RUBRIC,
		RESULT_RECEIVER
	}
	
	public ServiceIntentBuilder(Context context, int requestId) {
		this.requestId = requestId;
		this.context = context;
	}
	
	public ServiceIntentBuilder forAction(LentaService.Action action) {
		this.action = action;
		return this;
	}
	
	public ServiceIntentBuilder forNewsType(NewsType newsType) {
		this.newsType = newsType;
		return this;
	}
	
	public ServiceIntentBuilder forRubric(Rubrics rubric) {
		this.rubric = rubric;
		return this;
	}
	
	public ServiceIntentBuilder forResultReceiver(ResultReceiver resultReceiver) {
		this.resultReceiver = resultReceiver;
		
		return this;
	}
	
	public Intent build() {
		switch (action) {
		case UPDATE_RUBRIC:
		    return buildUpdateRubric();
        default:
		    break;
		}
		 
		 throw new AssertionError();
	}
	
	private Intent buildUpdateRubric() {
		Intent result = new Intent(LentaService.Action.UPDATE_RUBRIC.name(), Uri.EMPTY, context, LentaService.class);
		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
		result.putExtra(ServiceIntentKey.RUBRIC.name(), rubric.name());

		if (resultReceiver != null) {
			result.putExtra(ServiceIntentKey.RESULT_RECEIVER.name(), resultReceiver);
		}
		
		return result;
	}
	
//	private Intent buildSyncRubric() {
//		if (newsType == null) {
//			throw new NullPointerException("newsType is null.");
//		}
//
//		if (rubric == null) {
//			throw new NullPointerException("rubric is null.");
//		}
//
//		Intent result = new Intent(context, LentaService.class);
//		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
//		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.SYNC_RUBRIC.name());
//		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
//		result.putExtra(ServiceIntentKey.RUBRIC.name(), rubric.name());
//
//		if (resultReceiver != null) {
//			result.putExtra(ServiceIntentKey.RESULT_RECEIVER.name(), resultReceiver);
//		}
//
//		return result;
//	}
//
//	private Intent buildUpdateItem() {
//		if (newsType == null) {
//			throw new NullPointerException("newsType is null.");
//		}
//
//		if (itemId < 0) {
//			throw new IllegalArgumentException("itemId is negative.");
//		}
//
//		Intent result = new Intent(context, LentaService.class);
//		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
//		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_ITEM.name());
//		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
//		result.putExtra(ServiceIntentKey.ITEM_ID.name(), itemId);
//
//		return result;
//	}
//
//	private Intent buildUpdateItemFullTexts() {
//		if (newsType == null) {
//			throw new NullPointerException("newsType is null.");
//		}
//
//		if (itemId < 0) {
//			throw new IllegalArgumentException("itemId is negative.");
//		}
//
//		Intent result = new Intent(context, LentaService.class);
//		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
//		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_ITEM_FULL_TEXTS.name());
//		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
//		result.putExtra(ServiceIntentKey.ITEM_ID.name(), itemId);
//
//		return result;
//	}
//
//	private Intent buildUpdateItemImages() {
//		if (newsType == null) {
//			throw new NullPointerException("newsType is null.");
//		}
//
//		if (itemId < 0) {
//			throw new IllegalArgumentException("itemId is negative.");
//		}
//
//		Intent result = new Intent(context, LentaService.class);
//		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
//		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_ITEM_IMAGES.name());
//		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
//		result.putExtra(ServiceIntentKey.ITEM_ID.name(), itemId);
//
//		return result;
//	}
}
