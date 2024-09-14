# <u> What is ThreadPool: </u>

### - It's a collection of threads (aka workers), which are available to perform submitted tasks.
### - Once task completed, worker thread goes back to the pool and waits for another task.
### - Means threads can be reused, which is more efficient than creating a new thread for each task.

![ThreadPool Diagram](https://www.scientecheasy.com/wp-content/uploads/2020/08/java-thread-pool.png)

## <i> Advantages of ThreadPool: </i>

### - <b><u> Better Performance: </u></b> Threads are reused, so no need to create a new thread for each task.
### - <b><u> Better Resource Management: </u></b> Limit the number of threads, which prevents the system from running out of resources.
### - <b><u> Better Scalability: </u></b> Can adjust the number of threads in the pool based on the workload.
### - <b><u> Better Monitoring: </u></b> Can monitor the status of the thread pool, like the number of active threads, tasks completed, etc.
### - <b><u> Better Control: </u></b> Can control the number of threads in the pool, the size of the queue, etc.