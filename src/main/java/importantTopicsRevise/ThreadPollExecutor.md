# <u>What is ThreadPoolExecutor:</u>

![Executor Framework](https://www.logicbig.com/tutorials/core-java-tutorial/java-multi-threading/executor-framework/images/executor-framework-classes.png)

### - ThreadPoolExecutor is a class that provides an extensible thread pool framework. It is a concrete implementation of ExecutorService. It allows you to create a pool of threads and reuse them. It also provides a way to manage the pool size and the queue of tasks that the threads will execute.

## <i> Arguments of ThreadPoolExecutor </i>:

### <b> corePoolSize: </b> The number of threads to keep in the pool, even if they are idle, unless allowCoreThreadTimeOut is set.
### <b> maximumPoolSize: </b> The maximum number of threads to allow in the pool.
### <b> keepAliveTime: </b> When the number of threads is greater than the core, this is the maximum time that excess idle threads will wait for new tasks before terminating.
### <b> unit: </b> The time unit for the keepAliveTime argument.
### <b> blockingQueue: </b> The queue to use for holding tasks before they are executed. This queue will hold only the Runnable tasks submitted by the execute method.
####  -<b><i><u> Bounded Queue: </u></b></i> Queue with fixed size e.g. ArrayBlockingQueue.
####  -<b><i><u> Unbounded Queue: </u></b></i> Queue with no size limit e.g. LinkedBlockingQueue.
### <b> threadFactory: </b> The factory to use when the executor creates a new thread.
### <b> rejectedExecutionHandler: </b> The handler to use when execution is blocked because the thread bounds and queue capacities are reached.
####  -<b><i><u> new ThreadPoolExecutor.AbortPolicy: </u></b></i> Throws RejectedExecutionException.
####  -<b><i><u> new ThreadPoolExecutor.CallerRunsPolicy: </u></b></i> Executes the task in the caller's thread.
####  -<b><i><u> new ThreadPoolExecutor.DiscardPolicy: </u></b></i> Discards the task silently.
####  -<b><i><u> new ThreadPoolExecutor.DiscardOldestPolicy: </u></b></i> Discards the oldest task and adds the new task to the queue.

## <i> Methods of ThreadPoolExecutor </i>:

### <b> execute(Runnable command): </b> Executes the given task sometime in the future.
### <b> submit(Callable<T> task): </b> Submits a value-returning task for execution and returns a Future representing the pending results of the task.
### <b> shutdown(): </b> Initiates an orderly shutdown in which previously submitted tasks are executed, but no new tasks will be accepted.
### <b> shutdownNow(): </b> Attempts to stop all actively executing tasks, halts the processing of waiting tasks, and returns a list of the tasks that were waiting to be executed.
### <b> isShutdown(): </b> Returns true if this executor has been shut down.
### <b> isTerminated(): </b> Returns true if all tasks have completed following shut down.
### <b> awaitTermination(long timeout, TimeUnit unit): </b> Blocks until all tasks have completed execution after a shutdown request, or the timeout occurs, or the current thread is interrupted, whichever happens first.
### <b> setCorePoolSize(int corePoolSize): </b> Sets the core number of threads.
### <b> setMaximumPoolSize(int maximumPoolSize): </b> Sets the maximum allowed number of threads.
### <b> setKeepAliveTime(long time, TimeUnit unit): </b> Sets the time limit for which threads may remain idle before being terminated.
### <b> setThreadFactory(ThreadFactory threadFactory): </b> Sets the thread factory used to create new threads.
### <b> setRejectedExecutionHandler(RejectedExecutionHandler handler): </b> Sets the handler for tasks that cannot be executed by the thread pool.
