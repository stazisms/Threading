package goon.core.threading;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

// a wrapper for a repeating task that makes it easier to manage
public class ScheduledTask {

    private ScheduledFuture<?> future;
    private final Runnable task;
    private final long initialDelay;
    private final long period;
    private final TimeUnit unit;

    public ScheduledTask(Runnable task, long initialDelay, long period, TimeUnit unit) {
        this.task = task;
        this.initialDelay = initialDelay;
        this.period = period;
        this.unit = unit;
    }

    // starts the repeating task
    public void start() {
        if (future == null || future.isDone()) {
            future = CThread.runRepeating(task, initialDelay, period, unit);
        }
    }

    // stops the repeating task
    public void stop() {
        if (future != null && !future.isDone()) {
            future.cancel(true); // true to interrupt the running task
        }
    }

    // restarts the task
    public void restart() {
        stop();
        start();
    }

    // checks if the task is currently scheduled to run
    public boolean isRunning() {
        return future != null && !future.isDone();
    }
}
