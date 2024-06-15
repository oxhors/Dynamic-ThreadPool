package org.wx.TheadPool;

import java.beans.PropertyVetoException;
import java.util.concurrent.*;

public class wxThreadPool {
    private static final int CPU = Runtime.getRuntime().availableProcessors();
    private static final int MAX_POOL_SIZE = CPU * 2;
    private static final int QUEUE_SIZE = 1024;
    private static final long KEEP_TIME = 1000 * 60;
    private static final TimeUnit KEEP_TIME_UNIT = TimeUnit.MILLISECONDS;
    private final ExecutorService executorService = new ThreadPoolExecutor(CPU,MAX_POOL_SIZE,KEEP_TIME,TimeUnit.SECONDS,new ArrayBlockingQueue<>(QUEUE_SIZE));

    private final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(CPU, new RejectedExecutionHandler() {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            int retryTimes = 3;
            while (retryTimes -- > 0){
                try {
                    boolean offer = executor.getQueue().offer(r, 5, TimeUnit.SECONDS);
                    if(offer) break;
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    public void execute(Runnable r,boolean now){
        if(now){
            r.run();
        }else {
            executorService.execute(r);
        }
    }

    public <T> T submit(Callable<T> task,boolean now){
        if(now){
            try {
                return task.call();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            Future<T> submit = executorService.submit(task);
            try {
                return submit.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void execute(Runnable r,long initial,long period,TimeUnit timeUnit){
        scheduledExecutorService.scheduleAtFixedRate(r,initial,period,timeUnit);
    }



    public void stop() {
        executorService.shutdown();
        scheduledExecutorService.shutdown();
    }
}
