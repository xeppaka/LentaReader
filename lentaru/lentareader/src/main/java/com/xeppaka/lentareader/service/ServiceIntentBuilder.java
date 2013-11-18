package com.xeppaka.lentareader.service;

import android.content.Context;
import android.content.Intent;
import android.os.ResultReceiver;

import com.xeppaka.lentareader.data.NewsType;
import com.xeppaka.lentareader.data.Rubrics;

public class ServiceIntentBuilder {
	public static final int NO_ITEM_ID = -1;
	public static final int NO_REQUEST_ID = -1;
	
	private Context context;
	private ServiceAction action;
	private NewsType newsType;
	private long itemId;
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
		if (context == null) {
			throw new NullPointerException("context is null.");
		}
		
		itemId = NO_ITEM_ID;
		this.requestId = requestId;
		this.context = context;
	}
	
	public ServiceIntentBuilder forAction(ServiceAction action) {
		if (action == null) {
			throw new NullPointerException("action is null.");
		}
		
		this.action = action;
		return this;
	}
	
	public ServiceIntentBuilder forNewsType(NewsType newsType) {
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}
		
		this.newsType = newsType;
		return this;
	}
	
	public ServiceIntentBuilder forRubric(Rubrics rubric) {
		if (rubric == null) {
			throw new NullPointerException("rubric is null.");
		}
		
		this.rubric = rubric;
		return this;
	}
	
	public ServiceIntentBuilder forItemId(long itemId) {
		if (itemId < 0) {
			throw new IllegalArgumentException("itemId is negative.");
		}
		
		this.itemId = itemId;
		return this;
	}
	
	public ServiceIntentBuilder forResultReceiver(ResultReceiver resultReceiver) {
		if (resultReceiver == null) {
			throw new NullPointerException("resultReceiver is null.");
		}
		
		this.resultReceiver = resultReceiver;
		
		return this;
	}
	
	public Intent build() {
		 if (action == null) {
			 throw new NullPointerException("action is null.");
		 }
		 
		 switch (action) {
		 case UPDATE_RUBRIC:
			 return buildUpdateRubric();
		 case UPDATE_ITEM:
			 return buildUpdateItem();
		 case UPDATE_ITEM_FULL_TEXTS:
			 return buildUpdateItemFullTexts();
		 case UPDATE_ITEM_IMAGES:
			 return buildUpdateItemImages();
		case SYNC_RUBRIC:
			 return buildSyncRubric();
		default:
			break;
		 }
		 
		 throw new AssertionError();
	}
	
	private Intent buildUpdateRubric() {
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}

		if (rubric == null) {
			throw new NullPointerException("rubric is null.");
		}
		
		Intent result = new Intent(context, LentaService.class);
		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_RUBRIC.name());
		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
		result.putExtra(ServiceIntentKey.RUBRIC.name(), rubric.name());

		if (resultReceiver != null) {
			result.putExtra(ServiceIntentKey.RESULT_RECEIVER.name(), resultReceiver);
		}
		
		return result;
	}
	
	private Intent buildSyncRubric() {
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}

		if (rubric == null) {
			throw new NullPointerException("rubric is null.");
		}
		
		Intent result = new Intent(context, LentaService.class);
		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.SYNC_RUBRIC.name());
		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
		result.putExtra(ServiceIntentKey.RUBRIC.name(), rubric.name());

		if (resultReceiver != null) {
			result.putExtra(ServiceIntentKey.RESULT_RECEIVER.name(), resultReceiver);
		}
		
		return result;
	}
	
	private Intent buildUpdateItem() {
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}
		
		if (itemId < 0) {
			throw new IllegalArgumentException("itemId is negative.");
		}
		
		Intent result = new Intent(context, LentaService.class);
		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_ITEM.name());
		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
		result.putExtra(ServiceIntentKey.ITEM_ID.name(), itemId);
		
		return result;
	}
	
	private Intent buildUpdateItemFullTexts() {
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}
		
		if (itemId < 0) {
			throw new IllegalArgumentException("itemId is negative.");
		}
		
		Intent result = new Intent(context, LentaService.class);
		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_ITEM_FULL_TEXTS.name());
		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
		result.putExtra(ServiceIntentKey.ITEM_ID.name(), itemId);
		
		return result;
	}
	
	private Intent buildUpdateItemImages() {
		if (newsType == null) {
			throw new NullPointerException("newsType is null.");
		}
		
		if (itemId < 0) {
			throw new IllegalArgumentException("itemId is negative.");
		}
		
		Intent result = new Intent(context, LentaService.class);
		result.putExtra(ServiceIntentKey.REQUEST_ID.name(), requestId);
		result.putExtra(ServiceIntentKey.SERVICE_ACTION.name(), ServiceAction.UPDATE_ITEM_IMAGES.name());
		result.putExtra(ServiceIntentKey.NEWS_TYPE.name(), newsType.name());
		result.putExtra(ServiceIntentKey.ITEM_ID.name(), itemId);
		
		return result;
	}
}
