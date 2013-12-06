package com.xeppaka.lentareader.service.commands;

@Deprecated
public class RetrieveNewsImageServiceCommand /*extends RetrieveImageServiceCommand<News>*/ {
//	private Bundle result;
//
//	public RetrieveNewsImageServiceCommand(int requestId, News newsObject,
//			ContentResolver contentResolver, ResultReceiver resultReceiver,
//			boolean reportError) {
//		super(requestId, newsObject, contentResolver, resultReceiver, reportError);
//	}
//
//	@Override
//	public void execute() throws Exception {
//		Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());
//
//		AsyncDao<News> newsDao = NewsDao.getInstance(getContentResolver());
//
//		News news = getNewsObject();
//
//		if (news == null) {
//			news = newsDao.read(getNewsId());
//		}
//
//		if (news != null) {
//			String imageLink = news.getImageLink();
//
//			if (imageLink != null && !TextUtils.isEmpty(imageLink)) {
//				try {
//					Bitmap newBitmap = LentaHttpImageDownloader.downloadBitmap(imageLink);
//					news.setImage(new StrongBitmapReference(newBitmap));
//
//					newsDao.update(news);
//
//					Log.d(LentaConstants.LoggerServiceTag, "Successfuly downloaded and saved image: " + imageLink);
//
//					prepareResultUpdated(news.getId());
//				} catch (HttpStatusCodeException e) {
//					Log.e(LentaConstants.LoggerServiceTag,
//							"Error loading image from URL: " + imageLink, e);
//					throw e;
//				} catch (IOException e) {
//					Log.e(LentaConstants.LoggerServiceTag,
//							"Error loading image from URL: " + imageLink, e);
//					throw e;
//				}
//			} else {
//				throw new ImageUpdateException("Image link is empty for news with guid " + news.getGuid());
//			}
//		} else {
//			throw new ImageUpdateException("Cannot find in database news with guid " + getNewsId());
//		}
//	}
//
//	private void prepareResultUpdated(long id) {
//		result = new Bundle();
//		result.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
//		result.putString(BundleConstants.KEY_ACTION.name(), ServiceResultAction.IMAGE_UPDATED.name());
//		result.putString(BundleConstants.KEY_NEWS_TYPE.name(), NewsType.NEWS.name());
//		result.putLongArray(BundleConstants.KEY_IDS.name(), new long[]{id});
//	}
//
//	@Override
//	protected Bundle getResult() {
//		return result;
//	}
}
