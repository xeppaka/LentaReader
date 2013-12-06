package com.xeppaka.lentareader.service.commands;

public interface ServiceCommand extends Runnable, Comparable<ServiceCommand> {
	void execute() throws Exception;
	long getCreationTime();
	int getRequestId();
	String getCommandName();
}
