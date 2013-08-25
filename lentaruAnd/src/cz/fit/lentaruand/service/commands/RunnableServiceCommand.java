package cz.fit.lentaruand.service.commands;

import android.os.Bundle;
import android.os.ResultReceiver;
import android.util.Log;
import cz.fit.lentaruand.service.BundleConstants;
import cz.fit.lentaruand.service.ServiceResult;
import cz.fit.lentaruand.utils.LentaConstants;

public abstract class RunnableServiceCommand implements ServiceCommand {
	private ResultReceiver resultReceiver;
	private int requestId;
	private long creationTime;
	private boolean reportError;

	public RunnableServiceCommand(int requestId, ResultReceiver resultReceiver, boolean reportError) {
		if (requestId < NO_REQUEST_ID) {
			throw new IllegalArgumentException("requestId is invalid. Use -1 if there is no request id.");
		}
		
		if (resultReceiver == null) {
			throw new NullPointerException("resultReceiver is null.");
		}
		
		creationTime = System.currentTimeMillis();
		
		this.requestId = requestId;
		this.resultReceiver = resultReceiver;
	}
	
	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			Log.d(LentaConstants.LoggerServiceTag, "Exception occured during running the command " + getClass().getSimpleName(), e);
			
			if (resultReceiver != null && reportError()) {
				resultReceiver.send(ServiceResult.ERROR.ordinal(), prepareExceptionResult(e));
			}
		}
		
		if (resultReceiver != null) {
			Bundle result = getResult();
			
			if (result != null) {
				resultReceiver.send(ServiceResult.SUCCESS.ordinal(), result);
			}
		}
	}
	
	protected abstract Bundle getResult();
	
	protected Bundle prepareExceptionResult(Exception e) {
		Bundle bundle = new Bundle();
		bundle.putInt(BundleConstants.KEY_REQUEST_ID.name(), getRequestId());
		bundle.putSerializable(BundleConstants.KEY_EXCEPTION.name(), e);
		
		return bundle;
	}

	@Override
	public int compareTo(ServiceCommand otherCommand) {
		long time1 = getCreationTime();
		long time2 = otherCommand.getCreationTime();
		
		if (time1 < time2) {
			return 1;
		}
		
		if (time1 > time2) {
			return -1;
		}
		
		return 0;
	}

	@Override
	public long getCreationTime() {
		return creationTime;
	}
	
	@Override
	public int getRequestId() {
		return requestId;
	}

	protected ResultReceiver getResultReceiver() {
		return resultReceiver;
	}
	
	protected boolean reportError() {
		return reportError;
	}
}
