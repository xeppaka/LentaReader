package cz.fit.lentaruand.service;

import android.os.Bundle;
import android.os.ResultReceiver;
import cz.fit.lentaruand.data.Rubrics;

public class RubricUpdateServiceCommand extends RunnableServiceCommand {
	private Processor processor;
	private Rubrics rubric;
	private ResultReceiver receiver;

	public RubricUpdateServiceCommand(Processor processor, ResultReceiver receiver, Rubrics rubric) {
		super(receiver);
		
		this.processor = processor;
		this.rubric = rubric;
	}

	@Override
	public void execute() {
		processor.downloadRubricBrief(rubric, receiver);
	}

	@Override
	protected Bundle prepareResult() {
		// prepare result of the rubric update operation here.
		
		return null;
	}
}
