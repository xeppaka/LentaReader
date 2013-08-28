package cz.fit.lentaruand.service.commands;

public interface ServiceCommand extends Runnable, Comparable<ServiceCommand> {
	public static final int NO_REQUEST_ID = -1;
	
	void execute() throws Exception;
	long getCreationTime();
	int getRequestId();
	String getCommandName();
}
