package goon.core.threading;

import goon.core.Utils.CthreadSupport;
import java.util.concurrent.*;

// for easy and efficient thread management
public final class CThread {

    private static final ExecutorService SHARED_EXECUTOR = new ThreadPoolBuilder()
            .name("CThread-Shared")
            .build();

    private static final ScheduledExecutorService SHARED_SCHEDULER = new ThreadPoolBuilder()
            .name("CThread-Scheduler")
            .corePoolSize(4)
            .buildScheduled();

    private CThread() {}

    // makes a new builder for a custom thread pool
    // use this for demanding tasks that need their own pool
    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    // quickly makes and starts a named thread. good for long tasks
    public static Thread newThread(Runnable runnable, String name) {
        Thread thread = new Thread(runnable);
        thread.setName(name);
        thread.start();
        return thread;
    }

    public static void RegisterThreads() {
        CthreadSupport.register();
    }

    // submits a task to the shared pool
    public static Future<?> submit(Runnable task) {
        return SHARED_EXECUTOR.submit(task);
    }

    // submits a task that returns a value to the shared pool
    public static <T> Future<T> submit(Callable<T> task) {
        return SHARED_EXECUTOR.submit(task);
    }

    // runs a task after a delay
    public static ScheduledFuture<?> runLater(Runnable task, long delay, TimeUnit unit) {
        return SHARED_SCHEDULER.schedule(task, delay, unit);
    }

    // runs a task on repeat
    public static ScheduledFuture<?> runRepeating(Runnable task, long initialDelay, long period, TimeUnit unit) {
        return SHARED_SCHEDULER.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    // shuts down all shared thread pools
    // call this when your app is closing
    public static void shutdown() {
        shutdown(SHARED_EXECUTOR);
        shutdown(SHARED_SCHEDULER);
    }

    // helper to gracefully shut down a thread pool
    public static void shutdown(ExecutorService executor) {
        try {
            executor.shutdown();
            if (!executor.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }
}
