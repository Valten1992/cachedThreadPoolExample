import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class CustomThreadPoolExecutor extends ThreadPoolExecutor {
	
	public CustomThreadPoolExecutor(int corePoolSize,
			int maximumPoolSize, long keepAliveTime, TimeUnit unit,
			BlockingQueue<Runnable> workQueue) {
		super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue);
	}

	@Override
	public void execute(Runnable runnable) {
		
		super.execute(runnable);
	}

	@Override
	protected void afterExecute(Runnable runnable, Throwable throwable) {
		super.afterExecute(runnable, throwable);
		synchronized (this) {
			int threads = getMaximumPoolSize();

			//if an error occurs (exception thrown) decrease the maximuum amount of threads
			if (throwable != null) {
				setMaximumPoolSize(threads - 1);
				System.out.println("Decrease! Current amount of maximum threads is "
						+ getMaximumPoolSize());
			} else {
				//if the task executes successfully, increase the thread pool size
				setMaximumPoolSize(threads + 1);
				System.out.println("Increase! Current amount of maximum threads is "
						+ getMaximumPoolSize());
			}
		}
	}

}