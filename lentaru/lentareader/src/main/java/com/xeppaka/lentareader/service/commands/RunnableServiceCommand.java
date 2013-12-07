package com.xeppaka.lentareader.service.commands;

import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.service.LentaService;
import com.xeppaka.lentareader.utils.LentaConstants;

public abstract class RunnableServiceCommand implements ServiceCommand {
	private ResultReceiver resultReceiver;
	private int requestId;
	private long creationTime;

	public RunnableServiceCommand(int requestId, ResultReceiver resultReceiver) {
		creationTime = System.currentTimeMillis();
		
		this.requestId = requestId;
		this.resultReceiver = resultReceiver;
	}
	
	@Override
	public void run() {
		Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());

		try {
			execute();
		} catch (Exception e) {
			Log.d(LentaConstants.LoggerServiceTag, "Exception occured during running the command " + getClass().getSimpleName(), e);
			
            resultReceiver.send(requestId, LentaService.resultFail);

			Log.d(LentaConstants.LoggerServiceTag, "Command finished with exception: " + getClass().getSimpleName());
			return;
		}
		
		if (resultReceiver != null) {
            resultReceiver.send(requestId, LentaService.resultSuccess);
		}
		
		Log.d(LentaConstants.LoggerServiceTag, "Command finished successfuly: " + getClass().getSimpleName());
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
	
	@Override
	public String getCommandName() {
		return getClass().getSimpleName();
	}
}
