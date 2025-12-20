package goon.core.threading;

import goon.core.Utils.NamedThreadFactory;

import java.util.concurrent.*;

// builder for making custom ExecutorService instances
// gives you more control over the thread pool
public class ThreadPoolBuilder {

    private int corePoolSize = 1;
    private int maxPoolSize = Integer.MAX_VALUE;
    private long keepAliveTime = 60L;
    private TimeUnit unit = TimeUnit.SECONDS;
    private String name = "CustomPool";

    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolBuilder maxPoolSize(int maxPoolSize) {
        this.maxPoolSize = maxPoolSize;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime, TimeUnit unit) {
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        return this;
    }

    public ThreadPoolBuilder name(String name) {
        this.name = name;
        return this;
    }

    // builds a standard, flexible thread pool
    public ExecutorService build() {
        return new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                unit,
                new SynchronousQueue<>(),
                new NamedThreadFactory(name)
        );
    }

    // builds a scheduled thread pool for delayed or periodic tasks
    public ScheduledExecutorService buildScheduled() {
        return new ScheduledThreadPoolExecutor(
                corePoolSize,
                new NamedThreadFactory(name)
        );
    }
}
