package com.xeppaka.lentareader.service.commands;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.ResultReceiver;
import android.util.Log;

import com.xeppaka.lentareader.service.LentaService;
import com.xeppaka.lentareader.service.commands.exceptions.NoInternetConnectionException;
import com.xeppaka.lentareader.utils.LentaConstants;

public abstract class RunnableServiceCommand implements ServiceCommand {
    private Context context;
	private int requestId;
    private ResultReceiver resultReceiver;
	private long creationTime;
    private boolean internetConnectionRequired;

	public RunnableServiceCommand(Context context, int requestId, ResultReceiver resultReceiver, boolean internetConnectionRequired) {
		creationTime = System.currentTimeMillis();

        this.context = context;
		this.requestId = requestId;
		this.resultReceiver = resultReceiver;
        this.internetConnectionRequired = internetConnectionRequired;
	}

    public RunnableServiceCommand(Context context, int requestId, ResultReceiver resultReceiver) {
        this(context, requestId, resultReceiver, false);
    }

	@Override
	public void run() {
        if (LentaConstants.DEVELOPER_MODE) {
            Log.d(LentaConstants.LoggerServiceTag, "Command started: " + getClass().getSimpleName());
        }

		try {
            if (internetConnectionRequired) {
                checkInternetConnection();
            }

			execute();

            if (resultReceiver != null) {
                resultReceiver.send(requestId, LentaService.resultSuccess);
            }

            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerServiceTag, "Command finished successfuly: " + getClass().getSimpleName());
            }
        } catch (Exception e) {
            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerServiceTag, "Exception occured during running the command " + getClass().getSimpleName(), e);
            }
			
            resultReceiver.send(requestId, LentaService.createFailResult(e));

            if (LentaConstants.DEVELOPER_MODE) {
                Log.d(LentaConstants.LoggerServiceTag, "Command finished with exception: " + getClass().getSimpleName());
            }
		}
	}

    private void checkInternetConnection() throws NoInternetConnectionException {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();

        if (netInfo == null || !netInfo.isConnected()) {
            throw new NoInternetConnectionException();
        }
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
