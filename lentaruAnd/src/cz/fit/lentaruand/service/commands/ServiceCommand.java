package cz.fit.lentaruand.service.commands;


public interface ServiceCommand extends Runnable, Comparable<ServiceCommand> {
	void execute() throws Exception;
	long getCreationTime();
}
