package cz.fit.lentaruand.service;

import java.util.Comparator;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriorityExecutor extends ThreadPoolExecutor {

	/**
	 * дефолтный конструктор, создает threadPoolExecutor. 
	 * corePoolSize - 0
	 * maximumPoolSize - integer.maxValue
	 * keepAliveTime - 60L seconds
	 * workQueue - PriorityBlockingQueue (initialCapacity, Comparator)
	 * 
	 */
	
	public PriorityExecutor() {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>(11,
						new PriorityTaskComparator()));
	}

	public PriorityExecutor(final ThreadFactory threadFactory) {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>(11,
						new PriorityTaskComparator()), threadFactory);
	}

	public PriorityExecutor(final RejectedExecutionHandler handler) {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>(11,
						new PriorityTaskComparator()), handler);
	}

	public PriorityExecutor(final ThreadFactory threadFactory,
			final RejectedExecutionHandler handler) {
		super(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS,
				new PriorityBlockingQueue<Runnable>(11,
						new PriorityTaskComparator()), threadFactory, handler);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Callable<T> callable) {
		if (callable instanceof Important)
			return new PriorityTask<T>(((Important) callable).getPriority(),
					callable);
		else
			return new PriorityTask<T>(0, callable);
	}

	@Override
	protected <T> RunnableFuture<T> newTaskFor(final Runnable runnable,
			final T value) {
		if (runnable instanceof Important)
			return new PriorityTask<T>(((Important) runnable).getPriority(),
					runnable, value);
		else
			return new PriorityTask<T>(0, runnable, value);
	}

	public interface Important {
		int getPriority();
	}

	private static final class PriorityTask<T> extends FutureTask<T> implements
			Comparable<PriorityTask<T>> {
		private final int priority;

		public PriorityTask(final int priority, final Callable<T> tCallable) {
			super(tCallable);

			this.priority = priority;
		}

		public PriorityTask(final int priority, final Runnable runnable,
				final T result) {
			super(runnable, result);

			this.priority = priority;
		}

		@Override
		public int compareTo(final PriorityTask<T> o) {
			final long diff = o.priority - priority;
			return 0 == diff ? 0 : 0 > diff ? -1 : 1;
		}
	}

	private static class PriorityTaskComparator implements Comparator<Runnable> {
		@Override
		public int compare(final Runnable left, final Runnable right) {
			return ((PriorityTask) left).compareTo((PriorityTask) right);
		}
	}
}