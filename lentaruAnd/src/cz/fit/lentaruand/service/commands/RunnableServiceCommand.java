package cz.fit.lentaruand.service.commands;

import cz.fit.lentaruand.service.BundleConstants;
import cz.fit.lentaruand.service.ServiceResult;
import android.os.Bundle;
import android.os.ResultReceiver;

public abstract class RunnableServiceCommand implements ServiceCommand {
	private ResultReceiver resultReceiver;
	private long creationTime;
	
	public RunnableServiceCommand(ResultReceiver resultReceiver) {
		creationTime = System.currentTimeMillis();
		this.resultReceiver = resultReceiver;
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			if (resultReceiver != null) {
				resultReceiver.send(ServiceResult.ERROR.ordinal(), prepareExceptionResult(e));
			}
		}
		
		if (resultReceiver != null) {
			resultReceiver.send(ServiceResult.SUCCESS.ordinal(), prepareResult());
		}
	}
	
	protected abstract Bundle prepareResult();
	
	protected Bundle prepareExceptionResult(Exception e) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(BundleConstants.KEY_EXCEPTION, e);
		
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
	
	protected ResultReceiver getResultReceiver() {
		return resultReceiver;
	}
}
