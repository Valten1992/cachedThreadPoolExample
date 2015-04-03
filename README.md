# cachedThreadPoolExample

This project is an example of parallel processing through the usage of Cached Thread Pools. 
Say we were passing in a list of keywords to a web service that returns data based on that keyword. 
If we had several thousand keywords, that would be a costly and long process to iterate through them. 
Loading all of them at the same time is not a viable solution either as it may not scale well with the sheer amount of 
HTTP POST requests that are made. 

Therefore, we use a Cached Thread Pool with Executors. Callable threads are created, each containing the data for one POST 
request and takes one keyword as an argument. 
To ensure scalability, we define a maximum amount of threads allowed at one time. If a Callable thread returns the string 
value of successful HTTP response, we increment the amount of threads as the application can handle that level of 
simultaneous requests. Likewise, if a thread fails (returns null or times out), we reduce the amount of threads allowed to
ensure overall performance does not suffer.
