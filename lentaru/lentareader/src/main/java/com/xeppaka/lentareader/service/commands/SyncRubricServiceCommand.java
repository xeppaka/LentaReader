package com.xeppaka.lentareader.service.commands;

@Deprecated
public class SyncRubricServiceCommand /*extends RunnableServiceCommand*/ {
//	private Rubrics rubric;
//	private NewsType newsType;
//	private ExecutorService executor;
//	private ContentResolver contentResolver;
//
//	public SyncRubricServiceCommand(int requestId, Rubrics rubric, NewsType newsType, ExecutorService executor, ContentResolver contentResolver,
//			ResultReceiver resultReceiver) {
//		super(requestId, resultReceiver);
//
//		this.rubric = rubric;
//		this.newsType = newsType;
//		this.executor = executor;
//		this.contentResolver = contentResolver;
//	}
//
//	@Override
//	public void execute() throws Exception {
//		switch (newsType) {
//		case NEWS:
//			syncNews(rubric);
//			break;
//		default:
//			throw new UnsupportedOperationException("sync is unsupported for news of type: " + newsType.name() + " by now.");
//		}
//	}
//
//	private void syncNews(Rubrics rubric) {
//		AsyncNODao<News> newsDao = NewsDao.getInstance(contentResolver);
//		Collection<News> news = newsDao.read(rubric);
//
//		for (News newsObject : news) {
//			executor.execute(new SyncNewsServiceCommand(getRequestId(), newsObject, contentResolver, executor, getResultReceiver(), false));
//		}
//	}
}
