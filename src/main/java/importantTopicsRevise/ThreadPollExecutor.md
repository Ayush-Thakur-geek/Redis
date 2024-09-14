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
