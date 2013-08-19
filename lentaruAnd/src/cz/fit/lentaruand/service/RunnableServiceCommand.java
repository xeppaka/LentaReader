package cz.fit.lentaruand.service;

import android.os.Bundle;
import android.os.ResultReceiver;

public abstract class RunnableServiceCommand implements ServiceCommand {
	private ResultReceiver resultReceiver;
	
	public RunnableServiceCommand(ResultReceiver resultReceiver) {
		this.resultReceiver = resultReceiver;
	}

	@Override
	public void run() {
		try {
			execute();
		} catch (Exception e) {
			resultReceiver.send(ServiceResult.ERROR.ordinal(), prepareExceptionResult(e));
		}
		
		resultReceiver.send(ServiceResult.SUCCESS.ordinal(), prepareResult());
	}
	
	protected abstract Bundle prepareResult();
	
	protected Bundle prepareExceptionResult(Exception e) {
		Bundle bundle = new Bundle();
		bundle.putSerializable(BundleConstants.KEY_EXCEPTION, e);
		
		return bundle;
	}
}
