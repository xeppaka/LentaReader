package com.xeppaka.lentareader.service.commands;

@Deprecated
public class SyncNewsServiceCommand /*extends RunnableServiceCommand*/ {
//	private ContentResolver contentResolver;
//	private ExecutorService executor;
//	private News news;
//	private long newsId;
//
//	public SyncNewsServiceCommand(int requestId, long newsId, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver, boolean reportError) {
//		super(requestId, resultReceiver);
//
//		if (newsId < 0) {
//			throw new NullPointerException("newsId is negative.");
//		}
//
//		this.newsId = newsId;
//		this.contentResolver = contentResolver;
//		this.executor = executor;
//	}
//
//	public SyncNewsServiceCommand(int requestId, News news, ContentResolver contentResolver, ExecutorService executor, ResultReceiver resultReceiver, boolean reportError) {
//		super(requestId, resultReceiver);
//
//		this.contentResolver = contentResolver;
//		this.executor = executor;
//		this.news = news;
//	}
//
//	@Override
//	public void execute() throws Exception {
//		AsyncNODao<News> newsDao = NewsDao.getInstance(contentResolver);
//
//		if (news == null) {
//			news = newsDao.read(newsId);
//		}
//
//		if (news != null) {
//			ImageDaoOld imageDao = ImageDaoOld.getInstance(contentResolver);
//
//			String imageLink = news.getImageLink();
//			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
//				if (!imageDao.imageExist(imageLink)) {
//					executor.execute(new RetrieveNewsImageServiceCommand(getRequestId(), news, contentResolver, getResultReceiver()));
//				}
//			}
//
//			if (news.getBody() == null) {
//				executor.execute(new RetrieveNewsFullTextServiceCommand(getRequestId(), news, contentResolver, getResultReceiver()));
//			}
//		}
//	}
//
//	@Override
//	protected Bundle getResult() {
//		return null;
//	}
}